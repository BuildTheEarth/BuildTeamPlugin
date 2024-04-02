package net.buildtheearth.modules.metrics.listeners;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.write.Point;
import net.buildtheearth.modules.generator.model.GeneratorGenerateEvent;
import net.buildtheearth.modules.metrics.MetricsModule;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GeneratorGenerateEventListener implements Listener {

    private final InfluxDBClient influxDBClient = MetricsModule.getInstance().getInfluxDBClient();
    @EventHandler
    public void onGeneratorGenerate(GeneratorGenerateEvent event) {

        event.getCompleted().whenComplete((result, throwable) -> {
            if(throwable != null) {
                ChatHelper.logError("Something went wrong with the GeneratorGenerateEvent completion handling.");

                Point point = Point.measurement(String.valueOf(event.getGeneratingPlayer().getUniqueId()))
                        .addTag("type", event.getGeneratorType().getName())
                        .addTag("completed", String.valueOf(false))
                        .addField("location", event.getApproximateLocation().toString());

                influxDBClient.writePoint(point);
                return;
            }

            Point point = Point.measurement(String.valueOf(event.getGeneratingPlayer().getUniqueId()))
                    .addTag("type", event.getGeneratorType().getName())
                    .addTag("completed", String.valueOf(true))
                    .addField("location", event.getApproximateLocation().toString());

            influxDBClient.writePoint(point);
        });
    }

}
