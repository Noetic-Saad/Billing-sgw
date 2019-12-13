package com.noetic.sgw.billing.sgwbilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.noetic.sgw.billing.sgwbilling.controller.BillingController;
import com.noetic.sgw.billing.sgwbilling.request.TelenorCharging;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public
class ChargingService {

    Logger logger = LoggerFactory.getLogger(TelenorCharging.class);
    private TelenorCharging telenorCharging;
    private int operator_id = 1;
    HttpResponse<JsonNode> response = null;

    @Autowired
    public ChargingService(TelenorCharging telenorCharging) {
        this.telenorCharging = telenorCharging;
    }
    public HttpResponse<JsonNode> processRequest(HttpServletRequest req) {
        try {
            if (operator_id == 1) {
                response = telenorCharging.chargeRequest(req);
            } else if (operator_id == 2) {

            } else if (operator_id == 3) {

            } else if (operator_id == 4) {

            } else {

            }
        } catch (Exception e) {
            logger.error("Exception Caught while Sending Request: " + e.getCause());
        }
        return response;
    }
}
