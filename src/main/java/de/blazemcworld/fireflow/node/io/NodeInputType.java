package de.blazemcworld.fireflow.node.io;

import net.minestom.server.network.NetworkBuffer;

import java.util.function.BiConsumer;

//TODO: redo this entire thing
public enum NodeInputType {
    SINGULAR((buffer, input) -> {}, (buffer, input) -> {}),
    MULTIPLE((buffer, input) -> {}, (buffer, input) -> {}),
    ;

    private final BiConsumer<NetworkBuffer, NodeIO.In> writer;
    private final BiConsumer<NetworkBuffer, NodeIO.In> reader;
    NodeInputType(BiConsumer<NetworkBuffer, NodeIO.In> writer, BiConsumer<NetworkBuffer, NodeIO.In> reader) {
        this.writer = writer;
        this.reader = reader;
    }
    
    public void write(NetworkBuffer buffer, NodeIO.In input) {
        writer.accept(buffer, input);
    }

    public void read(NetworkBuffer buffer, NodeIO.In input) {
        reader.accept(buffer, input);
    }
}
