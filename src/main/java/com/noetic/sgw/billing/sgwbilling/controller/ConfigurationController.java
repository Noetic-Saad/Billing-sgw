package com.noetic.sgw.billing.sgwbilling.controller;

import com.noetic.sgw.billing.sgwbilling.config.StartConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {

    @Autowired
    StartConfiguration startConfiguration;

    @RequestMapping(value = "/refresh",method = RequestMethod.GET)
    public void refreshConfigurations(){
        startConfiguration.loadOperatorPlan();
        startConfiguration.loadOperator();
        startConfiguration.loadChargingMechanism();
    }
}
