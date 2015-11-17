package com.tpatterson.cs.comcast.wholesale.event.poller;

import com.theplatform.data.api.client.DataServiceClient;
import com.theplatform.data.api.client.RequestParameters;
import com.theplatform.data.api.exception.ObjectNotFoundException;
import com.theplatform.data.api.objects.DataObject;
import com.theplatform.test.modules.conditionpoller.ConditionChecker;
import com.theplatform.test.modules.conditionpoller.ConditionStatus;
import org.apache.commons.lang.RandomStringUtils;

import java.net.URI;
import java.util.Date;

public class DataObjectWithGuidExistsChecker<T extends DataObject> implements ConditionChecker<String>
{
    private DataServiceClient<T> dataServiceClient;
    private URI ownerId;
    private String guid;
    private Date lastModifiedDate;

    private T domainObjectWithGuid;

    public DataObjectWithGuidExistsChecker(DataServiceClient<T> dataServiceClient, String guid, URI ownerId, Date lastModifiedDate)
    {
        this.dataServiceClient = dataServiceClient;
        this.guid = guid;
        this.ownerId = ownerId;
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public ConditionStatus<String> checkCondition()
    {
        try
        {
            // add request params to break cache
            RequestParameters requestParameters = new RequestParameters();
            requestParameters.setCorrelationId(RandomStringUtils.randomAlphabetic(8));

            domainObjectWithGuid = dataServiceClient.getByGuid(guid, ownerId, null, requestParameters);
        }
        catch(ObjectNotFoundException ignored)
        {
        }

        String objectName = dataServiceClient.getDataObjectClass().getSimpleName();

        String additionalInfo = (domainObjectWithGuid == null ?
            String.format("Did not find %s for account:%s with guid:%s.", objectName, ownerId, guid) :
            String.format("Found %s for account:%s with guid:%s.", objectName, ownerId, guid)
        );

        // check modified date if provided
        if (lastModifiedDate!=null && domainObjectWithGuid !=null)
        {
            if (!domainObjectWithGuid.getUpdated().after(lastModifiedDate))
            {
                domainObjectWithGuid = null;
                additionalInfo = String.format("Found %s for account:%s with guid:%s, but updated was before:%s", objectName, ownerId, guid,
                    lastModifiedDate);
            }
        }

        return new ConditionStatus<>(domainObjectWithGuid != null, additionalInfo);
    }

    T getObjectWithGuid()
    {
        return domainObjectWithGuid;
    }
}
