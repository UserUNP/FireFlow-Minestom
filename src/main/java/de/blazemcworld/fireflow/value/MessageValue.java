package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class MessageValue implements Value {
    public static final Value INSTANCE = new MessageValue();

    private MessageValue() {
    }

    public static final MiniMessage MM = MiniMessage.builder()
            .tags(TagResolver.builder().resolvers(
                    StandardTags.color(),
                    StandardTags.decorations(),
                    StandardTags.font(),
                    StandardTags.gradient(),
                    StandardTags.keybind(),
                    StandardTags.newline(),
                    StandardTags.rainbow(),
                    StandardTags.reset(),
                    StandardTags.transition(),
                    StandardTags.translatable()
            ).build()).build();

    @Override
    public String getBaseName() {
        return "Message";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public Type getType() {
        return Type.getType(Component.class);
    }

    @Override
    public InsnList compile(Object inset) {
        InsnList out = new InsnList();

        out.add(new FieldInsnNode(Opcodes.GETSTATIC, "de/blazemcworld/fireflow/value/MessageValue", "MM", "Lnet/kyori/adventure/text/minimessage/MiniMessage;"));

        if (inset instanceof String) {
            out.add(new LdcInsnNode(inset));
        } else if (inset instanceof Component comp) {
            out.add(new LdcInsnNode(MM.serialize(comp)));
        } else {
            out.add(new LdcInsnNode(""));
        }

        out.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/kyori/adventure/text/serializer/ComponentSerializer", "deserialize", "(Ljava/lang/Object;)Lnet/kyori/adventure/text/Component;"));

        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode cast = new LabelNode();
        LabelNode end = new LabelNode();
        return new MultiInstruction(Type.getType(Component.class),
                value,
                new RawInstruction(Type.getType(Component.class),
                        new InsnNode(Opcodes.DUP),
                        new TypeInsnNode(Opcodes.INSTANCEOF, "net/kyori/adventure/text/Component"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, "net/kyori/adventure/text/Component", "empty", "()Lnet/kyori/adventure/text/TextComponent;"),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "net/kyori/adventure/text/Component"),
                        end
                ));
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return value;
    }

    @Override
    public Object prepareInset(String message) {
        return message;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        buffer.write(NetworkBuffer.STRING, (String) inset);
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        return buffer.read(NetworkBuffer.STRING);
    }
}
