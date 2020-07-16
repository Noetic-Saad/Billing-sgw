package com.noetic.sgw.billing.sgwbilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.noetic.sgw.billing.sgwbilling.config.StartConfiguration;
import com.noetic.sgw.billing.sgwbilling.request.*;
import com.noetic.sgw.billing.sgwbilling.util.ChargeRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.MoRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.MoResponse;
import com.noetic.sgw.billing.sgwbilling.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public
class ChargingService {

    Logger logger = LoggerFactory.getLogger(ChargingService.class);

    private TelenorCharging telenorCharging;
    @Autowired
    private JazzCharging jazzCharging;
    @Autowired
    StartConfiguration startConfiguration;
    @Autowired
    UcipRequest ucipRequest;
    @Autowired
    ZongCharging zongCharging;
    @Autowired
    ZongMoCharging zongMoCharging;


    Response response = null;
    MoResponse moResponse = null;
    private long operator_id;
    private int ucipCount = 0;
    private int ucipDailyCap = 0;
    private int dotDailyCap = 0;
    private int dotCount = 0;
    private int TELENOR_COUNT = 1;
    private int ZONG_COUNT = 1;
    private int UFONE_COUNT = 1;
    private int jazzSegregationMechanism = 1;


    @Autowired
    public ChargingService(TelenorCharging telenorCharging) {
        this.telenorCharging = telenorCharging;
    }

    public Response processRequest(ChargeRequestProperties req) throws Exception {
        operator_id = req.getOperatorId();
        if (operator_id == startConfiguration.getTelenor()) {
            response = telenorCharging.chargeRequest(req);
        } else if (operator_id == startConfiguration.getJazz()) {
            try {
                response = jazzCharging.jazzChargeRequest(req);
            }catch (Exception e){
                logger.error("Exception Here "+req.getMsisdn());
            }
        } else if (operator_id == startConfiguration.getWarid()) {

        } else if (operator_id == startConfiguration.getZong()) {
            try {
                response = zongCharging.sendChargingRequest(req);
            }catch (Exception e){
                logger.error("Exception Here "+req.getMsisdn());
            }
        } else {

        }
        return response;
    }

    public MoResponse processMoChargeRequest(MoRequestProperties req) throws InterruptedException {

        if (req.getOperatorId() == startConfiguration.getJazz() ||
                req.getOperatorId() == startConfiguration.getWarid()) {
            moResponse = processJazz(req);
        } else if (req.getOperatorId() == startConfiguration.getUfone()) {

        } else if (req.getOperatorId() == startConfiguration.getZong()) {
            zongMoCharging.sendChargingRequest(req);
        } else {

        }

        return moResponse;
    }

    private MoResponse processJazz(MoRequestProperties req) throws InterruptedException {

        switch (jazzSegregationMechanism) {
            case 1:
                if (ucipCount == startConfiguration.getTrafficPercentage(startConfiguration.getChargingMechanism("UCIP"))
                        || startConfiguration.getDailyCap(startConfiguration.getChargingMechanism("UCIP")) == ucipDailyCap) {
                    ucipCount = 0;
                    jazzSegregationMechanism = startConfiguration.getChargingMechanism("UCIP");
                    processJazz(req);
                } else {
                    moResponse = ucipRequest.ucipRequest(req);
                    moResponse.setChargingMechanism(startConfiguration.getChargingMechanism("UCIP"));
                    ucipDailyCap++;
                    ucipCount++;
                }
                break;
            case 2:
                if (dotCount == startConfiguration.getTrafficPercentage(startConfiguration.getChargingMechanism("DOT"))
                        || startConfiguration.getDailyCap(startConfiguration.getChargingMechanism("DOT")) == dotDailyCap) {
                    dotCount = 0;
                    jazzSegregationMechanism = startConfiguration.getChargingMechanism("DOT");
                    processJazz(req);
                } else {
                    //dot
                    moResponse.setChargingMechanism(startConfiguration.getChargingMechanism("DOT"));
                    dotDailyCap++;
                    dotCount++;
                }
                break;
            case 3:
                break;
        }

        return moResponse;
    }

}