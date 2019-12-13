package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.FailedBilledRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedRecordsRepository extends JpaRepository<FailedBilledRecordsEntity,Integer> {
}
