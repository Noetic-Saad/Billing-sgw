package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "operator", schema = "public", catalog = "sgw")
public class OperatorEntity {
    private int id;
    private String name;
    private String prefix;
    private Boolean active;
    private Integer subCycle;
    private Double chargeAmount;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "prefix")
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Basic
    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
    @Column(name = "charge_amount")
    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatorEntity that = (OperatorEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(prefix, that.prefix) &&
                Objects.equals(active, that.active) &&
                Objects.equals(subCycle, that.subCycle) &&
                Objects.equals(chargeAmount, that.chargeAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, prefix, active, subCycle, chargeAmount);
    }
}
