package groovy.com.tpatterson.playground.featuretoggle;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.user.NoOpUserProvider;

/**
 * Created by tom.patterson on 7/25/17.
 */
public class TestToggle
{

    @Test
    public void testToggleFeaturesWithManager()
    {
        FeatureManager manager = new FeatureManagerBuilder()
            .featureEnum(MyFeatures.class)
            .stateRepository(new InMemoryStateRepository())
            .userProvider(new NoOpUserProvider())
            .build();

        Assert.assertTrue(manager.isActive(MyFeatures.FEATURE_ONE));
        Assert.assertFalse(manager.isActive(MyFeatures.FEATURE_TWO));

        manager.getFeatureState(MyFeatures.FEATURE_ONE).setEnabled(false);
        Assert.assertFalse(manager.isActive(MyFeatures.FEATURE_ONE));
    }

    @Test
    public void testToggleFeaturesFromConfig()
    {
        FeatureManager manager = new FeatureManagerBuilder()
            .togglzConfig(new ToggleConfig())
            .build();

        // Generally you DO NOT do this, but use spring or web context - see docs
        StaticFeatureManagerProvider.setFeatureManager(manager);

        Assert.assertTrue(MyFeatures.FEATURE_ONE.isActive());
        Assert.assertFalse(MyFeatures.FEATURE_TWO.isActive());
    }

}
