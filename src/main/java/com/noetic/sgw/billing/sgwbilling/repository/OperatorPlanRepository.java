package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.OperatorPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorPlanRepository extends JpaRepository<OperatorPlanEntity,Integer> {
}
