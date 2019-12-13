package com.noetic.sgw.billing.sgwbilling.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.noetic.sgw.billing.sgwbilling.service.ChargingService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@RestController
@RequestMapping("/charge")
public
class BillingController {

    @Autowired
    private ChargingService chargingService;

    @PostMapping
    public HttpResponse<JsonNode> chargeRequest(HttpServletRequest req) throws JsonProcessingException {
        return chargingService.processRequest(req);
    }
}
