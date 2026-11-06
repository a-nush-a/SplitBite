package com.splitbite.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class BillCalculationRequest {

    private String restaurantName;
    private String splitType; // 'EQUAL' or 'INDIVIDUAL'
    private BigDecimal cgstPercent = new BigDecimal("2.5"); // Indian CGST (default 2.5%)
    private BigDecimal sgstPercent = new BigDecimal("2.5"); // Indian SGST (default 2.5%)
    private BigDecimal taxPercent = new BigDecimal("5.0");
    private BigDecimal serviceCharge = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private String paidBy; // Name of person who paid the bill upfront
    private String payerUpiId; // Host's UPI ID for reimbursements
    private String notes;
    private List<FoodItemInput> foodItems;
    private List<ParticipantInput> participants;
    private Map<String, List<String>> assignments;

    public static class FoodItemInput {
        private String id;
        private String name;
        private BigDecimal price;
        private Integer quantity;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class ParticipantInput {
        private String id;
        private String name;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }

    public BigDecimal getCgstPercent() { return cgstPercent; }
    public void setCgstPercent(BigDecimal cgstPercent) { this.cgstPercent = cgstPercent; }

    public BigDecimal getSgstPercent() { return sgstPercent; }
    public void setSgstPercent(BigDecimal sgstPercent) { this.sgstPercent = sgstPercent; }

    public BigDecimal getTaxPercent() { return taxPercent; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }

    public BigDecimal getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }

    public String getPayerUpiId() { return payerUpiId; }
    public void setPayerUpiId(String payerUpiId) { this.payerUpiId = payerUpiId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<FoodItemInput> getFoodItems() { return foodItems; }
    public void setFoodItems(List<FoodItemInput> foodItems) { this.foodItems = foodItems; }

    public List<ParticipantInput> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantInput> participants) { this.participants = participants; }

    public Map<String, List<String>> getAssignments() { return assignments; }
    public void setAssignments(Map<String, List<String>> assignments) { this.assignments = assignments; }
}
