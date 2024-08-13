package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;

public class DisplayPlayerDamageAnimationNode extends Node {

    public DisplayPlayerDamageAnimationNode() {
        super("Display Player Damage Animation");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Criticals", ConditionValue.INSTANCE);
        input("Magic Criticals", ConditionValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(DisplayPlayerDamageAnimationNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) {
            p.sendPacketToViewersAndSelf(new EntityAnimationPacket(p.getEntityId(), EntityAnimationPacket.Animation.TAKE_DAMAGE));

            if (criticals()) {
                p.sendPacketToViewersAndSelf(new EntityAnimationPacket(p.getEntityId(), EntityAnimationPacket.Animation.CRITICAL_EFFECT));
            }
            if (magicCriticals()) {
                p.sendPacketToViewersAndSelf(new EntityAnimationPacket(p.getEntityId(), EntityAnimationPacket.Animation.MAGICAL_CRITICAL_EFFECT));
            }
        }
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Criticals")
    private static boolean criticals() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Magic Criticals")
    private static boolean magicCriticals() {
        throw new IllegalStateException();
    }

}
