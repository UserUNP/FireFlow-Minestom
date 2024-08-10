package de.blazemcworld.fireflow;

import net.minestom.server.MinecraftServer;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket;
import net.minestom.server.network.socket.Server;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class TransferPacketPatcher {

    /*
    * Utility class to change TransferPacket behaviour, that makes Minestom treat every transfer packet as a regular login.
    * Can be removed once Minestom supports the transfer intent.
    * */
    public static void apply() {
        try {
            PacketProcessor process = MinecraftServer.process().packetProcessor();

            Field packetListenerManager = PacketProcessor.class.getDeclaredField("packetListenerManager");
            packetListenerManager.setAccessible(true);

            PacketProcessor wrapper = new PacketProcessor((PacketListenerManager) packetListenerManager.get(process)) {
                @Override
                public @NotNull ClientPacket create(@NotNull ConnectionState connectionState, int packetId, ByteBuffer body) {
                    if (connectionState == ConnectionState.HANDSHAKE && packetId == 0) {
                        NetworkBuffer buffer = new NetworkBuffer(body);
                        ClientHandshakePacket actualPacket = new ClientHandshakePacket(buffer);
                        body.position(buffer.readIndex());

                        if (actualPacket.intent() == ClientHandshakePacket.Intent.TRANSFER) {
                            return new ClientHandshakePacket(
                                    actualPacket.protocolVersion(),
                                    actualPacket.serverAddress(),
                                    actualPacket.serverPort(),
                                    ClientHandshakePacket.Intent.LOGIN
                            );
                        }

                        return actualPacket;
                    }
                    return process.create(connectionState, packetId, body);
                }
            };

            Class<?> clazz = Class.forName("net.minestom.server.ServerProcessImpl");

            Field processor = clazz.getDeclaredField("packetProcessor");
            processor.setAccessible(true);
            processor.set(MinecraftServer.process(), wrapper);

            Field socketServer = clazz.getDeclaredField("server");
            socketServer.setAccessible(true);
            Server srv = (Server) socketServer.get(MinecraftServer.process());

            Field processor2 = Server.class.getDeclaredField("packetProcessor");
            processor2.setAccessible(true);
            processor2.set(srv, wrapper);
        } catch (Exception err) {
            FireFlow.LOGGER.warn("Failed to patch TransferPacket behaviour!", err);
        }
    }
}
