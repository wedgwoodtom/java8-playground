package com.tpatterson.cs.comcast.wholesale.event.listener.integration.spring

import com.theplatform.access.api.web.client.RegistryServiceClient
import com.tpatterson.cs.comcast.wholesale.event.poller.AssetPoller
import com.tpatterson.cs.comcast.wholesale.event.poller.DeliveryPoller
import com.tpatterson.cs.comcast.wholesale.event.poller.ProcessingPoller
import com.theplatform.cs.wholesale.event.data.api.client.AssetClient
import com.theplatform.cs.wholesale.event.data.api.client.DeliveryClient
import com.theplatform.cs.wholesale.event.data.api.client.ProcessingClient
import com.theplatform.cs.wholesale.event.object.Asset
import com.theplatform.cs.wholesale.event.object.Delivery
import com.theplatform.cs.wholesale.pitch.data.api.client.PitchTaskClient
import com.theplatform.cs.wholesale.pitch.data.api.client.ReceiverClient
import com.theplatform.data.api.client.ClientConfiguration
import com.theplatform.media.api.client.MediaClient
import com.theplatform.media.api.client.ProviderClient
import com.theplatform.module.authentication.client.AuthenticationClient
import com.theplatform.profile.data.workflow.api.client.ProfileResultClient
import com.theplatform.test.conditionpoller.extensions.mpx.DataObjectFieldComparisonPoller
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment

import javax.annotation.Resource

/**
 * Spring test configuration
 */
@Configuration
@PropertySource('classpath:${propFile:test.properties}')
class IntegrationTestConfiguration
{
    public static final String COMCAST_WHOLESALE_EVENT_DS = 'Comcast Wholesale Event Data Service'

    public static final String COMCAST_WHOLESALE_PITCH_DS = 'Comcast Wholesale Pitch Data Service'

    @Resource
    Environment env

    @Bean
    URI testAccount()
    {
        URI.create(read('test.account'))
    }

    @Bean
    Long pollingIntervalMillis()
    {
        Long.parseLong(read('test.polling.interval.millis'))
    }

    @Bean
    Long pollingTimeoutMillis()
    {
        Long.parseLong(read('test.polling.interval.timeout'))
    }

    @Bean
    URI primaryProviderId()
    {
        URI.create(read('test.primaryProviderId'))
    }

    @Bean
    URI secondaryProviderId()
    {
        URI.create(read('test.secondaryProviderId'))
    }

    @Bean
    URI originalMediaId()
    {
        URI.create(read('test.originalMediaId'))
    }

    @Bean
    URI programId()
    {
        URI.create(read('test.programId'))
    }

    @Bean
    URI outletProfileId()
    {
        URI.create(read('test.outletProfileId'))
    }

    @Bean
    URI publishProfileId()
    {
        URI.create(read('test.publishProfileId'))
    }

    @Bean
    RegistryServiceClient registryServiceClient(AuthenticationClient authenticationClient)
    {
        new RegistryServiceClient(read('access.url'), authenticationClient)
    }

    @Bean
    MediaClient mediaClient(URI testAccount, RegistryServiceClient registryServiceClient,
            AuthenticationClient authenticationClient)
    {
        def mdsUrl = registryServiceClient.resolveUrlByAccount(testAccount, 'Media Data Service')
        new MediaClient(mdsUrl, authenticationClient)
    }

    @Bean
    ProviderClient providerClient(URI testAccount, RegistryServiceClient registryServiceClient,
            AuthenticationClient authenticationClient)
    {
        def mdsUrl = registryServiceClient.resolveUrlByAccount(testAccount, 'Media Data Service')
        new ProviderClient(mdsUrl, authenticationClient)
    }

    @Bean
    PitchTaskClient pitchTaskClient(URI testAccount, RegistryServiceClient registryServiceClient,
            AuthenticationClient authenticationClient)
    {
        def pitchDsUrl = registryServiceClient.resolveUrlByAccount(testAccount, 'Comcast Wholesale Pitch Data Service')
        // makes debugging REST transactions much easier
        ClientConfiguration clientConfiguration = new ClientConfiguration()
        clientConfiguration.acceptCompression = false
        new PitchTaskClient(pitchDsUrl, authenticationClient, clientConfiguration)
    }

    @Bean
    ReceiverClient receiverClient(URI testAccount, RegistryServiceClient registryServiceClient,
            AuthenticationClient authenticationClient)
    {
        def pitchDsUrl = registryServiceClient.resolveUrlByAccount(testAccount, COMCAST_WHOLESALE_PITCH_DS)
        new ReceiverClient(pitchDsUrl, authenticationClient)
    }

    @Bean
    String eventDataServiceUrl(URI testAccount, RegistryServiceClient registryServiceClient)
    {
        registryServiceClient.resolveUrlByAccount(testAccount, COMCAST_WHOLESALE_EVENT_DS)
    }

    @Bean
    AssetClient assetClient(URI testAccount, String eventDataServiceUrl, AuthenticationClient authenticationClient)
    {
        new AssetClient(eventDataServiceUrl, authenticationClient)
    }

    @Bean
    ProcessingClient processingClient(URI testAccount, RegistryServiceClient registryServiceClient,
            AuthenticationClient authenticationClient)
    {
        def eventDsUrl = registryServiceClient.resolveUrlByAccount(testAccount, COMCAST_WHOLESALE_EVENT_DS)
        new ProcessingClient(eventDsUrl, authenticationClient)
    }

    @Bean
    DeliveryClient deliveryClient(URI testAccount, RegistryServiceClient registryServiceClient,
            AuthenticationClient authenticationClient)
    {
        def eventDsUrl = registryServiceClient.resolveUrlByAccount(testAccount, COMCAST_WHOLESALE_EVENT_DS)
        // makes debugging REST transactions much easier
        ClientConfiguration clientConfiguration = new ClientConfiguration()
        clientConfiguration.acceptCompression = false
        new DeliveryClient(eventDsUrl, authenticationClient, clientConfiguration)
    }

    @Bean
    ProfileResultClient profileResultClient(URI testAccount, RegistryServiceClient registryServiceClient,
            AuthenticationClient authenticationClient)
    {
        def eventDsUrl = registryServiceClient.resolveUrlByAccount(testAccount, 'Workflow Data Service')
        new ProfileResultClient(eventDsUrl, authenticationClient)
    }

    @Bean
    AuthenticationClient authenticationClient(URI testAccount)
    {
        new AuthenticationClient(
                read('identity.url'),
                read('test.user'),
                read('test.user.password'),
                [testAccount.toString()] as String[]
        )
    }

    @Bean
    ProcessingPoller processingPoller()
    {
        new ProcessingPoller()
    }

    @Bean
    AssetPoller assetPoller()
    {
        new AssetPoller()
    }

    @Bean
    DeliveryPoller deliveryPoller()
    {
        new DeliveryPoller()
    }

    @Bean
    DataObjectFieldComparisonPoller<Delivery> deliveryCheckerPoller(DeliveryClient deliveryClient)
    {
        return new DataObjectFieldComparisonPoller<Delivery>(deliveryClient)
    }

    @Bean
    DataObjectFieldComparisonPoller<Asset> assetCheckerPoller(AssetClient assetClient)
    {
        return new DataObjectFieldComparisonPoller<Asset>(assetClient)
    }

    def read(String propertyName)
    {
        env.getRequiredProperty(propertyName)
    }
}
