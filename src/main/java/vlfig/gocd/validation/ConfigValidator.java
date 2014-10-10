package vlfig.gocd.validation;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.mockito.Mockito.mock;

import java.io.OutputStream;
import java.nio.file.Files;

import com.thoughtworks.go.config.ConfigCache;
import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.MagicalGoConfigXmlLoader;
import com.thoughtworks.go.config.MagicalGoConfigXmlWriter;
import com.thoughtworks.go.config.registry.ConfigElementImplementationRegistrar;
import com.thoughtworks.go.config.registry.ConfigElementImplementationRegistry;
import com.thoughtworks.go.config.registry.NoPluginsInstalled;
import com.thoughtworks.go.metrics.service.MetricsProbeService;
import com.thoughtworks.go.plugin.infra.PluginManager;

public class ConfigValidator {

    private static final String READ_FROM = "config.xml";
    private static final String WRITE_TO = "cruise-config.xml";

    private ConfigValidator() {}

    public static void validateConfig() throws Exception {
        MetricsProbeService metricsProbeService = mock(MetricsProbeService.class);
        ConfigElementImplementationRegistry registryWithNoPlugins = registryWithNoPlugins();

        MagicalGoConfigXmlLoader loader =
                new MagicalGoConfigXmlLoader(new ConfigCache(), registryWithNoPlugins, metricsProbeService);
        String configStr = new String(readAllBytes(get(READ_FROM)));
        CruiseConfig config = loader.loadConfigHolder(configStr).config;

        MagicalGoConfigXmlWriter writer =
                new MagicalGoConfigXmlWriter(new ConfigCache(), registryWithNoPlugins, metricsProbeService);
        OutputStream os = Files.newOutputStream(get(WRITE_TO));
        writer.write(config, os, true);
    }

    private static ConfigElementImplementationRegistry registryWithNoPlugins() {
        ConfigElementImplementationRegistry registry =
                new ConfigElementImplementationRegistry(new NoPluginsInstalled());
        new ConfigElementImplementationRegistrar(mock(PluginManager.class), registry).initialize();
        return registry;
    }

    public static void main(String[] args) throws Exception {
        validateConfig();
    }
}
