package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class VectorValue implements Value {

    public static final VectorValue INSTANCE = new VectorValue();

    private VectorValue() {
    }

    @Override
    public String getBaseName() {
        return "Vector";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.WHITE;
    }

    @Override
    public Type getType() {
        return Type.getType(Vec.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        out.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minestom/server/coordinate/Vec", "ZERO", "Lnet/minestom/server/coordinate/Vec;"));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode cast = new LabelNode();
        LabelNode end = new LabelNode();

        return new MultiInstruction(Type.getType(Vec.class),
                value,
                new RawInstruction(Type.getType(Vec.class),
                        new InsnNode(Opcodes.DUP),
                        new TypeInsnNode(Opcodes.INSTANCEOF, "net/minestom/server/coordinate/Vec"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "net/minestom/server/coordinate/Vec", "ZERO", "Lnet/minestom/server/coordinate/Vec;"),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "net/minestom/server/coordinate/Vec"),
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
