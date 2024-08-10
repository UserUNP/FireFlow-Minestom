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

public class SendTitleNode extends Node {
    public SendTitleNode() {
        super("Send Title");

        NodeInput signal = input("Signal", SignalValue.INSTANCE);
        NodeInput player = input("Player", PlayerValue.INSTANCE);
        NodeInput title = input("Title", MessageValue.INSTANCE);
        NodeInput subtitle = input("Subtitle", MessageValue.INSTANCE);
        NodeOutput next = output("Next", SignalValue.INSTANCE);

        signal.setInstruction(new MultiInstruction(
                Type.VOID_TYPE,
                PlayerValue.use(player, new MultiInstruction(Type.VOID_TYPE,
                        title,
                        subtitle,
                        new RawInstruction(Type.VOID_TYPE, new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "net/kyori/adventure/title/Title",
                                "title",
                                "(Lnet/kyori/adventure/text/Component;Lnet/kyori/adventure/text/Component;)Lnet/kyori/adventure/title/Title;",
                                true
                        )),
                        new RawInstruction(Type.VOID_TYPE, new MethodInsnNode(
                                Opcodes.INVOKEINTERFACE,
                                "net/kyori/adventure/audience/Audience",
                                "showTitle",
                                "(Lnet/kyori/adventure/title/Title;)V"
                        ))
                ), null),
                next
        ));
    }
}
