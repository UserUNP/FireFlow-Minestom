package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.WeakHashMap;

public class ListValue implements Value {

    private static final WeakHashMap<Value, ListValue> cache = new WeakHashMap<>();
    private final Value type;

    private ListValue(Value type) {
        this.type = type;
    }

    public static ListValue get(Value type) {
        return cache.computeIfAbsent(type, ListValue::new);
    }

    @Override
    public String getBaseName() {
        return "List";
    }

    @Override
    public TextColor getColor() {
        return type.getColor();
    }

    @Override
    public Type getType() {
        return Type.getType(List.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        if (inset != null) throw new IllegalStateException("List values can not be inset!");
        InsnList out = new InsnList();
        out.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        out.add(new InsnNode(Opcodes.DUP));
        out.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode cast = new LabelNode();
        LabelNode end = new LabelNode();
        return new MultiInstruction(getType(),
                value,
                new RawInstruction(getType(),
                        new InsnNode(Opcodes.DUP),
                        new TypeInsnNode(Opcodes.INSTANCEOF, "java/util/List"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"),
                        new InsnNode(Opcodes.DUP),
                        new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "java/util/List"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return value;
    }

    @Override
    public Object prepareInset(String message) {
        return null;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        throw new IllegalStateException("List values can not be inset!");
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        throw new IllegalStateException("List values can not be inset!");
    }

    public Value fromGenerics(List<Value> generics) {
        return ListValue.get(generics.getFirst());
    }

    public List<Value> toGenerics() {
        return List.of(type);
    }

    public List<GenericParam> possibleGenerics() {
        return List.of(new GenericParam("List Type", AllValues.dataOnly));
    }
}
