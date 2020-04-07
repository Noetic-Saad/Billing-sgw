package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.WeeklyChargedMsisdnsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyChargedMsisdnsRepository extends JpaRepository<WeeklyChargedMsisdnsEntity,Long> {
    WeeklyChargedMsisdnsEntity findTopByMsisdn(long msisdn);
}
