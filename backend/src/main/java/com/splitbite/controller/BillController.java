package com.splitbite.controller;

import com.splitbite.dto.BillCalculationRequest;
import com.splitbite.dto.BillCalculationResponse;
import com.splitbite.exception.ResourceNotFoundException;
import com.splitbite.model.Bill;
import com.splitbite.repository.BillRepository;
import com.splitbite.service.BillCalculationService;
import com.splitbite.service.PdfExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
@Tag(name = "Bills & Calculations", description = "Endpoints for creating bills, calculating equal/individual splits, and PDF exports")
public class BillController {

    @Autowired
    private BillCalculationService billCalculationService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private BillRepository billRepository;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Reports service health status and API version")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("app", "SplitBite Backend");
        status.put("version", "1.0.0");
        status.put("security", "Spring Security + JWT Enabled");
        status.put("swagger", "Available at /swagger-ui/index.html");
        return ResponseEntity.ok(status);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate and save bill", description = "Calculates penny-accurate split with CGST, SGST, service charge, and generates dynamic UPI QR codes")
    public ResponseEntity<BillCalculationResponse> calculateBill(
            @RequestBody BillCalculationRequest request) {
        BillCalculationResponse response = billCalculationService.calculateAndSaveBill(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all past bills", description = "Returns past bills history ordered by most recent first")
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bill details by ID", description = "Returns full details of a specific bill including participants and dishes")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        return ResponseEntity.ok(bill);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bill by ID", description = "Removes bill record and cascades to participants and items")
    public ResponseEntity<Map<String, String>> deleteBill(@PathVariable Long id) {
        if (!billRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bill not found with id: " + id);
        }
        billRepository.deleteById(id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Bill with ID " + id + " successfully deleted.");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/pdf/individual")
    @Operation(summary = "Download individual participant PDF bill")
    public ResponseEntity<byte[]> downloadIndividualPdf(
            @RequestBody BillCalculationResponse billData,
            @RequestParam String participantId) {
        try {
            byte[] pdfBytes = pdfExportService.generateIndividualBillPdf(billData, participantId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "SplitBite_Individual_Bill.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/pdf/all")
    @Operation(summary = "Download master group PDF bill")
    public ResponseEntity<byte[]> downloadAllBillsPdf(
            @RequestBody BillCalculationResponse billData) {
        try {
            byte[] pdfBytes = pdfExportService.generateAllBillsPdf(billData);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "SplitBite_Group_Bill.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
