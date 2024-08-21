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

public class NumberValue implements Value {

    public static NumberValue INSTANCE = new NumberValue();
    private NumberValue() {}

    @Override
    public String getBaseName() {
        return "Number";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Type getType() {
        return Type.DOUBLE_TYPE;
    }

    @Override
    public InsnList compile(Object inset) {
        if (!(inset instanceof Double)) {
            if (inset instanceof Number n) {
                inset = n.doubleValue();
            } else {
                inset = 0.0;
            }
        }
        InsnList out = new InsnList();
        out.add(new LdcInsnNode(inset));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode end = new LabelNode();
        LabelNode convert = new LabelNode();
        return new MultiInstruction(getType(),
                value,
                new RawInstruction(getType(),
                        new InsnNode(Opcodes.DUP),
                        new TypeInsnNode(Opcodes.INSTANCEOF, "java/lang/Double"),
                        new JumpInsnNode(Opcodes.IFGT, convert),
                        new InsnNode(Opcodes.POP),
                        new LdcInsnNode(0.0),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        convert,
                        new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Double"),
                        new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return new MultiInstruction(Type.getType(Double.class),
                value,
                new RawInstruction(Type.getType(Double.class),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;")
                )
        );
    }

    @Override
    public Double prepareInset(String message) {
        try {
            return Double.parseDouble(message);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean canInset() {
        return true;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        buffer.write(NetworkBuffer.DOUBLE, (Double) inset);
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        return buffer.read(NetworkBuffer.DOUBLE);
    }
}
