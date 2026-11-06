package com.splitbite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonIgnore
    private Bill bill;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "food_subtotal", precision = 10, scale = 2)
    private BigDecimal foodSubtotal = BigDecimal.ZERO;

    @Column(name = "tax_share", precision = 10, scale = 2)
    private BigDecimal taxShare = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod; // e.g., 'CASH', 'UPI', 'BANK_TRANSFER'

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodAssignment> assignments = new ArrayList<>();

    public Participant() {}

    public Participant(Bill bill, String name, BigDecimal finalAmount) {
        this.bill = bill;
        this.name = name;
        this.finalAmount = finalAmount;
        this.isPaid = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getFoodSubtotal() { return foodSubtotal; }
    public void setFoodSubtotal(BigDecimal foodSubtotal) { this.foodSubtotal = foodSubtotal; }

    public BigDecimal getTaxShare() { return taxShare; }
    public void setTaxShare(BigDecimal taxShare) { this.taxShare = taxShare; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getSettledAt() { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt) { this.settledAt = settledAt; }

    public List<FoodAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<FoodAssignment> assignments) { this.assignments = assignments; }
}
