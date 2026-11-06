package com.splitbite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "food_assignments")
public class FoodAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @JsonIgnore
    private FoodItem foodItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    @JsonIgnore
    private Participant participant;

    @Column(name = "share_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shareCost;

    public FoodAssignment() {}

    public FoodAssignment(FoodItem foodItem, Participant participant, BigDecimal shareCost) {
        this.foodItem = foodItem;
        this.participant = participant;
        this.shareCost = shareCost;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }

    public BigDecimal getShareCost() { return shareCost; }
    public void setShareCost(BigDecimal shareCost) { this.shareCost = shareCost; }
}
