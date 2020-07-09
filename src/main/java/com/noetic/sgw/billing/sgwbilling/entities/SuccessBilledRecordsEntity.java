package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "success_billed_records", schema = "public", catalog = "sgw")
public class SuccessBilledRecordsEntity {
    private int id;
    private Timestamp chargeTime;
    private Double chargedAmount;
    private Integer chargingMechanism;
    private Long msisdn;
    private Integer operatorId;
    private Double shareAmount;
    private Integer vpAccountId;
    private String correlationid;
    private Double taxAmount;
    private Integer vendorPlanId;
    private String trackerId;
    private String requestType;
    private Integer attempts;

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
    public Double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(Double shareAmount) {
        this.shareAmount = shareAmount;
    }

    @Basic
    @Column(name = "vendor_plan_id")
    public Integer getVpAccountId() {
        return vpAccountId;
    }

    public void setVpAccountId(Integer vpAccountId) {
        this.vpAccountId = vpAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuccessBilledRecordsEntity entity = (SuccessBilledRecordsEntity) o;
        return id == entity.id &&
                Objects.equals(chargeTime, entity.chargeTime) &&
                Objects.equals(chargedAmount, entity.chargedAmount) &&
                Objects.equals(chargingMechanism, entity.chargingMechanism) &&
                Objects.equals(msisdn, entity.msisdn) &&
                Objects.equals(operatorId, entity.operatorId) &&
                Objects.equals(shareAmount, entity.shareAmount) &&
                Objects.equals(vpAccountId, entity.vpAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeTime, chargedAmount, chargingMechanism, msisdn, operatorId, shareAmount, vpAccountId);
    }

    @Basic
    @Column(name = "correlationid")
    public String getCorrelationid() {
        return correlationid;
    }

    public void setCorrelationid(String correlationid) {
        this.correlationid = correlationid;
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
}
