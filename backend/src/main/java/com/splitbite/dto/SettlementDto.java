package com.splitbite.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SettlementDto {
    private Long participantId;
    private String participantName;
    private BigDecimal amountOwed;
    private boolean isPaid;
    private String paymentMethod; // 'CASH', 'UPI', 'BANK_TRANSFER'
    private LocalDateTime settledAt;

    public SettlementDto() {}

    public SettlementDto(Long participantId, String participantName, BigDecimal amountOwed, boolean isPaid, String paymentMethod, LocalDateTime settledAt) {
        this.participantId = participantId;
        this.participantName = participantName;
        this.amountOwed = amountOwed;
        this.isPaid = isPaid;
        this.paymentMethod = paymentMethod;
        this.settledAt = settledAt;
    }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public BigDecimal getAmountOwed() { return amountOwed; }
    public void setAmountOwed(BigDecimal amountOwed) { this.amountOwed = amountOwed; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getSettledAt() { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt) { this.settledAt = settledAt; }
}
