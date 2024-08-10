package de.blazemcworld.fireflow.space;

import net.minestom.server.MinecraftServer;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.timer.TaskSchedule;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpacesIndex {

    public static final List<SpaceInfo> spaces = new ArrayList<>();
    private static final Path spaceIndex = Path.of("space_index.bin");
    public static int nextId = 0;

    public static void init() {
        if (Files.exists(spaceIndex)) read();

        MinecraftServer.getSchedulerManager().buildShutdownTask(SpacesIndex::write);

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            write();
            return TaskSchedule.minutes(1);
        });
    }

    private static void write() {
        NetworkBuffer buffer = new NetworkBuffer();

        buffer.write(NetworkBuffer.INT, nextId);
        buffer.write(NetworkBuffer.INT, spaces.size());
        for (SpaceInfo info : spaces) {
            info.write(buffer);
        }

        try {
            Files.write(spaceIndex, buffer.readBytes(buffer.writeIndex()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void read() {
        NetworkBuffer buffer;
        try {
            buffer = new NetworkBuffer(ByteBuffer.wrap(Files.readAllBytes(spaceIndex)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        nextId = buffer.read(NetworkBuffer.INT);
        int count = buffer.read(NetworkBuffer.INT);
        for (int i = 0; i < count; i++) {
            SpaceInfo info = new SpaceInfo();
            info.read(buffer);
            spaces.add(info);
        }
    }

}
