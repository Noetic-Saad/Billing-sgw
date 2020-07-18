package com.noetic.sgw.billing.sgwbilling.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.noetic.sgw.billing.sgwbilling.service.ChargingService;
import com.noetic.sgw.billing.sgwbilling.util.ChargeRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.MoRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.MoResponse;
import com.noetic.sgw.billing.sgwbilling.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mocharge")
public
class MoBillingController {

    @Autowired
    private ChargingService chargingService;

    @PostMapping
    public MoResponse chargeRequest(@RequestBody MoRequestProperties req) throws Exception {
        System.out.println("Mo Charge Request");
        return chargingService.processMoChargeRequest(req);
    }
}
