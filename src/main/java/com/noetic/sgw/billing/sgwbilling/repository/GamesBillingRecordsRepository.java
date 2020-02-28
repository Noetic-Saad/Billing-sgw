package com.noetic.sgw.billing.sgwbilling.repository;

import com.noetic.sgw.billing.sgwbilling.entities.GamesBillingRecordEntity;
import com.noetic.sgw.billing.sgwbilling.entities.SuccessBilledRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GamesBillingRecordsRepository extends JpaRepository<GamesBillingRecordEntity,Long> {
    @Query(value = "SELECT * FROM public.games_billing_record WHERE msisdn=:msisdn and is_charged = 1 and is_renewal = 1 and date(charge_time) BETWEEN date(:fromDate) and date(:toDate)",nativeQuery = true)
    GamesBillingRecordEntity isAlreadyCharged(@Param("msisdn") Long msisdn, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(value = "SELECT * FROM public.games_billing_record WHERE msisdn=:msisdn and date(charge_time) BETWEEN date(:fromDate) and date(:toDate) order by id desc limit 1",nativeQuery = true)
    GamesBillingRecordEntity getChargingRecords(@Param("msisdn") Long msisdn, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}
