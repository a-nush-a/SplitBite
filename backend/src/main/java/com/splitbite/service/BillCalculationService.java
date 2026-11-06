package com.splitbite.service;

import com.splitbite.dto.BillCalculationRequest;
import com.splitbite.dto.BillCalculationResponse;
import com.splitbite.model.Bill;
import com.splitbite.model.FoodItem;
import com.splitbite.model.Participant;
import com.splitbite.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class BillCalculationService {

    @Autowired(required = false)
    private BillRepository billRepository;

    @Transactional
    public BillCalculationResponse calculateAndSaveBill(BillCalculationRequest request) {
        String restaurant = request.getRestaurantName() != null && !request.getRestaurantName().trim().isEmpty()
                ? request.getRestaurantName().trim()
                : "Restaurant Bill";
        String splitType = request.getSplitType() != null ? request.getSplitType().toUpperCase() : "EQUAL";
        List<BillCalculationRequest.FoodItemInput> foodItems = request.getFoodItems() != null ? request.getFoodItems() : Collections.emptyList();
        List<BillCalculationRequest.ParticipantInput> participants = request.getParticipants() != null ? request.getParticipants() : Collections.emptyList();
        Map<String, List<String>> assignments = request.getAssignments() != null ? request.getAssignments() : Collections.emptyMap();

        BigDecimal cgstPercent = request.getCgstPercent() != null ? request.getCgstPercent() : new BigDecimal("2.5");
        BigDecimal sgstPercent = request.getSgstPercent() != null ? request.getSgstPercent() : new BigDecimal("2.5");
        BigDecimal totalTaxPercent = cgstPercent.add(sgstPercent);

        BigDecimal serviceCharge = request.getServiceCharge() != null ? request.getServiceCharge() : BigDecimal.ZERO;
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        String paidBy = request.getPaidBy() != null && !request.getPaidBy().trim().isEmpty() ? request.getPaidBy().trim() : "Host";

        // 1. Calculate Food Subtotal
        BigDecimal foodSubtotal = BigDecimal.ZERO;
        for (BillCalculationRequest.FoodItemInput item : foodItems) {
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            int qty = item.getQuantity() != null && item.getQuantity() > 0 ? item.getQuantity() : 1;
            foodSubtotal = foodSubtotal.add(price.multiply(BigDecimal.valueOf(qty)));
        }
        foodSubtotal = foodSubtotal.setScale(2, RoundingMode.HALF_UP);

        // 2. Calculate GST Tax and Grand Total
        BigDecimal taxAmount = foodSubtotal.multiply(totalTaxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = foodSubtotal.add(taxAmount).add(serviceCharge).subtract(discount);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        BillCalculationResponse response = new BillCalculationResponse();
        response.setRestaurantName(restaurant);
        response.setFoodSubtotal(foodSubtotal);
        response.setTaxPercent(totalTaxPercent);
        response.setTaxAmount(taxAmount);
        response.setServiceCharge(serviceCharge);
        response.setDiscount(discount);
        response.setTotalBill(grandTotal);
        response.setSplitType(splitType);
        response.setPaidBy(paidBy);
        response.setNotes(request.getNotes());

        if (participants.isEmpty()) {
            response.setParticipantBills(Collections.emptyList());
            return response;
        }

        List<BillCalculationResponse.ParticipantBillDTO> participantBills = new ArrayList<>();

        if ("EQUAL".equals(splitType)) {
            int count = participants.size();
            BigDecimal countBd = BigDecimal.valueOf(count);
            BigDecimal rawShare = grandTotal.divide(countBd, 2, RoundingMode.HALF_UP);
            BigDecimal rawFoodShare = foodSubtotal.divide(countBd, 2, RoundingMode.HALF_UP);
            BigDecimal rawTaxShare = taxAmount.divide(countBd, 2, RoundingMode.HALF_UP);
            BigDecimal rawServiceShare = serviceCharge.divide(countBd, 2, RoundingMode.HALF_UP);
            BigDecimal rawDiscountShare = discount.divide(countBd, 2, RoundingMode.HALF_UP);

            BigDecimal runningTotal = BigDecimal.ZERO;

            for (int i = 0; i < count; i++) {
                BillCalculationRequest.ParticipantInput p = participants.get(i);
                BillCalculationResponse.ParticipantBillDTO pb = new BillCalculationResponse.ParticipantBillDTO();
                pb.setParticipantId(p.getId());
                pb.setParticipantName(p.getName());
                pb.setFoodSubtotal(rawFoodShare);
                pb.setTaxShare(rawTaxShare);
                pb.setServiceShare(rawServiceShare);
                pb.setDiscountShare(rawDiscountShare);
                pb.setSubtotal(rawFoodShare);

                BigDecimal share;
                if (i == count - 1) {
                    share = grandTotal.subtract(runningTotal);
                } else {
                    share = rawShare;
                    runningTotal = runningTotal.add(share);
                }
                pb.setFinalPayable(share);
                pb.setPaid(false);

                List<BillCalculationResponse.AssignedItemDTO> assignedList = new ArrayList<>();
                for (BillCalculationRequest.FoodItemInput f : foodItems) {
                    BillCalculationResponse.AssignedItemDTO ai = new BillCalculationResponse.AssignedItemDTO();
                    ai.setItemId(f.getId());
                    ai.setName(f.getName());
                    ai.setQuantity(f.getQuantity());
                    ai.setPrice(f.getPrice());
                    BigDecimal itemTotal = (f.getPrice() != null ? f.getPrice() : BigDecimal.ZERO)
                            .multiply(BigDecimal.valueOf(f.getQuantity() != null ? f.getQuantity() : 1));
                    ai.setTotalPrice(itemTotal);
                    ai.setIsShared(true);
                    ai.setSharedWithCount(count);
                    ai.setItemCostForParticipant(itemTotal.divide(countBd, 2, RoundingMode.HALF_UP));
                    assignedList.add(ai);
                }
                pb.setAssignedItems(assignedList);
                participantBills.add(pb);
            }
        } else {
            // INDIVIDUAL / DISH-BY-DISH ASSIGNMENT
            Map<String, BigDecimal> participantFoodTotals = new HashMap<>();
            Map<String, List<BillCalculationResponse.AssignedItemDTO>> participantAssignedMap = new HashMap<>();

            for (BillCalculationRequest.ParticipantInput p : participants) {
                participantFoodTotals.put(p.getId(), BigDecimal.ZERO);
                participantAssignedMap.put(p.getId(), new ArrayList<>());
            }

            for (BillCalculationRequest.FoodItemInput f : foodItems) {
                BigDecimal itemTotal = (f.getPrice() != null ? f.getPrice() : BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(f.getQuantity() != null ? f.getQuantity() : 1));

                List<String> assignedParticipantIds = assignments.get(f.getId());
                if (assignedParticipantIds != null && !assignedParticipantIds.isEmpty()) {
                    int numSharers = assignedParticipantIds.size();
                    BigDecimal perPersonCost = itemTotal.divide(BigDecimal.valueOf(numSharers), 2, RoundingMode.HALF_UP);

                    for (String pId : assignedParticipantIds) {
                        if (participantFoodTotals.containsKey(pId)) {
                            participantFoodTotals.put(pId, participantFoodTotals.get(pId).add(perPersonCost));

                            BillCalculationResponse.AssignedItemDTO ai = new BillCalculationResponse.AssignedItemDTO();
                            ai.setItemId(f.getId());
                            ai.setName(f.getName());
                            ai.setQuantity(f.getQuantity());
                            ai.setPrice(f.getPrice());
                            ai.setTotalPrice(itemTotal);
                            ai.setIsShared(numSharers > 1);
                            ai.setSharedWithCount(numSharers);
                            ai.setItemCostForParticipant(perPersonCost);
                            participantAssignedMap.get(pId).add(ai);
                        }
                    }
                }
            }

            BigDecimal totalAssignedFood = participantFoodTotals.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (BillCalculationRequest.ParticipantInput p : participants) {
                String pId = p.getId();
                BigDecimal myFood = participantFoodTotals.getOrDefault(pId, BigDecimal.ZERO);

                BigDecimal ratio = totalAssignedFood.compareTo(BigDecimal.ZERO) > 0
                        ? myFood.divide(totalAssignedFood, 4, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                BigDecimal myTax = taxAmount.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                BigDecimal myService = serviceCharge.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                BigDecimal myDiscount = discount.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                BigDecimal myFinal = myFood.add(myTax).add(myService).subtract(myDiscount).max(BigDecimal.ZERO);

                BillCalculationResponse.ParticipantBillDTO pb = new BillCalculationResponse.ParticipantBillDTO();
                pb.setParticipantId(pId);
                pb.setParticipantName(p.getName());
                pb.setFoodSubtotal(myFood);
                pb.setTaxShare(myTax);
                pb.setServiceShare(myService);
                pb.setDiscountShare(myDiscount);
                pb.setSubtotal(myFood);
                pb.setFinalPayable(myFinal);
                pb.setPaid(false);
                pb.setAssignedItems(participantAssignedMap.getOrDefault(pId, Collections.emptyList()));

                participantBills.add(pb);
            }
        }

        response.setParticipantBills(participantBills);

        // 3. Persist to MySQL database if repository is available
        if (billRepository != null) {
            try {
                Bill billEntity = new Bill();
                billEntity.setRestaurantName(restaurant);
                billEntity.setFoodSubtotal(foodSubtotal);
                billEntity.setTaxPercent(totalTaxPercent);
                billEntity.setTaxAmount(taxAmount);
                billEntity.setServiceCharge(serviceCharge);
                billEntity.setDiscount(discount);
                billEntity.setTotalAmount(grandTotal);
                billEntity.setSplitType(splitType);
                billEntity.setPaidBy(paidBy);
                billEntity.setNotes(request.getNotes());

                for (BillCalculationResponse.ParticipantBillDTO pb : participantBills) {
                    Participant pEntity = new Participant();
                    pEntity.setBill(billEntity);
                    pEntity.setName(pb.getParticipantName());
                    pEntity.setFoodSubtotal(pb.getFoodSubtotal());
                    pEntity.setTaxShare(pb.getTaxShare());
                    pEntity.setFinalAmount(pb.getFinalPayable());
                    pEntity.setPaid(false);
                    billEntity.getParticipants().add(pEntity);
                }

                for (BillCalculationRequest.FoodItemInput fi : foodItems) {
                    FoodItem fEntity = new FoodItem();
                    fEntity.setBill(billEntity);
                    fEntity.setName(fi.getName());
                    fEntity.setPrice(fi.getPrice() != null ? fi.getPrice() : BigDecimal.ZERO);
                    fEntity.setQuantity(fi.getQuantity() != null ? fi.getQuantity() : 1);
                    billEntity.getFoodItems().add(fEntity);
                }

                Bill saved = billRepository.save(billEntity);
                response.setBillId(saved.getId());
            } catch (Exception e) {
                // If DB is offline, calculation still returns successfully for demo
            }
        }

        return response;
    }
}
