package com.splitbite.repository;

import com.splitbite.model.Bill;
import com.splitbite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserOrderByCreatedAtDesc(User user);
    List<Bill> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.user = :user")
    BigDecimal sumTotalByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b")
    BigDecimal sumTotalAll();
}
