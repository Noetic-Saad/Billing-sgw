package com.noetic.sgw.billing.sgwbilling.controller;

import com.noetic.sgw.billing.sgwbilling.service.DcbChargingService;
import com.noetic.sgw.billing.sgwbilling.util.DcbApiResponse;
import com.noetic.sgw.billing.sgwbilling.util.DcbChargeRequestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dcb-charge")
public class DcbController {

    @Autowired private DcbChargingService dcbChargingService;

    @PostMapping
    public DcbApiResponse chargeRequest(@RequestBody DcbChargeRequestProperties req) throws Exception {
        return dcbChargingService.processRequest(req);
    }
}
