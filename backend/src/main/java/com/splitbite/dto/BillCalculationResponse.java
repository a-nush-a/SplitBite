package com.splitbite.dto;

import java.math.BigDecimal;
import java.util.List;

public class BillCalculationResponse {

    private Long billId;
    private String restaurantName;
    private BigDecimal foodSubtotal;
    private BigDecimal taxPercent;
    private BigDecimal taxAmount;
    private BigDecimal serviceCharge;
    private BigDecimal discount;
    private BigDecimal totalBill; // Grand Total
    private String splitType;
    private List<ParticipantBillDTO> participantBills;

    public static class ParticipantBillDTO {
        private String participantId;
        private String participantName;
        private List<AssignedItemDTO> assignedItems;
        private BigDecimal foodSubtotal;
        private BigDecimal taxShare;
        private BigDecimal serviceShare;
        private BigDecimal discountShare;
        private BigDecimal subtotal;
        private BigDecimal finalPayable;

        public String getParticipantId() { return participantId; }
        public void setParticipantId(String participantId) { this.participantId = participantId; }

        public String getParticipantName() { return participantName; }
        public void setParticipantName(String participantName) { this.participantName = participantName; }

        public List<AssignedItemDTO> getAssignedItems() { return assignedItems; }
        public void setAssignedItems(List<AssignedItemDTO> assignedItems) { this.assignedItems = assignedItems; }

        public BigDecimal getFoodSubtotal() { return foodSubtotal; }
        public void setFoodSubtotal(BigDecimal foodSubtotal) { this.foodSubtotal = foodSubtotal; }

        public BigDecimal getTaxShare() { return taxShare; }
        public void setTaxShare(BigDecimal taxShare) { this.taxShare = taxShare; }

        public BigDecimal getServiceShare() { return serviceShare; }
        public void setServiceShare(BigDecimal serviceShare) { this.serviceShare = serviceShare; }

        public BigDecimal getDiscountShare() { return discountShare; }
        public void setDiscountShare(BigDecimal discountShare) { this.discountShare = discountShare; }

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

        public BigDecimal getFinalPayable() { return finalPayable; }
        public void setFinalPayable(BigDecimal finalPayable) { this.finalPayable = finalPayable; }
    }

    public static class AssignedItemDTO {
        private String itemId;
        private String name;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;
        private Boolean isShared;
        private Integer sharedWithCount;
        private BigDecimal itemCostForParticipant;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

        public Boolean getIsShared() { return isShared; }
        public void setIsShared(Boolean isShared) { this.isShared = isShared; }

        public Integer getSharedWithCount() { return sharedWithCount; }
        public void setSharedWithCount(Integer sharedWithCount) { this.sharedWithCount = sharedWithCount; }

        public BigDecimal getItemCostForParticipant() { return itemCostForParticipant; }
        public void setItemCostForParticipant(BigDecimal itemCostForParticipant) { this.itemCostForParticipant = itemCostForParticipant; }
    }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public BigDecimal getFoodSubtotal() { return foodSubtotal; }
    public void setFoodSubtotal(BigDecimal foodSubtotal) { this.foodSubtotal = foodSubtotal; }

    public BigDecimal getTaxPercent() { return taxPercent; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTotalBill() { return totalBill; }
    public void setTotalBill(BigDecimal totalBill) { this.totalBill = totalBill; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }

    public List<ParticipantBillDTO> getParticipantBills() { return participantBills; }
    public void setParticipantBills(List<ParticipantBillDTO> participantBills) { this.participantBills = participantBills; }
}
