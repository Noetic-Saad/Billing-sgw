package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "failed_billed_records", schema = "public", catalog = "billing")
public class FailedBilledRecordsEntity {
    private int id;
    private Double chargeAmount;
    private Integer chargingMechanism;
    private Timestamp dateTime;
    private String msisdn;
    private String operatorId;
    private Double shareAmount;
    private Integer vpAccountId;
    private String reason;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Basic
    @Column(name = "operator_id")
    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    @Basic
    @Column(name = "share_amount")
    public Double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(Double shareAmount) {
        this.shareAmount = shareAmount;
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
                Objects.equals(shareAmount, that.shareAmount) &&
                Objects.equals(vpAccountId, that.vpAccountId) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeAmount, chargingMechanism, dateTime, msisdn, operatorId, shareAmount, vpAccountId, reason);
    }
}
