package com.theplatform.cs.comcast.wholesale.event.listener.integration

import com.tpatterson.cs.comcast.wholesale.event.listener.integration.spring.IntegrationTestConfiguration
import com.tpatterson.cs.comcast.wholesale.event.poller.DeliveryPoller
import com.theplatform.cs.wholesale.event.data.api.client.DeliveryClient
import com.theplatform.cs.wholesale.event.object.Delivery
import com.theplatform.cs.wholesale.pitch.data.api.client.PitchTaskClient
import com.theplatform.cs.wholesale.pitch.data.api.client.ReceiverClient
import com.theplatform.cs.wholesale.pitch.data.api.fields.PitchTaskField
import com.theplatform.cs.wholesale.pitch.object.FileObjectInfo
import com.theplatform.cs.wholesale.pitch.object.PitchState
import com.theplatform.cs.wholesale.pitch.object.PitchTask
import com.theplatform.cs.wholesale.pitch.object.Receiver
import com.theplatform.media.api.client.MediaClient
import com.theplatform.test.conditionpoller.extensions.mpx.DataObjectFieldComparisonPoller
import org.apache.commons.lang.RandomStringUtils
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert

import javax.annotation.Resource

@ContextConfiguration(classes = [IntegrationTestConfiguration])
class PitchEventIntegrationTest extends AbstractTestNGSpringContextTests
{
    public static final String DELETED = 'Deleted'

    @Resource
    URI testAccount
    @Resource
    String eventDataServiceUrl

    @Resource
    PitchTaskClient pitchTaskClient
    @Resource
    DeliveryClient deliveryClient
    @Resource
    ReceiverClient receiverClient
    @Resource
    MediaClient mediaClient
    @Resource
    DeliveryPoller deliveryPoller
    @Resource
    DataObjectFieldComparisonPoller<Delivery> deliveryCheckerPoller

    static Receiver primaryReceiver
    static Receiver secondaryReceiver
    List<PitchTask> createdPitchTasks = []
    synchronized List<Delivery> deliverys = []

    @AfterClass(alwaysRun = true)
    void deletePitchTasks()
    {
        URI[] pitchTasksToDelete = createdPitchTasks.collect() { PitchTask pitchTask -> pitchTask.id } as URI[]
        if (pitchTasksToDelete.length > 0)
        {
            pitchTaskClient.delete(pitchTasksToDelete)
        }
    }

    @AfterClass(alwaysRun = true)
    void deleteReceivers()
    {
        receiverClient.delete(primaryReceiver.id as URI[])
        receiverClient.delete(secondaryReceiver.id as URI[])
    }

    @AfterClass(alwaysRun = true)
    void deleteDeliverys()
    {
        URI[] deliverysToDelete = deliverys.collect() { Delivery delivery -> delivery.id } as URI[]
        if (deliverysToDelete.length > 0)
        {
            deliveryClient.delete(deliverysToDelete)
        }
    }

    @BeforeClass(alwaysRun = true)
    public void createReceiver()
    {
        //create a new Receiver
        primaryReceiver = receiverClient.create(new Receiver(title: "Test Receiver " + RandomStringUtils.randomNumeric(10)), [] as String[])
        secondaryReceiver = receiverClient.create(new Receiver(title: "Test Receiver " + RandomStringUtils.randomNumeric(10)), [] as String[])
    }

    @Test
    public void testPitchTaskCreate()
    {
        PitchTask testPitchTask = createPitchTask(generatePitchTask())

        Delivery expectedDelivery = new Delivery()
        setExpectedDeliveryFields(expectedDelivery, testPitchTask)

        Delivery createdDelivery = deliveryPoller.waitUntilDeliveryWithObjectIdExists(testPitchTask.id)

        deliveryCheckerPoller.waitUntilObjectFieldsEqual(createdDelivery.id, expectedDelivery);
        deliverys << createdDelivery
    }

    @DataProvider
    public Object[][] pitchFieldsToUpdate()
    {
        return [
                [PitchTaskField.title.localName],
                [PitchTaskField.mediaGuid.localName],
                [PitchTaskField.state.localName],
                [PitchTaskField.pitchWindowEnd.localName],
                [PitchTaskField.pitchWindowStart.localName],
                [PitchTaskField.remainingAttempts.localName],
                [PitchTaskField.receivingSiteGuid.localName]
        ]
    }

    /**
     * https://theplatform.jira.com/browse/COMWHLDEV-1857:
     * if pitchTask.hadError is false and pitchTask.state is COMPLETED
     * set Delivery.status to Complete
     */
    @Test
    public void testPitchTaskCompletion()
    {
        //create pitch task and its corresponding Delivery object
        testPitchTaskCreate()

        PitchTask pitchTask = createdPitchTasks[-1]
        Delivery delivery = deliverys[-1]

        pitchTask.state = PitchState.COMPLETED
        pitchTask.hadError = false

        Delivery expectedDelivery = new Delivery(status: 'Complete')
        pitchTask = pitchTaskClient.update(pitchTask, [] as String[])
        setExpectedDeliveryFields(expectedDelivery, pitchTask)

        deliveryCheckerPoller.waitUntilObjectFieldsEqual(delivery.id, expectedDelivery)
    }

    /**
     * Verify we set the delivery status to Error regardless of
     * whether the pitch task is complete
     */
    @Test
    public void testPitchTaskCompletionWithErrors()
    {
        //create pitch task and its corresponding Delivery object
        testPitchTaskCreate()

        PitchTask pitchTask = createdPitchTasks[-1]
        Delivery delivery = deliverys[-1]

        pitchTask.state = PitchState.COMPLETED
        pitchTask.hadError = true

        Delivery expectedDelivery = new Delivery(status: 'Error')
        pitchTask = pitchTaskClient.update(pitchTask, [] as String[])
        setExpectedDeliveryFields(expectedDelivery, pitchTask)

        deliveryCheckerPoller.waitUntilObjectFieldsEqual(delivery.id, expectedDelivery)
    }

    //Test to cover simple one-to-one field changes between
    @Test(dataProvider = 'pitchFieldsToUpdate')
    public void testPitchTaskUpdatedField(String field)
    {
        PitchTask testPitchTask = createPitchTask(generatePitchTask())

        //wait for delivery to exist
        Delivery createdDelivery = deliveryPoller.waitUntilDeliveryWithObjectIdExists(testPitchTask.id)

        //update pitchTask locally
        updatePitchTaskField(field, testPitchTask)

        //create expected delivery for update
        Delivery expectedDelivery = new Delivery()
        setExpectedDeliveryFields(expectedDelivery, testPitchTask)

        //update pitch task in DS
        pitchTaskClient.update(testPitchTask)

        //check that corresponding Delivery update succeeded
        deliveryCheckerPoller.waitUntilObjectFieldsEqual(createdDelivery.id, expectedDelivery)
        deliverys << createdDelivery
    }

    @Test
    public void testCompleteEventTimestamp()
    {
        PitchTask testPitchTask = createPitchTask(generatePitchTask())

        //wait for delivery to exist
        Delivery createdDelivery = deliveryPoller.waitUntilDeliveryWithObjectIdExists(testPitchTask.id)

        // update state
        testPitchTask.state = PitchState.COMPLETED

        // update in DS
        pitchTaskClient.update(testPitchTask)
        PitchTask updatedPitchTask = pitchTaskClient.get(testPitchTask.id)

        // Set up expected delivery.
        Delivery expectedDelivery = new Delivery()
        setExpectedDeliveryFields(expectedDelivery, testPitchTask)
        // Todo create a condition poller which checks notifications for a field state change and returns the timestamp value.
        expectedDelivery.completeEventTimestamp = updatedPitchTask.updated

        deliveryCheckerPoller.waitUntilObjectFieldsEqual(createdDelivery.id, expectedDelivery);
        deliverys << createdDelivery
    }

    @Test
    public void testTransmitQueuedTimestamp()
    {
        PitchTask testPitchTask = createPitchTask(generatePitchTask())

        //wait for delivery to exist
        Delivery createdDelivery = deliveryPoller.waitUntilDeliveryWithObjectIdExists(testPitchTask.id)

        // update state
        testPitchTask.state = PitchState.TRANSMIT_QUEUED

        // update in DS
        pitchTaskClient.update(testPitchTask)
        PitchTask updatedPitchTask = pitchTaskClient.get(testPitchTask.id)

        // Set up expected delivery.
        Delivery expectedDelivery = new Delivery()
        setExpectedDeliveryFields(expectedDelivery, testPitchTask)
        expectedDelivery.transmitQueuedTimestamp = updatedPitchTask.updated

        // check
        deliveryCheckerPoller.waitUntilObjectFieldsEqual(createdDelivery.id, expectedDelivery);
        deliverys << createdDelivery
    }

    @Test
    public void testPitchTaskDelete()
    {
        PitchTask testPitchTask = createPitchTask(generatePitchTask())
        Delivery createdDelivery = deliveryPoller.waitUntilDeliveryWithObjectIdExists(testPitchTask.id)

        pitchTaskClient.delete(testPitchTask.id)
        createdPitchTasks.remove(testPitchTask)

        Delivery expectedDelivery = new Delivery()
        expectedDelivery.status = DELETED

        deliveryCheckerPoller.waitUntilObjectFieldsEqual(createdDelivery.id, expectedDelivery);
        deliverys << createdDelivery
    }

    @Test
    public void testNullFieldUpdate()
    {
        PitchTask testPitchTask = createPitchTask(generatePitchTask())
        testPitchTask.processingStatusMessage = null
        testPitchTask.setNull(PitchTaskField.processingStatusMessage)
        testPitchTask.pitchWindowEnd = null
        testPitchTask.setNull(PitchTaskField.pitchWindowEnd)
        testPitchTask.pitchWindowStart = null
        testPitchTask.setNull(PitchTaskField.pitchWindowStart)
        pitchTaskClient.update(testPitchTask)

        // Verify that the fields were set to null in the data service
        pitchTaskClient.get(testPitchTask.id, [] as String[])
        SoftAssert checkPitchForNullFields = new SoftAssert()
        checkPitchForNullFields.assertNull(testPitchTask.processingStatusMessage)
        checkPitchForNullFields.assertNull(testPitchTask.pitchWindowEnd)
        checkPitchForNullFields.assertNull(testPitchTask.pitchWindowStart)
        checkPitchForNullFields.assertAll()

        Delivery delivery = deliveryPoller.waitUntilDeliveryWithObjectIdExists(testPitchTask.id)
        //verify that the fields are null
        assert !delivery.pitchWindowEnd
        assert !delivery.pitchWindowStart
        assert !delivery.statusInfo
    }

    ////////////////////
    // HELPER methods //
    ////////////////////

    public static void updatePitchTaskField(String field, PitchTask pitchTask)
    {
        Map<String, Closure> pitchTaskFieldUpdateClosures = [
                (PitchTaskField.title.localName)            : { PitchTask pt -> pt.title = pt.title + "updated" },
                (PitchTaskField.mediaGuid.localName)        : { PitchTask pt -> pt.mediaGuid = RandomStringUtils.randomAlphabetic(16) },
                (PitchTaskField.state.localName)            : {
                    PitchTask pt -> pt.state = ((PitchState.values() - pt.state) as List<PitchState>).get(0)
                },
                (PitchTaskField.pitchWindowEnd.localName)   : { PitchTask pt -> pt.pitchWindowEnd = pt.pitchWindowEnd + 1 },
                (PitchTaskField.pitchWindowStart.localName) : { PitchTask pt -> pt.pitchWindowStart = pt.pitchWindowStart + 1 },
                (PitchTaskField.remainingAttempts.localName): { PitchTask pt -> pt.remainingAttempts = pt.remainingAttempts - 1 },
                (PitchTaskField.receivingSiteGuid.localName): { PitchTask pt ->
                    pt.receivingSiteGuid =
                            (pt.receivingSiteGuid.equals(primaryReceiver.guid)) ? secondaryReceiver.guid : primaryReceiver.guid;
                },
        ]

        //Apply the appropriate update
        (pitchTaskFieldUpdateClosures.get(field))(pitchTask)
    }

    /*
    * This populates a delivery object with the fields that are a one-to-one mapping
    * with the fields in a pitch task.
    * The pitchTask parameter must be a pitch task that was created in the DS, to
    * ensure that any date truncation has already occurred.
    * */
    public static void setExpectedDeliveryFields(Delivery expectedDelivery, PitchTask pitchTask)
    {
        expectedDelivery.fileNames = new String[1];

        expectedDelivery.title = pitchTask.title
        expectedDelivery.assetReference = URI.create(pitchTask.mediaGuid)
        expectedDelivery.eventType = 'Pitch'
        expectedDelivery.fileNames[0] = pitchTask.fileObject.fileName
        expectedDelivery.pitchTaskState = pitchTask.state
        expectedDelivery.pitchWindowStart = pitchTask.pitchWindowStart
        expectedDelivery.pitchWindowEnd = pitchTask.pitchWindowEnd
        expectedDelivery.receivingSiteGuid = pitchTask.receivingSiteGuid
        expectedDelivery.remainingAttempts = pitchTask.remainingAttempts
        expectedDelivery.objectId = pitchTask.id
        expectedDelivery.statusInfo = (pitchTask.processingStatusMessage == null) ? "" : pitchTask.processingStatusMessage
    }

    /**
     * Create PitchTask based on given non-instantiated object and store in collection for later deletion.
     * @param pitchTaskToCreate
     * @return
     */
    PitchTask createPitchTask(PitchTask pitchTaskToCreate)
    {
        PitchTask postedPitchTask = pitchTaskClient.create(pitchTaskToCreate, [] as String[])
        createdPitchTasks << postedPitchTask
        return postedPitchTask
    }

    /**
     * Populate a dense PitchTask template object for instantiation.
     * @return
     */
    PitchTask generatePitchTask()
    {
        return new PitchTask(
                title: "Event Service Integration Test PitchTask for ${InetAddress.localHost.hostName}",
                guid: RandomStringUtils.randomAlphabetic(8),
                mediaGuid: RandomStringUtils.randomAlphabetic(16),
                ownerId: testAccount,
                state: PitchState.QUEUED,
                receivingSiteGuid: primaryReceiver.guid,
                transmitModeId: "TM",
                transmitQueueId: "TQ",
                fileObject: new FileObjectInfo("C:\\kencast\\Movie.AVI"),
                pitchWindowStart: new Date() + 1,
                pitchWindowEnd: new Date() + 10,
                processingStatusMessage: "Hello, I am a processingStatusMessage."
        )
    }
}
