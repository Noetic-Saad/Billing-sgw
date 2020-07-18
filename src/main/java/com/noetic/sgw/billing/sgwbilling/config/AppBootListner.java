package com.noetic.sgw.billing.sgwbilling.config;

import com.noetic.sgw.billing.sgwbilling.request.ZongMMLRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppBootListner implements ApplicationListener<ApplicationReadyEvent> {

    private ZongMMLRequest zongMMLRequest = new ZongMMLRequest();

    @Autowired
    StartConfiguration startConfiguration;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        zongMMLRequest.serverConnection();
        zongMMLRequest.logIn();
        zongMMLRequest.heartBeatScheduler();
        startConfiguration.loadChargingMechanism();
        startConfiguration.loadOperator();
        startConfiguration.loadOperatorPlan();
        startConfiguration.loadResponseTypes();
        startConfiguration.loadTestMsisdns();

    }
}
