package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "failed_billed_records", schema = "public", catalog = "sgw")
public class FailedBilledRecordsEntity {
    private int id;
    private Double chargeAmount;
    private Integer chargingMechanism;
    private Timestamp dateTime;
    private Long msisdn;
    private Integer operatorId;
    private Integer vpAccountId;
    private String reason;
    private Integer statusCode;
    private String correlationid;
    private Double taxAmount;
    private Double sharedAmount;
    private String requestType;
    private String trackerId;
    private Integer attempts;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "failed_billed_pk_seq",sequenceName = "failed_billed_pk_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "failed_billed_pk_seq")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "charge_amount")
    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
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
    @Column(name = "date_time")
    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
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
    @Column(name = "vp_account_id")
    public Integer getVpAccountId() {
        return vpAccountId;
    }

    public void setVpAccountId(Integer vpAccountId) {
        this.vpAccountId = vpAccountId;
    }

    @Basic
    @Column(name = "reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Basic
    @Column(name = "status_code")
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailedBilledRecordsEntity that = (FailedBilledRecordsEntity) o;
        return id == that.id &&
                Objects.equals(chargeAmount, that.chargeAmount) &&
                Objects.equals(chargingMechanism, that.chargingMechanism) &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(vpAccountId, that.vpAccountId) &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(statusCode, that.statusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeAmount, chargingMechanism, dateTime, msisdn, operatorId, sharedAmount, vpAccountId, reason, statusCode);
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
    @Column(name = "shared_amount")
    public Double getSharedAmount() {
        return sharedAmount;
    }

    public void setSharedAmount(Double sharedAmount) {
        this.sharedAmount = sharedAmount;
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
    @Column(name = "tracker_id")
    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
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
