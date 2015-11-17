package com.tpatterson.cs.comcast.wholesale.event.poller;

import com.theplatform.cs.wholesale.event.data.api.client.ProcessingClient;
import com.theplatform.cs.wholesale.event.data.api.client.query.ByAssetReference;
import com.theplatform.cs.wholesale.event.object.Processing;
import com.theplatform.data.api.client.query.Query;
import com.theplatform.test.modules.conditionpoller.ConditionChecker;
import com.theplatform.test.modules.conditionpoller.ConditionStatus;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class ProcessingByAssetReferenceExistsChecker  implements ConditionChecker<String>
{
    private  ProcessingClient processingClient;
    private URI assetReference;
    private Date lastModifiedDate;

    private Processing processingWithAssetReference;

    public ProcessingByAssetReferenceExistsChecker(ProcessingClient processingClient, URI assetReference, Date lastModifiedDate)
    {
        this.processingClient = processingClient;
        this.assetReference = assetReference;
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public ConditionStatus<String> checkCondition()
    {
        Query[] byAssetRef = new Query[] {new ByAssetReference(assetReference)};
        List<Processing> processingList = processingClient.getAll(null, byAssetRef,
            null, null, false).getEntries();

        boolean wasFound = !processingList.isEmpty();

        String additionalInfo = (wasFound) ?
            String.format("Found Processing with assetReference:%s.", assetReference) :
            String.format("Did not find Processing with assetReference:%s.", assetReference);

        if (wasFound)
        {
            processingWithAssetReference = processingList.get(0);
        }

        // check last modified date
        if (lastModifiedDate!=null)
        {
            if (!processingWithAssetReference.getUpdated().after(lastModifiedDate))
            {
                wasFound = false;
                processingWithAssetReference = null;
                additionalInfo = String.format("Found Processing with assetReference:%s, but updated was before:%s", assetReference, lastModifiedDate);
            }
        }

        return new ConditionStatus<>(wasFound, additionalInfo);
    }

    public Processing getProcessingWithAssetReference()
    {
        return processingWithAssetReference;
    }
}
