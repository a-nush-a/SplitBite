package com.splitbite.controller;

import com.splitbite.dto.AnalyticsResponse;
import com.splitbite.dto.SettlementDto;
import com.splitbite.exception.ResourceNotFoundException;
import com.splitbite.model.Bill;
import com.splitbite.model.Participant;
import com.splitbite.repository.BillRepository;
import com.splitbite.repository.ParticipantRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/settlements")
@CrossOrigin(origins = "*")
@Tag(name = "Settlements & Settle Up", description = "Endpoints for debt tracking, recording payments (Cash, UPI, Transfer), and analytics")
public class SettlementController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @GetMapping("/{billId}")
    @Operation(summary = "Get debt ledger for a bill", description = "Returns debt summary of who owes what to the host and settlement status")
    public ResponseEntity<Map<String, Object>> getSettlementSheet(@PathVariable Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        String paidBy = bill.getPaidBy() != null && !bill.getPaidBy().isEmpty()
                ? bill.getPaidBy()
                : "Host";

        List<SettlementDto> settlements = bill.getParticipants().stream().map(p -> new SettlementDto(
                p.getId(),
                p.getName(),
                p.getFinalAmount(),
                p.isPaid(),
                p.getPaymentMethod(),
                p.getSettledAt()
        )).collect(Collectors.toList());

        BigDecimal totalSettled = bill.getParticipants().stream()
                .filter(Participant::isPaid)
                .map(Participant::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPending = bill.getTotalAmount().subtract(totalSettled);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("billId", bill.getId());
        response.put("restaurantName", bill.getRestaurantName());
        response.put("paidBy", paidBy);
        response.put("totalBillAmount", bill.getTotalAmount());
        response.put("totalSettledAmount", totalSettled);
        response.put("totalPendingAmount", totalPending.max(BigDecimal.ZERO));
        response.put("participantsCount", bill.getParticipants().size());
        response.put("settlements", settlements);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{billId}/participants/{participantId}/settle")
    @Operation(summary = "Record payment / Settle debt", description = "Records settlement with payment method (Cash, UPI, Bank Transfer)")
    public ResponseEntity<SettlementDto> recordSettlement(
            @PathVariable Long billId,
            @PathVariable Long participantId,
            @RequestParam(defaultValue = "Cash") String method) {

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));

        participant.setPaid(true);
        participant.setPaymentMethod(method);
        participant.setSettledAt(LocalDateTime.now());
        participantRepository.save(participant);

        SettlementDto dto = new SettlementDto(
                participant.getId(),
                participant.getName(),
                participant.getFinalAmount(),
                participant.isPaid(),
                participant.getPaymentMethod(),
                participant.getSettledAt()
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{billId}/participants/{participantId}/unsettle")
    @Operation(summary = "Reset settlement status", description = "Marks participant debt back to unsettled")
    public ResponseEntity<SettlementDto> resetSettlement(
            @PathVariable Long billId,
            @PathVariable Long participantId) {

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));

        participant.setPaid(false);
        participant.setPaymentMethod(null);
        participant.setSettledAt(null);
        participantRepository.save(participant);

        SettlementDto dto = new SettlementDto(
                participant.getId(),
                participant.getName(),
                participant.getFinalAmount(),
                participant.isPaid(),
                null,
                null
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get overall dining expense analytics", description = "Returns aggregated totals, settled vs pending ratio, and monthly breakdown")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        List<Bill> allBills = billRepository.findAll();

        long totalCount = allBills.size();
        BigDecimal totalSpent = allBills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Participant> allParticipants = participantRepository.findAll();
        BigDecimal settled = allParticipants.stream()
                .filter(Participant::isPaid)
                .map(Participant::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pending = totalSpent.subtract(settled).max(BigDecimal.ZERO);

        Map<String, BigDecimal> monthly = new LinkedHashMap<>();
        allBills.forEach(b -> {
            String monthKey = b.getCreatedAt() != null
                    ? b.getCreatedAt().getMonth().name().substring(0, 3) + " " + b.getCreatedAt().getYear()
                    : "Recent";
            monthly.put(monthKey, monthly.getOrDefault(monthKey, BigDecimal.ZERO).add(b.getTotalAmount()));
        });

        AnalyticsResponse response = new AnalyticsResponse(
                totalCount,
                totalSpent,
                settled,
                pending,
                allParticipants.size(),
                monthly
        );

        return ResponseEntity.ok(response);
    }
}
