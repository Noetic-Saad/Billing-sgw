package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "mo_billed_records", schema = "public", catalog = "sgw")
public class MoBilledRecordsEntity {
    private int id;
    private Timestamp chargeTime;
    private Double chargedAmount;
    private Double taxAmount;
    private Integer chargingMechanism;
    private Long msisdn;
    private Integer operatorId;
    private Integer partnerPlanId;
    private String chargingResponse;
    private Integer isCharged;
    private String transactionId;
    private Long originalSmsId;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "mo_billed_record_id_seq",sequenceName = "mo_billed_record_id_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mo_billed_record_id_seq")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "charge_time")
    public Timestamp getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(Timestamp chargeTime) {
        this.chargeTime = chargeTime;
    }

    @Basic
    @Column(name = "charged_amount")
    public Double getChargedAmount() {
        return chargedAmount;
    }

    public void setChargedAmount(Double chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    @Basic
    @Column(name = "tax_amount")
    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Basic
    @Column(name = "charging_mechanism")
    public Integer getChargingMechanism() {
        return chargingMechanism;
    }

    public void setChargingMechanism(Integer chargingMechanism) {
        this.chargingMechanism = chargingMechanism;
    }

    @Basic
    @Column(name = "msisdn")
    public Long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
    }

    @Basic
    @Column(name = "operator_id")
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Basic
    @Column(name = "partner_plan_id")
    public Integer getPartnerPlanId() {
        return partnerPlanId;
    }

    public void setPartnerPlanId(Integer partnerPlanId) {
        this.partnerPlanId = partnerPlanId;
    }

    @Basic
    @Column(name = "charging_response")
    public String getChargingResponse() {
        return chargingResponse;
    }

    public void setChargingResponse(String chargingResponse) {
        this.chargingResponse = chargingResponse;
    }

    @Basic
    @Column(name = "is_charged")
    public Integer getIsCharged() {
        return isCharged;
    }

    public void setIsCharged(Integer isCharged) {
        this.isCharged = isCharged;
    }

    @Basic
    @Column(name = "transaction_id")
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Basic
    @Column(name = "original_sms_id")
    public Long getOriginalSmsId() {
        return originalSmsId;
    }

    public void setOriginalSmsId(Long originalSmsId) {
        this.originalSmsId = originalSmsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoBilledRecordsEntity that = (MoBilledRecordsEntity) o;
        return id == that.id &&
                Objects.equals(chargeTime, that.chargeTime) &&
                Objects.equals(chargedAmount, that.chargedAmount) &&
                Objects.equals(taxAmount, that.taxAmount) &&
                Objects.equals(chargingMechanism, that.chargingMechanism) &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(partnerPlanId, that.partnerPlanId) &&
                Objects.equals(chargingResponse, that.chargingResponse) &&
                Objects.equals(isCharged, that.isCharged) &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(originalSmsId, that.originalSmsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeTime, chargedAmount, taxAmount, chargingMechanism, msisdn, operatorId, partnerPlanId, chargingResponse, isCharged, transactionId, originalSmsId);
    }
}
