package groovy.com.tpatterson.playground.featuretoggle;

import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.user.UserProvider;
import org.togglz.servlet.user.ServletUserProvider;

import java.io.File;

/**
 * Created by tom.patterson on 7/25/17.
 */

//@ApplicationScoped
public class ToggleConfig implements TogglzConfig
{
    public Class<? extends Feature> getFeatureClass()
    {
        return MyFeatures.class;
    }

    public StateRepository getStateRepository()
    {
        return new FileBasedStateRepository(new File("/tmp/features.properties"));
    }

    public UserProvider getUserProvider()
    {
        return new ServletUserProvider("admin");
    }
}