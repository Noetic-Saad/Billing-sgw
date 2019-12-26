package com.noetic.sgw.billing.sgwbilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.noetic.sgw.billing.sgwbilling.controller.BillingController;
import com.noetic.sgw.billing.sgwbilling.request.JazzCharging;
import com.noetic.sgw.billing.sgwbilling.request.TelenorCharging;
import com.noetic.sgw.billing.sgwbilling.util.ChargeRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.Response;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Service
public
class ChargingService {

    Logger logger = LoggerFactory.getLogger(TelenorCharging.class);
    private TelenorCharging telenorCharging;
    @Autowired
    private JazzCharging jazzCharging;
    private long operator_id;
    Response response = null;

    @Autowired
    public ChargingService(TelenorCharging telenorCharging) {
        this.telenorCharging = telenorCharging;
    }
    public Response processRequest(ChargeRequestProperties req) throws JsonProcessingException {
        operator_id = req.getOperatorId();
            if (operator_id == 1) {
                response = telenorCharging.chargeRequest(req);
            } else if (operator_id == 2) {
               response = jazzCharging.jazzChargeRequest(req);
            } else if (operator_id == 3) {

            } else if (operator_id == 4) {

            } else {

            }
        System.out.println("Changing Exception"+response);
        return response;
    }
}
