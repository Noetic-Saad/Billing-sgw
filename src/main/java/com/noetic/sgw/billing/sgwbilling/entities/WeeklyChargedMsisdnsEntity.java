package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "weekly_charged_msisdns", schema = "public", catalog = "sgw")
public class WeeklyChargedMsisdnsEntity {
    private long id;
    private Long msisdn;
    private Integer isCharged;
    private Integer numberOfTries;
    private Timestamp cdate;
    private Integer subCycle;
    private Long vendorPlanId;
    private Integer operatorId;
    private Timestamp expirydatetime;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "weekly_charged_msisdns_id_seq",sequenceName = "weekly_charged_msisdns_id_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "weekly_charged_msisdns_id_seq")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "is_charged")
    public Integer getIsCharged() {
        return isCharged;
    }

    public void setIsCharged(Integer isCharged) {
        this.isCharged = isCharged;
    }

    @Basic
    @Column(name = "number_of_tries")
    public Integer getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(Integer numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    @Basic
    @Column(name = "cdate")
    public Timestamp getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Basic
    @Column(name = "sub_cycle")
    public Integer getSubCycle() {
        return subCycle;
    }

    public void setSubCycle(Integer subCycle) {
        this.subCycle = subCycle;
    }

    @Basic
    @Column(name = "vendor_plan_id")
    public Long getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(Long vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
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
    @Column(name = "expirydatetime")
    public Timestamp getExpirydatetime() {
        return expirydatetime;
    }

    public void setExpirydatetime(Timestamp expirydatetime) {
        this.expirydatetime = expirydatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyChargedMsisdnsEntity that = (WeeklyChargedMsisdnsEntity) o;
        return id == that.id &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(isCharged, that.isCharged) &&
                Objects.equals(numberOfTries, that.numberOfTries) &&
                Objects.equals(cdate, that.cdate) &&
                Objects.equals(subCycle, that.subCycle) &&
                Objects.equals(vendorPlanId, that.vendorPlanId) &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(expirydatetime, that.expirydatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, msisdn, isCharged, numberOfTries, cdate, subCycle, vendorPlanId, operatorId, expirydatetime);
    }
}
