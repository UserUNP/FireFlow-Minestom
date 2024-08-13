package de.blazemcworld.fireflow.node.impl;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.SignalValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ScheduleNode extends Node {

    public ScheduleNode() {
        super("Schedule");

        NodeInput signal = input("Signal", SignalValue.INSTANCE);
        NodeInput delay = input("Delay", NumberValue.INSTANCE).withDefault(1);
        NodeOutput now = output("Now", SignalValue.INSTANCE);
        NodeOutput then = output("Then", SignalValue.INSTANCE);

        signal.setInstruction(new Instruction() {
            @Override
            public void prepare(NodeCompiler ctx) {
                ctx.prepare(delay);
                ctx.markRoot(then);
                ctx.prepare(now);
            }

            @Override
            public InsnList compile(NodeCompiler ctx, int usedVars) {
                InsnList out = new InsnList();

                out.add(new VarInsnNode(Opcodes.ALOAD, 0));
                out.add(ctx.compile(delay, usedVars));
                out.add(new LdcInsnNode(ctx.markRoot(then)));
                out.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "de/blazemcworld/fireflow/compiler/CompiledNode", "runScheduled", "(DLjava/lang/String;)V"));
                out.add(ctx.compile(now, usedVars));

                return out;
            }

            @Override
            public Type returnType() {
                return Type.VOID_TYPE;
            }
        });
    }
}
