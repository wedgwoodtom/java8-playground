package com.tpatterson.cs.comcast.wholesale.event.poller;

import com.theplatform.cs.wholesale.event.data.api.client.DeliveryClient;
import com.theplatform.cs.wholesale.event.data.api.client.query.ByObjectId;
import com.theplatform.cs.wholesale.event.object.Delivery;
import com.theplatform.data.api.client.query.Query;
import com.theplatform.test.modules.conditionpoller.ConditionChecker;
import com.theplatform.test.modules.conditionpoller.ConditionStatus;

import java.net.URI;
import java.util.List;

/**
 * @author jason.horrocks
 */
public class DeliveryExistsChecker implements ConditionChecker<String>
{
    private DeliveryClient deliveryClient;
    private URI pitchTaskId;

    private Delivery delivery;

    public DeliveryExistsChecker(DeliveryClient deliveryClient, URI pitchTaskId)
    {
        this.deliveryClient = deliveryClient;
        this.pitchTaskId = pitchTaskId;
    }

    @Override
    public ConditionStatus<String> checkCondition()
    {
        ConditionStatus<String> conditionStatus = new ConditionStatus<>();

        List<Delivery> deliverys = deliveryClient.getAll(null, new Query[] { new ByObjectId(pitchTaskId) }, null, null, false).getEntries();

        if (deliverys.size() == 0)
        {
            delivery = null;
            conditionStatus.setIsConditionMet(false);
            conditionStatus.setInfo(String.format("Did not find Delivery with objectId of [%s].", pitchTaskId.toString()));
        }
        else
        {
            delivery = deliverys.get(0);
            conditionStatus.setIsConditionMet(true);
            conditionStatus.setInfo(
                    String.format("Found Delivery with id of [%s] with objectId of [%s] ", delivery.getId().toString(), pitchTaskId.toString())
            );
        }

        return conditionStatus;
    }

    public Delivery getDelivery()
    {
        return delivery;
    }
}