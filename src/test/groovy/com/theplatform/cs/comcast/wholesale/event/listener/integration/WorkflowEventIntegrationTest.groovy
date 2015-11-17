package com.theplatform.cs.comcast.wholesale.event.listener.integration

import com.tpatterson.cs.comcast.wholesale.event.listener.integration.spring.IntegrationTestConfiguration
import com.tpatterson.cs.comcast.wholesale.event.poller.ProcessingPoller
import com.theplatform.cs.wholesale.event.data.api.client.ProcessingClient
import com.theplatform.cs.wholesale.event.object.Processing
import com.theplatform.profile.data.workflow.api.client.ProfileResultClient
import com.theplatform.profile.data.workflow.api.objects.ProfileResult
import com.theplatform.profile.data.workflow.api.objects.WorkflowStatus
import org.apache.commons.lang.RandomStringUtils
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource


@ContextConfiguration(classes = [IntegrationTestConfiguration, ProcessingPoller])
class WorkflowEventIntegrationTest extends AbstractTestNGSpringContextTests
{
    public static final String PUBLISH_PROFILE_TITLE = "Test Profile for Event DS Processing"
    public static final String OUTLET_PROFILE_TITLE = "Test Outlet Profile for Event DS Processing"
    public static final String PUBLISH = "Publish"
    public static final String SHARING = "Sharing"
    public static final String DELETED = 'Deleted'

    @Resource
    ProcessingClient processingClient
    @Resource
    ProfileResultClient profileResultClient
    @Resource
    ProcessingPoller processingPoller

    @Resource
    URI testAccount
    @Resource
    URI outletProfileId
    @Resource
    URI publishProfileId

    ProfileResult testProfileResult

    List<ProfileResult> createdProfileResultObjects = []

    @BeforeMethod
    public void setup()
    {
        // create a new testProfileResult
        testProfileResult = createProfileResult(createTestProfileResult());

        // sleep in order to prevent create/update in less than one second
        sleep(1000)
    }

    @AfterClass
    void deleteProfileResultObjects()
    {
        URI[] toDelete = createdProfileResultObjects.collect({ ProfileResult p -> p.id }) as URI[]
        if (toDelete.length>0)
        {
            profileResultClient.delete(toDelete)
        }
    }

    @Test
    public void testProfileResultCreate()
    {
        Processing correspondingProcessing = processingPoller.waitUntilProcessingExists(testProfileResult.mediaId)
        validate(correspondingProcessing, testProfileResult)
    }

    @Test
    public void testProfileResultProfileIdBasedTitle()
    {
        Processing originalProcessing = processingPoller.waitUntilProcessingExists(testProfileResult.mediaId)
        assert originalProcessing.title == PUBLISH_PROFILE_TITLE

        testProfileResult.profileId = outletProfileId
        profileResultClient.update(testProfileResult)
        testProfileResult = profileResultClient.get(testProfileResult.id, null)

        Processing updatedProcessing = processingPoller.waitUntilProcessingIsUpdated(testProfileResult.mediaId, originalProcessing.updated)
        validate(updatedProcessing, testProfileResult)
        assert updatedProcessing.title == OUTLET_PROFILE_TITLE
    }

    @Test
    public void testProfileResultEventType()
    {
        Processing originalProcessing = processingPoller.waitUntilProcessingExists(testProfileResult.mediaId)
        validate(originalProcessing, testProfileResult)
        assert originalProcessing.eventType == PUBLISH

        testProfileResult.profileId = outletProfileId
        profileResultClient.update(testProfileResult)
        testProfileResult = profileResultClient.get(testProfileResult.id, null)

        Processing updatedProcessing = processingPoller.waitUntilProcessingIsUpdated(testProfileResult.mediaId, originalProcessing.updated)
        validate(updatedProcessing, testProfileResult)
        assert updatedProcessing.eventType == SHARING
    }

    @Test
    public void testProfileResultStatus()
    {
        // WorkflowStatus starts out Processing in the test setup

        // createEventTimestamp
        Processing originalProcessing = processingPoller.waitUntilProcessingExists(testProfileResult.mediaId)
        validate(originalProcessing, testProfileResult)
        assert originalProcessing.createEventTimestamp == testProfileResult.updated
        assert originalProcessing.completeEventTimestamp == null

        Date originalUpdated = testProfileResult.updated
        testProfileResult.status = WorkflowStatus.Revoked
        testProfileResult.profileId = publishProfileId
        profileResultClient.update(testProfileResult)
        testProfileResult = profileResultClient.get(testProfileResult.id, null)

        Processing updatedProcessing = processingPoller.waitUntilProcessingIsUpdated(testProfileResult.mediaId, originalProcessing.updated)
        validate(updatedProcessing, testProfileResult)
        assert updatedProcessing.createEventTimestamp == originalUpdated


        // completeEventTimestamp
        testProfileResult.status = WorkflowStatus.Processed
        profileResultClient.update(testProfileResult)
        testProfileResult = profileResultClient.get(testProfileResult.id, null)

        updatedProcessing = processingPoller.waitUntilProcessingIsUpdated(testProfileResult.mediaId, updatedProcessing.updated)
        validate(updatedProcessing, testProfileResult)
        assert updatedProcessing.completeEventTimestamp == testProfileResult.updated

        originalUpdated = testProfileResult.updated
        testProfileResult.status = WorkflowStatus.Revoked
        testProfileResult.profileId = outletProfileId
        profileResultClient.update(testProfileResult)
        testProfileResult = profileResultClient.get(testProfileResult.id, null)
        // ensure no update
        assert updatedProcessing.completeEventTimestamp == originalUpdated
    }

    @Test
    public void testProfileResultDelete()
    {
        Processing correspondingProcessing = processingPoller.waitUntilProcessingExists(testProfileResult.mediaId)
        assert correspondingProcessing.status != DELETED
        profileResultClient.delete(testProfileResult.id)
        createdProfileResultObjects.remove(testProfileResult)

        Processing updatedProcessing = processingPoller.waitUntilProcessingIsUpdated(testProfileResult.mediaId, correspondingProcessing.updated)
        validate(updatedProcessing, testProfileResult)

        assert correspondingProcessing.id == updatedProcessing.id
        assert updatedProcessing.status == DELETED
    }

    ProfileResult createProfileResult(ProfileResult profileResultToPost)
    {
        ProfileResult postedProfileResult = profileResultClient.create(profileResultToPost, [] as String[])
        createdProfileResultObjects << postedProfileResult
        return postedProfileResult
    }

    ProfileResult createTestProfileResult()
    {
        return new ProfileResult(
            title: "Event Service Integration Test ProfileResult for ${InetAddress.localHost.hostName}",
            guid: RandomStringUtils.randomAlphabetic(8),
            mediaId: URI.create("http://data.media.sandbox.theplatform.com/media/data/Media/RND_"+RandomStringUtils.randomAlphabetic(8)),
            profileId: publishProfileId,
            service: "publish",
            status: WorkflowStatus.Processing,
            statusInfo: "test status information",
            ownerId: testAccount
        )
    }

    static void validate(Processing processing, ProfileResult profileResult)
    {
        assert processing != null
        assert profileResult != null
        assert processing.ownerId == profileResult.ownerId
        assert processing.assetReference == profileResult.mediaId

        boolean isOutletProfile = profileResult.profileId.toString().contains("OutletProfile")

        String expectedTitle =  isOutletProfile ? OUTLET_PROFILE_TITLE : PUBLISH_PROFILE_TITLE
        assert processing.title == expectedTitle

        assert processing.objectId == profileResult.id

        String expectedEventType = isOutletProfile ? SHARING : PUBLISH
        assert processing.eventType == expectedEventType

        if (processing.status != DELETED)
        {
            assert processing.status == profileResult.status.toString()
        }
        assert processing.statusInfo == profileResult.statusInfo
        assert processing.statusTimestamp == profileResult.updated
    }

}
