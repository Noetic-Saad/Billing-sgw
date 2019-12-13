package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.SuccessBilledRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuccessBilledRecordsRepository extends JpaRepository<SuccessBilledRecordsEntity,Integer> {
}
