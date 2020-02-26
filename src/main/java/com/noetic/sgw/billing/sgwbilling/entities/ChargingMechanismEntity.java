package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "charging_mechanism", schema = "public", catalog = "sgw")
public class ChargingMechanismEntity {
    private int id;
    private String code;
    private String description;
    private Integer totalTraffic;
    private Integer dailyCap;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "total_traffic")
    public Integer getTotalTraffic() {
        return totalTraffic;
    }

    public void setTotalTraffic(Integer totalTraffic) {
        this.totalTraffic = totalTraffic;
    }

    @Basic
    @Column(name = "daily_cap")
    public Integer getDailyCap() {
        return dailyCap;
    }

    public void setDailyCap(Integer dailyCap) {
        this.dailyCap = dailyCap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargingMechanismEntity that = (ChargingMechanismEntity) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(description, that.description) &&
                Objects.equals(totalTraffic, that.totalTraffic) &&
                Objects.equals(dailyCap, that.dailyCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, description, totalTraffic, dailyCap);
    }
}
