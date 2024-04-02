package net.buildtheearth.modules.metrics;

import com.influxdb.v3.client.InfluxDBClient;
import lombok.Getter;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.metrics.listeners.GeneratorGenerateEventListener;

/**
 * Manages all things related to
 * collecting metrics data.
 */
public class MetricsModule extends Module {

    private static MetricsModule instance = null;

    @Getter
    private InfluxDBClient influxDBClient;

    protected MetricsModule() {
        super("Metrics");
    }

    public static MetricsModule getInstance() {
        return instance == null ? instance = new MetricsModule() : instance;
    }

    @Override
    public void enable() {
        String hostUrl = "https://eu-central-1-1.aws.cloud2.influxdata.com";
        char[] authToken = "U12KwZtar35Krjgroe6rMiSp0ARrZXShAWfc4oAFjOT-1vbvEP_TRE71Jsgpukz1lsjapCpmgMFRYHly8u6bqw==".toCharArray();

        this.influxDBClient = InfluxDBClient.getInstance(hostUrl, authToken, "BuildTeamTools");
    }

    @Override
    public void registerListeners() {
        registerListeners(new GeneratorGenerateEventListener());
    }
}
