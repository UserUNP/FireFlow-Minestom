package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class PositionValue implements Value {

    public static final PositionValue INSTANCE = new PositionValue();

    private PositionValue() {
    }

    @Override
    public String getBaseName() {
        return "Position";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.BLUE;
    }

    @Override
    public Type getType() {
        return Type.getType(Pos.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        out.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minestom/server/coordinate/Pos", "ZERO", "Lnet/minestom/server/coordinate/Pos;"));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode cast = new LabelNode();
        LabelNode end = new LabelNode();

        return new MultiInstruction(Type.getType(Pos.class),
                value,
                new RawInstruction(Type.getType(Pos.class),
                        new InsnNode(Opcodes.DUP),
                        new TypeInsnNode(Opcodes.INSTANCEOF, "net/minestom/server/coordinate/Pos"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "net/minestom/server/coordinate/Pos", "ZERO", "Lnet/minestom/server/coordinate/Pos;"),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "net/minestom/server/coordinate/Pos"),
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
        throw new IllegalStateException("Signal inputs can not be inset!");
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        throw new IllegalStateException("Signal inputs can not be inset!");
    }
}
