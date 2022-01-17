package com.sitrica.core.common;

import com.sitrica.core.common.messaging.Formatting;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface SourPlugin {

    Path getDataDirectory();

    File getJar();

    default void debugMessage(String message) {
        getConfiguration().ifPresent(configuration -> {
            if (configuration.node("debug", false).getBoolean())
                consoleMessage("&b" + message);
        });
    }

    default void consoleMessage(String message) {
        getAdventureConsole().sendMessage(Formatting.color(message));
    }

    String getName();

    String getPrefix();

    Optional<ConfigurationNode> getConfiguration(String config);

    Audience getAdventureConsole();

    default Optional<ConfigurationNode> getConfiguration() {
        return getConfiguration("config");
    }
}
