package com.sitrica.core.common;

import java.io.File;
import java.nio.file.Path;

public interface SourPlugin {

    Path getDataDirectory();

    File getJar();

    void debugMessage(String s);

    void consoleMessage(String s);

    String getName();
}
