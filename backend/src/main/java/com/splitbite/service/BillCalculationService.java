package com.splitbite.service;

import com.splitbite.dto.BillCalculationRequest;
import com.splitbite.dto.BillCalculationResponse;
import com.splitbite.model.Bill;
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
        String restaurant = request.getRestaurantName() != null ? request.getRestaurantName().trim() : "Restaurant Bill";
        String splitType = request.getSplitType() != null ? request.getSplitType().toUpperCase() : "EQUAL";
        List<BillCalculationRequest.FoodItemInput> foodItems = request.getFoodItems() != null ? request.getFoodItems() : Collections.emptyList();
        List<BillCalculationRequest.ParticipantInput> participants = request.getParticipants() != null ? request.getParticipants() : Collections.emptyList();
        Map<String, List<String>> assignments = request.getAssignments() != null ? request.getAssignments() : Collections.emptyMap();

        BigDecimal taxPercent = request.getTaxPercent() != null ? request.getTaxPercent() : new BigDecimal("5.0");
        BigDecimal serviceCharge = request.getServiceCharge() != null ? request.getServiceCharge() : BigDecimal.ZERO;
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;

        // 1. Calculate Food Subtotal
        BigDecimal foodSubtotal = BigDecimal.ZERO;
        for (BillCalculationRequest.FoodItemInput item : foodItems) {
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            int qty = item.getQuantity() != null && item.getQuantity() > 0 ? item.getQuantity() : 1;
            foodSubtotal = foodSubtotal.add(price.multiply(BigDecimal.valueOf(qty)));
        }
        foodSubtotal = foodSubtotal.setScale(2, RoundingMode.HALF_UP);

        // 2. Calculate GST Tax and Grand Total
        BigDecimal taxAmount = foodSubtotal.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = foodSubtotal.add(taxAmount).add(serviceCharge).subtract(discount);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        BillCalculationResponse response = new BillCalculationResponse();
        response.setRestaurantName(restaurant);
        response.setFoodSubtotal(foodSubtotal);
        response.setTaxPercent(taxPercent);
        response.setTaxAmount(taxAmount);
        response.setServiceCharge(serviceCharge);
        response.setDiscount(discount);
        response.setTotalBill(grandTotal);
        response.setSplitType(splitType);

        if (participants.isEmpty()) {
            response.setParticipantBills(Collections.emptyList());
            return response;
        }

        List<BillCalculationResponse.ParticipantBillDTO> participantBills = new ArrayList<>();
        BigDecimal count = BigDecimal.valueOf(participants.size());

        // 3. EQUAL SPLIT LOGIC
        if ("EQUAL".equals(splitType)) {
            BigDecimal rawShare = grandTotal.divide(count, 2, RoundingMode.HALF_UP);
            BigDecimal foodShare = foodSubtotal.divide(count, 2, RoundingMode.HALF_UP);
            BigDecimal taxShare = taxAmount.divide(count, 2, RoundingMode.HALF_UP);
            BigDecimal svcShare = serviceCharge.divide(count, 2, RoundingMode.HALF_UP);
            BigDecimal discShare = discount.divide(count, 2, RoundingMode.HALF_UP);

            BigDecimal runningTotal = BigDecimal.ZERO;

            for (int i = 0; i < participants.size(); i++) {
                BillCalculationRequest.ParticipantInput p = participants.get(i);
                BillCalculationResponse.ParticipantBillDTO pDto = new BillCalculationResponse.ParticipantBillDTO();
                pDto.setParticipantId(p.getId());
                pDto.setParticipantName(p.getName());

                BigDecimal share;
                if (i == participants.size() - 1) {
                    share = grandTotal.subtract(runningTotal);
                } else {
                    share = rawShare;
                    runningTotal = runningTotal.add(share);
                }

                pDto.setFoodSubtotal(foodShare);
                pDto.setTaxShare(taxShare);
                pDto.setServiceShare(svcShare);
                pDto.setDiscountShare(discShare);
                pDto.setSubtotal(foodShare);
                pDto.setFinalPayable(share);

                List<BillCalculationResponse.AssignedItemDTO> assignedItems = new ArrayList<>();
                for (BillCalculationRequest.FoodItemInput item : foodItems) {
                    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    BigDecimal portionCost = itemTotal.divide(count, 2, RoundingMode.HALF_UP);

                    BillCalculationResponse.AssignedItemDTO aDto = new BillCalculationResponse.AssignedItemDTO();
                    aDto.setItemId(item.getId());
                    aDto.setName(item.getName());
                    aDto.setQuantity(item.getQuantity());
                    aDto.setPrice(item.getPrice());
                    aDto.setTotalPrice(itemTotal);
                    aDto.setIsShared(true);
                    aDto.setSharedWithCount(participants.size());
                    aDto.setItemCostForParticipant(portionCost);
                    assignedItems.add(aDto);
                }
                pDto.setAssignedItems(assignedItems);
                participantBills.add(pDto);
            }
        } 
        // 4. INDIVIDUAL FOOD ASSIGNMENT WITH PROPORTIONAL GST
        else {
            Map<String, BillCalculationResponse.ParticipantBillDTO> pMap = new LinkedHashMap<>();
            for (BillCalculationRequest.ParticipantInput p : participants) {
                BillCalculationResponse.ParticipantBillDTO pDto = new BillCalculationResponse.ParticipantBillDTO();
                pDto.setParticipantId(p.getId());
                pDto.setParticipantName(p.getName());
                pDto.setAssignedItems(new ArrayList<>());
                pDto.setFoodSubtotal(BigDecimal.ZERO);
                pDto.setSubtotal(BigDecimal.ZERO);
                pDto.setFinalPayable(BigDecimal.ZERO);
                pMap.put(p.getId(), pDto);
            }

            for (BillCalculationRequest.FoodItemInput item : foodItems) {
                BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                int qty = item.getQuantity() != null && item.getQuantity() > 0 ? item.getQuantity() : 1;
                BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(qty));

                List<String> assignedParticipantIds = assignments.getOrDefault(item.getId(), Collections.emptyList());
                if (assignedParticipantIds.isEmpty()) {
                    continue;
                }

                int shareCount = assignedParticipantIds.size();
                BigDecimal portion = itemTotal.divide(BigDecimal.valueOf(shareCount), 2, RoundingMode.HALF_UP);

                for (String pId : assignedParticipantIds) {
                    BillCalculationResponse.ParticipantBillDTO targetP = pMap.get(pId);
                    if (targetP != null) {
                        BillCalculationResponse.AssignedItemDTO aDto = new BillCalculationResponse.AssignedItemDTO();
                        aDto.setItemId(item.getId());
                        aDto.setName(item.getName());
                        aDto.setQuantity(qty);
                        aDto.setPrice(price);
                        aDto.setTotalPrice(itemTotal);
                        aDto.setIsShared(shareCount > 1);
                        aDto.setSharedWithCount(shareCount);
                        aDto.setItemCostForParticipant(portion);

                        targetP.getAssignedItems().add(aDto);
                        targetP.setFoodSubtotal(targetP.getFoodSubtotal().add(portion));
                    }
                }
            }

            // Calculate total food consumed by all assigned participants
            BigDecimal totalAssignedFood = BigDecimal.ZERO;
            for (BillCalculationResponse.ParticipantBillDTO pDto : pMap.values()) {
                totalAssignedFood = totalAssignedFood.add(pDto.getFoodSubtotal());
            }

            // Distribute GST & charges proportionally
            for (BillCalculationResponse.ParticipantBillDTO targetP : pMap.values()) {
                BigDecimal ratio = (totalAssignedFood.compareTo(BigDecimal.ZERO) > 0)
                        ? targetP.getFoodSubtotal().divide(totalAssignedFood, 4, RoundingMode.HALF_UP)
                        : BigDecimal.ONE.divide(count, 4, RoundingMode.HALF_UP);

                BigDecimal myTax = taxAmount.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                BigDecimal myService = serviceCharge.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                BigDecimal myDiscount = discount.multiply(ratio).setScale(2, RoundingMode.HALF_UP);

                targetP.setTaxShare(myTax);
                targetP.setServiceShare(myService);
                targetP.setDiscountShare(myDiscount);
                targetP.setSubtotal(targetP.getFoodSubtotal());

                BigDecimal payable = targetP.getFoodSubtotal().add(myTax).add(myService).subtract(myDiscount);
                targetP.setFinalPayable(payable);
            }

            participantBills.addAll(pMap.values());
        }

        response.setParticipantBills(participantBills);

        // Optional save to MySQL
        try {
            if (billRepository != null) {
                Bill billEntity = new Bill(restaurant, grandTotal, splitType);
                billRepository.save(billEntity);
                response.setBillId(billEntity.getId());
            }
        } catch (Exception e) {
            System.err.println("Database notice: " + e.getMessage());
        }

        return response;
    }
}
