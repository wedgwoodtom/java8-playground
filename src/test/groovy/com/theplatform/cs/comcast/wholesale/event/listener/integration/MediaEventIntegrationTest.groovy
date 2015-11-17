package com.theplatform.cs.comcast.wholesale.event.listener.integration

import com.tpatterson.cs.comcast.wholesale.event.listener.integration.spring.IntegrationTestConfiguration
import com.tpatterson.cs.comcast.wholesale.event.poller.AssetPoller
import com.theplatform.cs.wholesale.event.object.Asset
import com.theplatform.cs.wholesale.event.object.DeliveryPriorityType
import com.theplatform.data.api.objects.FieldInfo
import com.theplatform.media.api.client.MediaClient
import com.theplatform.media.api.data.objects.Media
import com.theplatform.test.conditionpoller.extensions.mpx.DataObjectFieldComparisonPoller
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.time.DateUtils
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Integration tests for the Listener (see spec https://theplatform.jira.com/wiki/display/COMW/Comcast+Wholesale+Event+Recording+Service#ComcastWholesaleEventRecordingService-Listeners_
 *
 * Verifies that Media updates create notifications that are processed by the Listener in order to update the associated Assets in the Event DS.
 *
 */
@ContextConfiguration(classes = [IntegrationTestConfiguration, AssetPoller])
class MediaEventIntegrationTest extends AbstractTestNGSpringContextTests
{
    private static final String UPDATED_SUFFIX = "(Updated)"

    @Resource
    MediaClient mediaClient
    @Resource
    AssetPoller assetPoller

    @Resource
    URI testAccount
    @Resource
    URI originalMediaId
    @Resource
    URI programId
    @Resource
    DataObjectFieldComparisonPoller<Asset> assetCheckerPoller

    Media testMedia
    Asset testAsset

    List<Media> createdMediaObjects = []

    public static final String WHOLESALENAMESPACE = "http://wholesale.comcast.com/whsl"
    public static final FieldInfo PRICE = new FieldInfo("price", WHOLESALENAMESPACE)
    private static final String PROVIDER_MAP = "providerMap"
    private static final String PROVIDER_NAME = "provider_name"
    private static final String PROVIDER = "provider"
    private static final String PROVIDER_ID = "provider_id"

    @AfterClass
    void deleteMediaObjects()
    {
        mediaClient.delete(createdMediaObjects.collect({ Media m -> m.id }) as URI[])
    }

    @BeforeMethod
    public void setup()
    {
        // create a new testMedia
        testMedia = createMedia(createTestMedia())
        testAsset = assetPoller.waitUntilAssetExists(testMedia.guid)

        // sleep in order to prevent create/update in less than one second
        sleep(1000)
    }

    @Test
    public void testMediaCreate()
    {
        Asset expectedAsset = new Asset()
        setExpectedAssetFields(expectedAsset, testMedia)

        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testMediaUpdateAvailableDate()
    {
        testMedia.availableDate = testMedia.availableDate + 1
        mediaClient.update(new Media(id: testMedia.id, availableDate: testMedia.availableDate))

        Asset updatedAsset = assetPoller.waitUntilAssetIsUpdated(testMedia.guid, testAsset.updated)
        validate(updatedAsset, testMedia)
    }

    @Test
    public void testMediaUpdateExpirationDate()
    {
        testMedia.expirationDate = Date.from(testMedia.expirationDate.plus(5).toInstant())
        mediaClient.update(testMedia)

        Asset updatedAsset = assetPoller.waitUntilAssetIsUpdated(testMedia.guid, testAsset.updated)
        validate(updatedAsset, testMedia)
    }

    @Test
    public void testMediaUpdateTitle()
    {
        testMedia.title = testMedia.title + UPDATED_SUFFIX
        mediaClient.update(testMedia)

        Asset updatedAsset = assetPoller.waitUntilAssetIsUpdated(testMedia.guid, testAsset.updated)
        validate(updatedAsset, testMedia)
    }

    @Test
    public void testMediaUpdateProgramId()
    {
        testMedia.programId = URI.create(testMedia.programId.toString() + UPDATED_SUFFIX)
        mediaClient.update(testMedia)

        Asset updatedAsset = assetPoller.waitUntilAssetIsUpdated(testMedia.guid, testAsset.updated)
        validate(updatedAsset, testMedia)
    }

    @Test
    public void testMediaUpdateGuid()
    {
        // When we update the guid, we should have an Asset for the old guid and a new one for the new guid
        String originalGuid = testMedia.guid
        testMedia.guid = testMedia.guid + UPDATED_SUFFIX
        mediaClient.update(testMedia)

        // verify that we get a new Asset
        Asset newAsset = assetPoller.waitUntilAssetExists(testMedia.guid)
        validate(newAsset, testMedia)

        // verify old testAsset is still there
        Asset originalAsset = assetPoller.waitUntilAssetExists(originalGuid)
        assert originalAsset != null
    }

    @Test
    public void testMediaDelete()
    {
        // delete media
        mediaClient.delete(testMedia.id)
        Asset updatedAsset = assetPoller.waitUntilAssetIsUpdated(testMedia.guid, testAsset.updated)

        // verify updated Asset
        validate(updatedAsset, testMedia)
        assert updatedAsset.deleted
    }

    @Test
    public void testMediaCreateDeleteRecreate()
    {
        // Test the case where media has been ingested, is deleted, and is then re-ingested with the same guid
        // delete media
        Media deletedMedia = testMedia
        String mediaGuid = testMedia.guid
        mediaClient.delete(testMedia.id)
        Asset updatedAsset = assetPoller.waitUntilAssetIsUpdated(mediaGuid, testAsset.updated)
        validate(updatedAsset, testMedia)
        assert updatedAsset.deleted

        // create a new media with the same guid as the deleted media
        Media newMediaWithSameGuidAsDeletedMedia = createTestMedia()
        newMediaWithSameGuidAsDeletedMedia.guid = mediaGuid
        Media newMedia = createMedia(newMediaWithSameGuidAsDeletedMedia)
        updatedAsset = assetPoller.waitUntilAssetIsUpdated(mediaGuid, updatedAsset.updated)

        // verify associated asset has been updated with new media meta-data
        assert deletedMedia.guid == newMedia.guid
        assert deletedMedia.id != newMedia.id
        assert deletedMedia.title != newMedia.title
        validate(updatedAsset, newMedia)
        assert updatedAsset.deleted == false
    }

    @Test
    public void testTransactionalAfterPut()
    {
        testMedia.setCustomValue(PRICE, "1")
        mediaClient.update(testMedia)

        Asset expectedAsset = new Asset()
        expectedAsset.transactional = true

        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testTransactionalPriceZero()
    {
        Media media = createTestMedia()
        media.setCustomValue(PRICE, "0")
        media = createMedia(media)

        Asset createdAsset = assetPoller.waitUntilAssetExists(media.guid)

        Asset expectedAsset = new Asset()
        expectedAsset.transactional = false

        assetCheckerPoller.waitUntilObjectFieldsEqual(createdAsset.id, expectedAsset)
    }

    @Test
    public void testTransactionalPriceOne()
    {
        Media media = createTestMedia()
        media.setCustomValue(PRICE, "1")
        media = createMedia(media)

        Asset createdAsset = assetPoller.waitUntilAssetExists(media.guid)

        Asset expectedAsset = new Asset()
        expectedAsset.transactional = true

        assetCheckerPoller.waitUntilObjectFieldsEqual(createdAsset.id, expectedAsset)
    }

    @Test
    public void testNormalDeliveryPriority()
    {
        Asset expectedAsset = new Asset()
        setExpectedAssetFields(expectedAsset, testMedia)
        expectedAsset.deliveryPriorityType = DeliveryPriorityType.NORMAL

        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test(enabled = false)
    public void testC3DeliveryPriority()
    {
        //TODO: set provider name on the Media data object providerMap custom field
        testMedia.provider = null //avoid name consistency error
        mediaClient.update(testMedia)
        testMedia = mediaClient.get(testMedia.id, [] as String[])

        Asset expectedAsset = new Asset()
        setExpectedAssetFields(expectedAsset, testMedia)

        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testCreateMediaProviderMapHappyPath()
    {
        setProviderFields(testMedia, ['provider_id': 'provider_name', 'provider_name': 'name'] as Map<String, String>)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, 'provider', 'provider_name')
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testCreateMediaNullProviderMap()
    {
        setProviderFields(testMedia, null)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, null, null)
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testCreateMediaEmptyProviderMap()
    {
        setProviderFields(testMedia, new HashMap<String, String>(0))

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, '', '')
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testCreateMediaNoProviderName()
    {
        setProviderFields(testMedia, ['provider_id': 'provider_name'] as Map<String, String>)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, 'provider', '')
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)

    }

    @Test
    public void testCreateMediaNoProviderId()
    {
        setProviderFields(testMedia, ['provider_name': 'name'] as Map<String, String>)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, '', 'provider_name')
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)

    }

    @Test
    public void testCreateMediaLongStrings()
    {
        setProviderFields(testMedia, ['provider_id': generateRandomStringWithLength(256), 'provider_name': generateRandomStringWithLength(256)] as Map<String, String>)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, 'provider', 'provider_name')
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testCreateMediaEmptyStrings()
    {
        setProviderFields(testMedia, ['provider_id': '', 'provider_name': ''] as Map<String, String>)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, 'provider', 'provider_name')
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testCreateMediaNonASCIIChars()
    {
        setProviderFields(testMedia, ['provider_id': '!@#$%^&*()', 'provider_name': '!@#$%^&**'] as Map<String, String>)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, 'provider', 'provider_name')
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    @Test
    public void testCreateMediaNullCustomFields()
    {
        setProviderFields(testMedia, null)

        testMedia = mediaClient.update(new Media(id: testMedia.id, customValues: testMedia.customValues), [] as String[])

        Asset expectedAsset = createAssetWithProviderFields(testMedia, null, null)
        assetCheckerPoller.waitUntilObjectFieldsEqual(testAsset.id, expectedAsset)
    }

    //HELPER methods

    Media createMedia(Media mediaToPost)
    {
        Media postedMedia = mediaClient.create(mediaToPost, [] as String[])
        createdMediaObjects << postedMedia
        return postedMedia
    }

    Media createTestMedia()
    {
        Media media = new Media(
                title: "Event Service Integration Test Media " + RandomStringUtils.randomAlphabetic(8) + " for ${InetAddress.localHost.hostName}",
                guid: RandomStringUtils.randomAlphabetic(8),
                expirationDate: Date.from(new Date().plus(5).toInstant()),
                availableDate: new Date(),
                ownerId: testAccount,
                originalOwnerIds: [testAccount],
                originalMediaIds: [originalMediaId],
                programId: programId
        )
        media
    }

    String generateRandomStringWithLength(int n)
    {
        UUID.randomUUID().toString().replaceAll('-', '')
    }

    void setProviderFields(Media media, Map providerMap)
    {
        media.setCustomValue(WHOLESALENAMESPACE, 'providerMap', providerMap)
    }

    void setExpectedAssetFields(Asset expectedAsset, Media media)
    {
        expectedAsset.mediaId = media.id
        expectedAsset.availableDate = media.availableDate
        expectedAsset.expirationDate = media.expirationDate
        expectedAsset.title = media.title
        expectedAsset.ownerId = media.ownerId
        expectedAsset.mediaGuid = media.guid
        expectedAsset.programId = media.programId.toString()
        expectedAsset.originalOwnerIds = media.originalOwnerIds
        expectedAsset.originalMediaIds = media.originalMediaIds
        expectedAsset.mediaAdded = media.added
    }

    private Asset createAssetWithProviderFields(Media media, String assetProvider, String assetProviderName)
    {
        Asset expectedAsset = new Asset()
        if (assetProvider != null)
        {
            expectedAsset.provider = extractAssetCustomValueOnKey(media, assetProvider)
        }
        if (assetProviderName != null)
        {
            expectedAsset.providerName = extractAssetCustomValueOnKey(media, assetProviderName)
        }
        expectedAsset
    }


    private String extractAssetCustomValueOnKey(Media media, String key)
    {
        if (media.getCustomValue(WHOLESALENAMESPACE, 'providerMap') == null)
        {
            return null
        } else
        {
            (media.getCustomValue(WHOLESALENAMESPACE, 'providerMap') as Map<String, String>)[key]
        }
    }

    void validate(Asset asset, Media media)
    {
        // TODO: It would be great to use soft-asserts here, but I also don't want to lose the fine-grain detail that the groovy assert provides.  Is
        //  there a way to get the best of both?

        assert asset != null
        assert media != null
        assert asset.mediaId == media.id
        assert withoutMillis(asset.availableDate) == withoutMillis(media.availableDate)
        assert withoutMillis(asset.expirationDate) == withoutMillis(media.expirationDate)
        assert asset.title == media.title
        assert asset.ownerId == media.ownerId
        assert asset.mediaGuid == media.guid
        assert asset.programId != null
        assert asset.programId == media.programId.toString()
        assert media.originalOwnerIds != null
        //noinspection GroovyAssignabilityCheck
        assert asset.originalOwnerIds == media.originalOwnerIds
        assert media.originalMediaIds != null
        //noinspection GroovyAssignabilityCheck
        assert asset.originalMediaIds == media.originalMediaIds
        assert withoutMillis(asset.mediaAdded) == withoutMillis(media.added)
        assert asset.status == null
    }

    static void withoutMillis(Date date)
    {
        DateUtils.setMilliseconds(date, 0)
    }
}
