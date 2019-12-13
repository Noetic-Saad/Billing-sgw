package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "success_billed_records", schema = "public", catalog = "billing")
public class SuccessBilledRecordsEntity {
    private int id;
    private Timestamp chargeTime;
    private Double chargedAmount;
    private Integer chargingMechanism;
    private Integer operatorId;
    private Double shareAmount;
    private Integer vpAccountId;
    private String msisdn;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    @Column(name = "operator_id")
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
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
    @Column(name = "msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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
                Objects.equals(operatorId, entity.operatorId) &&
                Objects.equals(shareAmount, entity.shareAmount) &&
                Objects.equals(vpAccountId, entity.vpAccountId) &&
                Objects.equals(msisdn, entity.msisdn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeTime, chargedAmount, chargingMechanism, operatorId, shareAmount, vpAccountId, msisdn);
    }
}
