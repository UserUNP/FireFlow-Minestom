package de.blazemcworld.fireflow.compiler;

import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.StructValue;
import de.blazemcworld.fireflow.value.Value;
import it.unimi.dsi.fastutil.Pair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public final class StructDefinition {

    private static final WeakHashMap<StructValue, StructDefinition> cache = new WeakHashMap<>();
    public static StructDefinition get(StructValue type) {
        return cache.computeIfAbsent(type, StructDefinition::new);
    }

    public final StructValue type;
    public final FunctionDefinition definition;
    private final String stName;

    private StructDefinition(StructValue type) {
        this.type = type;
        this.stName = type.getBaseName();
        ArrayList<NodeOutput> fnInputs = new ArrayList<>(type.size() + 1);
        fnInputs.add(new NodeOutput("On Creation", SignalValue.INSTANCE));
        for (int i = 0; i < type.size(); i++) {
            Pair<String, Value> pair = type.getField(i);
            fnInputs.add(new NodeOutput(pair.left(), pair.right()));
        }
        definition = new FunctionDefinition(stName + " Struct", fnInputs, List.of()); //TODO: wait for optional node inputs
    }

    public FunctionDefinition.Call createCall() {
        int size = definition.fnInputs.size() - 1;
        ArrayList<NodeOutput> fnInputs = new ArrayList<>(size);
        for (int i = 1; i < size; i++) {
            NodeOutput in = definition.fnInputs.get(i);
            fnInputs.add(new NodeOutput(in.getName(), in.type));
        }

        FunctionDefinition.Call defCall = definition.createCall();
        NodeOutput struct = new NodeOutput(stName, type);
        Instruction[] instructions = new Instruction[size + 2];
        instructions[0] = defCall.inputs.getFirst();
        instructions[1] = new RawInstruction(type.getType(),
                new IntInsnNode(Opcodes.BIPUSH, size),
                new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object")
        );
        for (int i = 0; i < size; i++) {
            NodeOutput out = fnInputs.get(i);
            defCall.inputs.get(i + 1).connectValue(out);
            instructions[i + 2] = new MultiInstruction(Type.VOID_TYPE,
                    new RawInstruction(Type.VOID_TYPE,
                            new InsnNode(Opcodes.DUP),
                            new LdcInsnNode(i)
                    ),
                    out.getType().wrapPrimitive(out),
                    new RawInstruction(Type.VOID_TYPE, new InsnNode(Opcodes.AASTORE))
            );
        }

        struct.setInstruction(new MultiInstruction(type.getType(), instructions));
        NodeInput createOut = new NodeInput(stName, type);
        createOut.connectValue(struct);

        FunctionDefinition createFunction = new FunctionDefinition(type.getBaseName(), fnInputs, List.of(createOut));
        return createFunction.createCall();
    }
}
