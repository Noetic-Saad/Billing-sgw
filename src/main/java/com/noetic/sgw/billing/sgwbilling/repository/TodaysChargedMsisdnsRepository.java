package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.TodaysChargedMsisdnsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodaysChargedMsisdnsRepository extends JpaRepository<TodaysChargedMsisdnsEntity,Long> {
    TodaysChargedMsisdnsEntity findTopByMsisdn(long msisdn);
}
