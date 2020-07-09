package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.SuccessBilledRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SuccessBilledRecordsRepository extends JpaRepository<SuccessBilledRecordsEntity,Integer> {
    @Query(value = "SELECT * FROM public.success_billed_records WHERE msisdn=:msisdn and date(charge_time) BETWEEN date(:fromDate) and date(:toDate)",nativeQuery = true)
    SuccessBilledRecordsEntity isAlreadyCharged(@Param("msisdn") Long msisdn, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}