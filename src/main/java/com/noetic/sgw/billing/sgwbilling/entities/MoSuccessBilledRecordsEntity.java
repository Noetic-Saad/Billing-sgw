package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "mo_success_billed_records", schema = "public", catalog = "sgw")
public class MoSuccessBilledRecordsEntity {
    private int id;
    private Timestamp chargeTime;
    private Double chargedAmount;
    private Double taxAmount;
    private Integer chargingMechanism;
    private Long msisdn;
    private Integer operatorId;
    private Double sharedAmount;
    private Integer partnerPlanId;
    private String trackerId;
    private String requestType;
    private Integer attempts;
    private String correlationid;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "success_billed_pk_seq",sequenceName = "success_billed_pk_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "success_billed_pk_seq")
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
    @Column(name = "shared_amount")
    public Double getSharedAmount() {
        return sharedAmount;
    }

    public void setSharedAmount(Double sharedAmount) {
        this.sharedAmount = sharedAmount;
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
    @Column(name = "tracker_id")
    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    @Basic
    @Column(name = "request_type")
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @Basic
    @Column(name = "attempts")
    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    @Basic
    @Column(name = "correlationid")
    public String getCorrelationid() {
        return correlationid;
    }

    public void setCorrelationid(String correlationid) {
        this.correlationid = correlationid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoSuccessBilledRecordsEntity that = (MoSuccessBilledRecordsEntity) o;
        return id == that.id &&
                Objects.equals(chargeTime, that.chargeTime) &&
                Objects.equals(chargedAmount, that.chargedAmount) &&
                Objects.equals(taxAmount, that.taxAmount) &&
                Objects.equals(chargingMechanism, that.chargingMechanism) &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(sharedAmount, that.sharedAmount) &&
                Objects.equals(partnerPlanId, that.partnerPlanId) &&
                Objects.equals(trackerId, that.trackerId) &&
                Objects.equals(requestType, that.requestType) &&
                Objects.equals(attempts, that.attempts) &&
                Objects.equals(correlationid, that.correlationid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeTime, chargedAmount, taxAmount, chargingMechanism, msisdn, operatorId, sharedAmount, partnerPlanId, trackerId, requestType, attempts, correlationid);
    }
}
