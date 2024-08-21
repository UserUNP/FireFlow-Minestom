package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class ConditionValue implements Value {

    public static ConditionValue INSTANCE = new ConditionValue();
    private ConditionValue() {}

    @Override
    public String getBaseName() {
        return "Condition";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN_TYPE;
    }

    @Override
    public InsnList compile(Object inset) {
        InsnList out = new InsnList();
        out.add(new LdcInsnNode(inset == Boolean.TRUE));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode end = new LabelNode();
        return new MultiInstruction(getType(),
                value,
                new RawInstruction(getType(),
                        new InsnNode(Opcodes.DUP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;"),
                        new JumpInsnNode(Opcodes.IF_ACMPEQ, end),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return new MultiInstruction(Type.getType(Boolean.class),
                value,
                new RawInstruction(Type.getType(Boolean.class),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;")
                )
        );
    }

    @Override
    public Object prepareInset(String message) {
        if (message.equalsIgnoreCase("true")) return true;
        if (message.equalsIgnoreCase("false")) return false;
        return null;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        buffer.write(NetworkBuffer.BOOLEAN, (Boolean) inset);
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        return buffer.read(NetworkBuffer.BOOLEAN);
    }
}
