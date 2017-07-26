package com.tpatterson.playground.featuretoggle;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.user.NoOpUserProvider;

import java.io.File;

/**
 * Created by tom.patterson on 7/25/17.
 */
public class TestToggle
{

    @Test
    public void testToggleFeaturesWithManager()
    {
        // Generally you DO NOT do this directly, but use annotations
        // Togglz comes with dep. for spring or web context in order to load the manager
        FeatureManager manager = new FeatureManagerBuilder()
            .featureEnum(MyFeatures.class)
            .stateRepository(new FileBasedStateRepository(new File("/app/config/features.properties")))
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
        // https://www.togglz.org/

        // Generally you DO NOT do this directly, but use annotations
        // Togglz comes with dep. for spring or web context in order to load the manager
        FeatureManager manager = new FeatureManagerBuilder()
                .togglzConfig(new ToggleConfig())
                .build();
        StaticFeatureManagerProvider.setFeatureManager(manager);

        // Access feature active/disabled this way
        Assert.assertTrue(MyFeatures.FEATURE_ONE.isActive());
        Assert.assertFalse(MyFeatures.FEATURE_TWO.isActive());

        // use the manager to change configs at runtime
        manager.getFeatureState(MyFeatures.FEATURE_ONE).setEnabled(false);
        Assert.assertFalse(manager.isActive(MyFeatures.FEATURE_ONE));

        // See ToggleConfig to see how specific features are mapped to a state repository
        // (lots of built-in one and you can just write your own)
    }

}
