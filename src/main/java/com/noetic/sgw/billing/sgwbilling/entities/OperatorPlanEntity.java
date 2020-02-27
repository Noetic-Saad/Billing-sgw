package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "operator_plan", schema = "public", catalog = "sgw")
public class OperatorPlanEntity {
    private int id;
    private Integer operatorId;
    private Integer chargingMechanismId;
    private Integer trafficPercentage;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @Column(name = "charging_mechanism_id")
    public Integer getChargingMechanismId() {
        return chargingMechanismId;
    }

    public void setChargingMechanismId(Integer chargingMechanismId) {
        this.chargingMechanismId = chargingMechanismId;
    }

    @Basic
    @Column(name = "traffic_percentage")
    public Integer getTrafficPercentage() {
        return trafficPercentage;
    }

    public void setTrafficPercentage(Integer trafficPercentage) {
        this.trafficPercentage = trafficPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatorPlanEntity that = (OperatorPlanEntity) o;
        return id == that.id &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(chargingMechanismId, that.chargingMechanismId) &&
                Objects.equals(trafficPercentage, that.trafficPercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, operatorId, chargingMechanismId, trafficPercentage);
    }
}
