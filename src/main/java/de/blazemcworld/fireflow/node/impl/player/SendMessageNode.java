package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;

public class SendMessageNode extends Node {

    public SendMessageNode() {
        super("Send Message");

        NodeInput signal = input("Signal", SignalValue.INSTANCE);
        NodeInput player = input("Player", PlayerValue.INSTANCE);
        NodeInput message = input("Message", MessageValue.INSTANCE);
        NodeOutput next = output("Next", SignalValue.INSTANCE);

        signal.setInstruction(new MultiInstruction(
                Type.VOID_TYPE,
                PlayerValue.use(player, new MultiInstruction(Type.VOID_TYPE,
                        message,
                        new RawInstruction(Type.VOID_TYPE, new MethodInsnNode(
                                Opcodes.INVOKEINTERFACE,
                                "net/kyori/adventure/audience/Audience",
                                "sendMessage",
                                "(Lnet/kyori/adventure/text/Component;)V",
                                true
                        ))
                ), null),
                next
        ));
    }

}
