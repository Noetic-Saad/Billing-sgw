package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.MoFailedBilledRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoFailedRecordsRepository extends JpaRepository<MoFailedBilledRecordsEntity,Integer> {
}
