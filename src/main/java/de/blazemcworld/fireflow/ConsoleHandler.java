package de.blazemcworld.fireflow;

import net.minestom.server.MinecraftServer;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConsoleHandler {

    public static void init() {
        new Thread(ConsoleHandler::loop).start();
    }

    private static void loop() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String line = sc.nextLine();
            switch (line) {
                case "stop" -> {
                    FireFlow.LOGGER.info("Stopping...");
                    MinecraftServer.stopCleanly();
                    return;
                }
                case "update" -> {
                    FireFlow.LOGGER.info("Updating...");
                    try (ZipInputStream zip = new ZipInputStream(URI.create("https://nightly.link/BlazeMCworld/FireFlow/workflows/build/main/FireFlow.zip").toURL().openStream())){
                        ZipEntry entry = zip.getNextEntry();
                        while (entry != null) {
                            if (entry.getName().endsWith(".jar")) break;
                            entry = zip.getNextEntry();
                        }
                        if (entry == null) {
                            FireFlow.LOGGER.error("Failed to find .jar in downloaded zip!");
                        } else {
                            Files.write(Path.of(ConsoleHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath()), zip.readAllBytes());
                            FireFlow.LOGGER.info("Updated jar!");
                        }
                    } catch (Exception err) {
                        FireFlow.LOGGER.error("Error updating", err);
                    }
                }
                default -> FireFlow.LOGGER.info("Unknown command!");
            }
        }
    }

}
