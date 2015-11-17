package com.tpatterson.cs.comcast.wholesale.event.poller;

import com.theplatform.cs.wholesale.event.data.api.client.AssetClient;
import com.theplatform.cs.wholesale.event.object.Asset;
import com.theplatform.test.modules.conditionpoller.ConditionNotMetException;
import com.theplatform.test.modules.conditionpoller.ConditionPollerFactory;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Date;

public class AssetPoller
{
    @Resource
    AssetClient assetClient;

    @Resource
    URI testAccount;
    @Resource
    Long pollingIntervalMillis;
    @Resource
    Long pollingTimeoutMillis;

    public Asset waitUntilAssetExists(String guid) throws ConditionNotMetException
    {
        com.tpatterson.cs.comcast.wholesale.event.poller.DataObjectWithGuidExistsChecker<Asset> assetWithGuidChecker = new com.tpatterson.cs.comcast.wholesale.event.poller.DataObjectWithGuidExistsChecker<>(assetClient, guid, testAccount, null);
        ConditionPollerFactory.createConditionPollerInstance(assetWithGuidChecker, pollingIntervalMillis, pollingTimeoutMillis)
            .waitUntilConditionMet();
        return assetWithGuidChecker.getObjectWithGuid();
    }

    public Asset waitUntilAssetIsUpdated(String guid, Date lastUpdate) throws ConditionNotMetException
    {
        com.tpatterson.cs.comcast.wholesale.event.poller.DataObjectWithGuidExistsChecker<Asset> assetWithGuidChecker = new com.tpatterson.cs.comcast.wholesale.event.poller.DataObjectWithGuidExistsChecker<>(assetClient, guid, testAccount, lastUpdate);
        ConditionPollerFactory.createConditionPollerInstance(assetWithGuidChecker, pollingIntervalMillis, pollingTimeoutMillis)
            .waitUntilConditionMet();
        return assetWithGuidChecker.getObjectWithGuid();
    }
}