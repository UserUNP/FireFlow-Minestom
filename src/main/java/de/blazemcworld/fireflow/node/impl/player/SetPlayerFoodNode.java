package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;

public class SetPlayerFoodNode extends Node {
    public SetPlayerFoodNode() {
        super("Set Player Food");

        NodeInput signal = input("Signal", SignalValue.INSTANCE);
        NodeInput self = input("Player", PlayerValue.INSTANCE);
        NodeInput food = input("Food", NumberValue.INSTANCE);
        NodeOutput next = output("Next", SignalValue.INSTANCE);

        LabelNode fail = new LabelNode();
        LabelNode end = new LabelNode();
        signal.setInstruction(new MultiInstruction(
                Type.VOID_TYPE,
                self,
                new InstanceMethodInstruction(PlayerValue.Reference.class, self, "resolve", Type.getType(Player.class), List.of()),
                new RawInstruction(Type.getType(Player.class), new InsnNode(Opcodes.DUP)),
                new RawInstruction(Type.VOID_TYPE, new JumpInsnNode(Opcodes.IFNULL, fail)),
                food,
                new RawInstruction(Type.DOUBLE_TYPE, new LdcInsnNode(0.0)),
                new RawInstruction(Type.DOUBLE_TYPE, new LdcInsnNode(20.0)),
                new RawInstruction(Type.DOUBLE_TYPE, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Math", "clamp", "(DDD)D")),
                new RawInstruction(Type.INT_TYPE, new InsnNode(Opcodes.D2I)),
                new RawInstruction(Type.VOID_TYPE, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minestom/server/entity/Player", "setFood", "(I)V")),
                new RawInstruction(Type.VOID_TYPE, new JumpInsnNode(Opcodes.GOTO, end)),
                new RawInstruction(Type.VOID_TYPE, fail),
                new RawInstruction(Type.VOID_TYPE, new InsnNode(Opcodes.POP)),
                new RawInstruction(Type.VOID_TYPE, end),
                next
        ));
    }
}