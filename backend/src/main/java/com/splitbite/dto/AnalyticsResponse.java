package com.splitbite.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AnalyticsResponse {
    private long totalBillsCount;
    private BigDecimal totalAmountSpent;
    private BigDecimal totalSettledAmount;
    private BigDecimal totalPendingAmount;
    private long totalParticipantsCount;
    private Map<String, BigDecimal> monthlyBreakdown;

    public AnalyticsResponse() {}

    public AnalyticsResponse(long totalBillsCount, BigDecimal totalAmountSpent, BigDecimal totalSettledAmount, BigDecimal totalPendingAmount, long totalParticipantsCount, Map<String, BigDecimal> monthlyBreakdown) {
        this.totalBillsCount = totalBillsCount;
        this.totalAmountSpent = totalAmountSpent;
        this.totalSettledAmount = totalSettledAmount;
        this.totalPendingAmount = totalPendingAmount;
        this.totalParticipantsCount = totalParticipantsCount;
        this.monthlyBreakdown = monthlyBreakdown;
    }

    public long getTotalBillsCount() { return totalBillsCount; }
    public void setTotalBillsCount(long totalBillsCount) { this.totalBillsCount = totalBillsCount; }

    public BigDecimal getTotalAmountSpent() { return totalAmountSpent; }
    public void setTotalAmountSpent(BigDecimal totalAmountSpent) { this.totalAmountSpent = totalAmountSpent; }

    public BigDecimal getTotalSettledAmount() { return totalSettledAmount; }
    public void setTotalSettledAmount(BigDecimal totalSettledAmount) { this.totalSettledAmount = totalSettledAmount; }

    public BigDecimal getTotalPendingAmount() { return totalPendingAmount; }
    public void setTotalPendingAmount(BigDecimal totalPendingAmount) { this.totalPendingAmount = totalPendingAmount; }

    public long getTotalParticipantsCount() { return totalParticipantsCount; }
    public void setTotalParticipantsCount(long totalParticipantsCount) { this.totalParticipantsCount = totalParticipantsCount; }

    public Map<String, BigDecimal> getMonthlyBreakdown() { return monthlyBreakdown; }
    public void setMonthlyBreakdown(Map<String, BigDecimal> monthlyBreakdown) { this.monthlyBreakdown = monthlyBreakdown; }
}
