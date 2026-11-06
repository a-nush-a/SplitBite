package com.splitbite.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class BillCalculationRequest {

    private String restaurantName;
    private String splitType; // 'EQUAL' or 'INDIVIDUAL'
    private BigDecimal taxPercent = new BigDecimal("5.0"); // e.g. 5% GST
    private BigDecimal serviceCharge = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private List<FoodItemInput> foodItems;
    private List<ParticipantInput> participants;
    private Map<String, List<String>> assignments; // itemId -> list of participantIds

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

    public BigDecimal getTaxPercent() { return taxPercent; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }

    public BigDecimal getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public List<FoodItemInput> getFoodItems() { return foodItems; }
    public void setFoodItems(List<FoodItemInput> foodItems) { this.foodItems = foodItems; }

    public List<ParticipantInput> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantInput> participants) { this.participants = participants; }

    public Map<String, List<String>> getAssignments() { return assignments; }
    public void setAssignments(Map<String, List<String>> assignments) { this.assignments = assignments; }
}
