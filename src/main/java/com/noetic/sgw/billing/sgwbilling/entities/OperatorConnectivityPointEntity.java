package com.noetic.sgw.billing.sgwbilling.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "operator_connectivity_point", schema = "public", catalog = "sgw")
public class OperatorConnectivityPointEntity {
    private int id;
    private Integer operatorId;
    private Integer connectivityPointId;

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
    @Column(name = "connectivity_point_id")
    public Integer getConnectivityPointId() {
        return connectivityPointId;
    }

    public void setConnectivityPointId(Integer connectivityPointId) {
        this.connectivityPointId = connectivityPointId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatorConnectivityPointEntity that = (OperatorConnectivityPointEntity) o;
        return id == that.id &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(connectivityPointId, that.connectivityPointId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, operatorId, connectivityPointId);
    }
}
