package com.noetic.sgw.billing.sgwbilling.config;

import com.noetic.sgw.billing.sgwbilling.entities.*;
import com.noetic.sgw.billing.sgwbilling.repository.*;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StartConfiguration {



    @Autowired
    ChargingMechanismRepository mechanismRepository;
    @Autowired
    OperatorPlanRepository operatorPlanRepository;
    @Autowired
    OperatorRepository operatorRepository;
    @Autowired ResponseTypeRepository responseTypeRepository;
    @Autowired
    TestMsisdnsRepository testMsisdnsRepository;

    public Map<Integer,List<OperatorPlanEntity>> operatorPlanMap = new HashMap<>();
    public Map<Integer,Integer> trafficPercentageMap = new HashMap<>();
    public Map<Integer,Integer> dailyCap = new HashMap<>();
    public Map<String,Integer> chargingMechanismMap = new HashMap<>();
    public Map<String, ResponseTypeEntity> responseTypeEntityMap = new HashMap<>();
    private Map<Long, TestMsisdnsEntity> testMsisdnsMap = new HashMap<>();

    private int jazz = 0;
    private int warid = 0;
    private int ufone = 0;
    private int zong = 0;
    private int telenor = 0;


    public void loadChargingMechanism(){
        List<ChargingMechanismEntity> list = mechanismRepository.findAll();
        list.forEach(chargingMechanismEntity -> trafficPercentageMap.put(chargingMechanismEntity.getId(),chargingMechanismEntity.getTotalTraffic()));
        list.forEach(chargingMechanismEntity -> dailyCap.put(chargingMechanismEntity.getId(),chargingMechanismEntity.getDailyCap()));
        list.forEach(chargingMechanismEntity -> chargingMechanismMap.put(chargingMechanismEntity.getCode(),chargingMechanismEntity.getId()));
    }

    public void loadResponseTypes() {
        Map<String, ResponseTypeEntity> map = new HashMap<>();
        List<ResponseTypeEntity> list = responseTypeRepository.findAll();
        list.forEach(entity -> map.put(entity.getCode(), entity));
        responseTypeEntityMap = map;
    }

    public void loadTestMsisdns() {
        List<TestMsisdnsEntity> list = testMsisdnsRepository.findAll();
        Map<Long, TestMsisdnsEntity> map = new HashMap<>();
        list.forEach(entity -> map.put(entity.getMsisdn(), entity));
        testMsisdnsMap = map;
    }

    public void loadOperator(){
        List<OperatorEntity> list = operatorRepository.findAll();
        for (int i = 0; i < list.size() ; i++) {
            if(list.get(i).getName().equalsIgnoreCase("jazz")){
                jazz = list.get(i).getId();
            }else if(list.get(i).getName().equalsIgnoreCase("warid")){
                warid = list.get(i).getId();
            } else if(list.get(i).getName().equalsIgnoreCase("ufone")){
                ufone = list.get(i).getId();
            }else if(list.get(i).getName().equalsIgnoreCase("zong")){
                zong = list.get(i).getId();
            }else {
                telenor = list.get(i).getId();
            }
        }
    }

    public Integer getTrafficPercentage(int chargingMechanismId){
        return trafficPercentageMap.get(chargingMechanismId);
    }

    public void loadOperatorPlan(){
        List<OperatorPlanEntity> jazzLst = new ArrayList<>();
        List<OperatorPlanEntity> telenorLst = new ArrayList<>();
        List<OperatorPlanEntity> zongLst = new ArrayList<>();
        List<OperatorPlanEntity> ufoneLst = new ArrayList<>();
        List<OperatorPlanEntity> waridLst = new ArrayList<>();
        List<OperatorPlanEntity> list = operatorPlanRepository.findAll();
        for (int i = 0; i < list.size() ; i++) {
            if(list.get(i).getOperatorId()==jazz){
                jazzLst.add(list.get(i));
                operatorPlanMap.put(list.get(i).getOperatorId(),jazzLst);
            }else if(list.get(i).getOperatorId()==telenor){
                telenorLst.add(list.get(i));
                operatorPlanMap.put(list.get(i).getOperatorId(),telenorLst);
            }else if(list.get(i).getOperatorId()==zong){
                zongLst.add(list.get(i));
                operatorPlanMap.put(list.get(i).getOperatorId(),zongLst);
            }else if(list.get(i).getOperatorId()==warid){
                waridLst.add(list.get(i));
                operatorPlanMap.put(list.get(i).getOperatorId(),waridLst);
            }else {
                ufoneLst.add(list.get(i));
                operatorPlanMap.put(list.get(i).getOperatorId(),ufoneLst);
            }

        }
    }

    public Integer getChargingMechanism(String code){
        return chargingMechanismMap.get(code);
    }

    public Integer getDailyCap(int chargingMechanism){
        return dailyCap.get(chargingMechanism);
    }
    public List<OperatorPlanEntity> getOperatorPlanEntity(Integer operatorID){
        return operatorPlanMap.get(operatorID);
    }

    public boolean isTestMsisdn(Long msisdn){
        if(testMsisdnsMap.containsKey(msisdn)){
            return true;
        }else {
            return false;
        }
    }

    public int getJazz() {
        return jazz;
    }

    public String getResultStatusDescription(String code) {
        return responseTypeEntityMap.get(code).getDescription();
    }

    public int getWarid() {
        return warid;
    }

    public int getUfone() {
        return ufone;
    }

    public int getZong() {
        return zong;
    }

    public int getTelenor() {
        return telenor;
    }
}
