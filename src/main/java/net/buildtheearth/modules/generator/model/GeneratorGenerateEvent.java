package net.buildtheearth.modules.generator.model;

import com.sk89q.worldedit.math.Vector3;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratorGenerateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Player generatingPlayer;

    @Getter
    private final GeneratorType generatorType;

    @Getter
    private final Vector3 approximateLocation;

    @Getter
    private final CompletableFuture<Boolean> completed;

    public GeneratorGenerateEvent(Player generatingPlayer, GeneratorType generatorType, Vector3 approximateLocation, CompletableFuture<Boolean> completed) {
        this.generatingPlayer = generatingPlayer;
        this.generatorType = generatorType;
        this.approximateLocation = approximateLocation;
        this.completed = completed;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
