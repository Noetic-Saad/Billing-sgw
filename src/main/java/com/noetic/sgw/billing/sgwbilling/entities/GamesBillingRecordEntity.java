package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "games_billing_record", schema = "public", catalog = "sgw")
public class GamesBillingRecordEntity {
    private long id;
    private Timestamp cdate;
    private Double amount;
    private Double taxAmount;
    private Double shareAmount;
    private Integer isCharged;
    private Integer noOfDailyAttempts;
    private Integer noAttemptsMonthly;
    private Long vendorPlanId;
    private String transactionId;
    private Integer isPostpaid;
    private Short oparatorId;
    private Short chargingMechanism;
    private Long msisdn;
    private Integer isRenewal;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "games_billing_record_id_seq",sequenceName = "games_billing_record_id_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "games_billing_record_id_seq")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
    @Column(name = "share_amount")
    public Double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(Double shareAmount) {
        this.shareAmount = shareAmount;
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
    @Column(name = "no_of_daily_attempts")
    public Integer getNoOfDailyAttempts() {
        return noOfDailyAttempts;
    }

    public void setNoOfDailyAttempts(Integer noOfDailyAttempts) {
        this.noOfDailyAttempts = noOfDailyAttempts;
    }

    @Basic
    @Column(name = "no_attempts_monthly")
    public Integer getNoAttemptsMonthly() {
        return noAttemptsMonthly;
    }

    public void setNoAttemptsMonthly(Integer noAttemptsMonthly) {
        this.noAttemptsMonthly = noAttemptsMonthly;
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
    @Column(name = "transaction_id")
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Basic
    @Column(name = "is_postpaid")
    public Integer getIsPostpaid() {
        return isPostpaid;
    }

    public void setIsPostpaid(Integer isPostpaid) {
        this.isPostpaid = isPostpaid;
    }

    @Basic
    @Column(name = "oparator_id")
    public Short getOparatorId() {
        return oparatorId;
    }

    public void setOparatorId(Short oparatorId) {
        this.oparatorId = oparatorId;
    }

    @Basic
    @Column(name = "charging_mechanism")
    public Short getChargingMechanism() {
        return chargingMechanism;
    }

    public void setChargingMechanism(Short chargingMechanism) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamesBillingRecordEntity that = (GamesBillingRecordEntity) o;
        return id == that.id &&
                Objects.equals(cdate, that.cdate) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(taxAmount, that.taxAmount) &&
                Objects.equals(shareAmount, that.shareAmount) &&
                Objects.equals(isCharged, that.isCharged) &&
                Objects.equals(noOfDailyAttempts, that.noOfDailyAttempts) &&
                Objects.equals(noAttemptsMonthly, that.noAttemptsMonthly) &&
                Objects.equals(vendorPlanId, that.vendorPlanId) &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(isPostpaid, that.isPostpaid) &&
                Objects.equals(oparatorId, that.oparatorId) &&
                Objects.equals(chargingMechanism, that.chargingMechanism) &&
                Objects.equals(msisdn, that.msisdn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cdate, amount, taxAmount, shareAmount, isCharged, noOfDailyAttempts, noAttemptsMonthly, vendorPlanId, transactionId, isPostpaid, oparatorId, chargingMechanism, msisdn);
    }

    @Basic
    @Column(name = "is_renewal")
    public Integer getIsRenewal() {
        return isRenewal;
    }

    public void setIsRenewal(Integer isRenewal) {
        this.isRenewal = isRenewal;
    }
}
