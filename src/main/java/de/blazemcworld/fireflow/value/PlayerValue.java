package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.space.Space;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.UUID;

public class PlayerValue implements Value {
    public static final Value INSTANCE = new PlayerValue();

    private PlayerValue() {}

    @Override
    public String getBaseName() {
        return "Player";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GOLD;
    }

    @Override
    public Type getType() {
        return Type.getType(Reference.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        out.add(new FieldInsnNode(Opcodes.GETSTATIC, "de/blazemcworld/fireflow/value/PlayerValue$Reference", "UNKNOWN", "Lde/blazemcworld/fireflow/value/PlayerValue$Reference;"));
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
                        new TypeInsnNode(Opcodes.INSTANCEOF, "de/blazemcworld/fireflow/value/PlayerValue$Reference"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "de/blazemcworld/fireflow/value/PlayerValue$Reference", "UNKNOWN", "Lde/blazemcworld/fireflow/value/PlayerValue$Reference;"),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "de/blazemcworld/fireflow/value/PlayerValue$Reference"),
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
        throw new IllegalStateException("Player values can not be inset!");
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        throw new IllegalStateException("Player values can not be inset!");
    }

    public static Instruction use(Instruction player, Instruction action, Instruction otherwise) {
        LabelNode fail = new LabelNode();
        LabelNode end = new LabelNode();
        if (otherwise == null) {
            otherwise = new RawInstruction(Type.VOID_TYPE);
        }
        return new MultiInstruction(Type.VOID_TYPE,
                new InstanceMethodInstruction(Reference.class, player, "resolve", Type.getType(Player.class), List.of()),
                new RawInstruction(Type.getType(Player.class), new InsnNode(Opcodes.DUP)),
                new RawInstruction(Type.VOID_TYPE, new JumpInsnNode(Opcodes.IFNULL, fail)),
                action,
                new RawInstruction(Type.VOID_TYPE, new JumpInsnNode(Opcodes.GOTO, end)),
                new RawInstruction(Type.VOID_TYPE, fail),
                new RawInstruction(Type.VOID_TYPE, new InsnNode(Opcodes.POP)),
                otherwise,
                new RawInstruction(Type.VOID_TYPE, end)
        );
    }

    public record Reference(Space space, UUID uuid) {
        @SuppressWarnings("unused") // Used by asm
        public static Reference UNKNOWN = new Reference(null, UUID.fromString("00000000-0000-0000-0000-000000000000"));

        public Reference(Space space, Player player) {
            this(space, player.getUuid());
        }

        @SuppressWarnings("unused") // Used by asm
        public @Nullable Player resolve() {
            if (space == null) return null;
            return space.play.getPlayerByUuid(uuid);
        }
    }
}
