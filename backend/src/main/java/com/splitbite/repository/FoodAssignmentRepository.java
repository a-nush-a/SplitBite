package com.splitbite.repository;

import com.splitbite.model.FoodAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodAssignmentRepository extends JpaRepository<FoodAssignment, Long> {
    List<FoodAssignment> findByFoodItemId(Long foodItemId);
    List<FoodAssignment> findByParticipantId(Long participantId);
}
