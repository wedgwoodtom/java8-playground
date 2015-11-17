package com.tpatterson.cs.comcast.wholesale.event.poller

import com.theplatform.cs.wholesale.event.data.api.client.ProcessingClient
import com.theplatform.cs.wholesale.event.object.Processing
import com.theplatform.test.modules.conditionpoller.ConditionNotMetException
import com.theplatform.test.modules.conditionpoller.ConditionPollerFactory

import javax.annotation.Resource

class ProcessingPoller
{
    @Resource
    ProcessingClient processingClient

    @Resource
    URI testAccount
    @Resource
    Long pollingIntervalMillis;
    @Resource
    Long pollingTimeoutMillis;


    public Processing waitUntilProcessingExists(URI assetReference) throws ConditionNotMetException
    {
        ProcessingByAssetReferenceExistsChecker processingByAssetReferenceChecker = new ProcessingByAssetReferenceExistsChecker(processingClient, assetReference, null);
        ConditionPollerFactory.createConditionPollerInstance(processingByAssetReferenceChecker, pollingIntervalMillis, pollingTimeoutMillis)
            .waitUntilConditionMet();
        return processingByAssetReferenceChecker.getProcessingWithAssetReference();
    }

    public Processing waitUntilProcessingIsUpdated(URI assetReference, Date lastUpdate) throws ConditionNotMetException
    {
        ProcessingByAssetReferenceExistsChecker processingByAssetReferenceChecker = new ProcessingByAssetReferenceExistsChecker(processingClient, assetReference, lastUpdate);
        ConditionPollerFactory.createConditionPollerInstance(processingByAssetReferenceChecker, pollingIntervalMillis, pollingTimeoutMillis)
            .waitUntilConditionMet();
        return processingByAssetReferenceChecker.getProcessingWithAssetReference();
    }
}