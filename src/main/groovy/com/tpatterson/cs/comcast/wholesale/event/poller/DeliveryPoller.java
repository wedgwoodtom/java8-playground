package com.tpatterson.cs.comcast.wholesale.event.poller;

import com.theplatform.cs.wholesale.event.data.api.client.DeliveryClient;
import com.theplatform.cs.wholesale.event.object.Delivery;
import com.theplatform.test.modules.conditionpoller.ConditionNotMetException;
import com.theplatform.test.modules.conditionpoller.ConditionPollerFactory;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Date;

public class DeliveryPoller
{
    @Resource
    DeliveryClient deliveryClient;

    @Resource
    URI testAccount;
    @Resource
    Long pollingIntervalMillis;
    @Resource
    Long pollingTimeoutMillis;

    public Delivery waitUntilDeliveryWithObjectIdExists(URI pitchTaskId) throws ConditionNotMetException
    {
        DeliveryExistsChecker deliveryExistsChecker = new DeliveryExistsChecker(deliveryClient, pitchTaskId);
        ConditionPollerFactory.createConditionPollerInstance(deliveryExistsChecker,pollingIntervalMillis, pollingTimeoutMillis).waitUntilConditionMet();
        return deliveryExistsChecker.getDelivery();
    }
}