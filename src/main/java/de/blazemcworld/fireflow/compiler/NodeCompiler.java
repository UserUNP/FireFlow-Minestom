package de.blazemcworld.fireflow.compiler;

import de.blazemcworld.fireflow.compiler.instruction.CpuCheckInstruction;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;

public class NodeCompiler {

    public final ClassNode classNode = new ClassNode();
    public final String className;
    private final HashMap<Instruction, Integer> uses = new HashMap<>();
    private final HashMap<Instruction, String> names = new HashMap<>();
    private final HashMap<Instruction, MethodNode> methods = new HashMap<>();

    public NodeCompiler(String name) {
        classNode.access = Opcodes.ACC_PUBLIC;
        classNode.name = "de/blazemcworld/fireflow/compiled/" + name.replace('.', '/');
        classNode.version = Opcodes.V21;
        classNode.superName = "de/blazemcworld/fireflow/compiler/CompiledNode";
        className = classNode.name.replace('/', '.');

        MethodNode initMethod = new MethodNode();
        initMethod.name = "<init>";
        initMethod.access = Opcodes.ACC_PUBLIC;
        initMethod.desc = "()V";
        initMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "de/blazemcworld/fireflow/compiler/CompiledNode", "<init>", "()V"));
        initMethod.instructions.add(new InsnNode(Opcodes.RETURN));
        classNode.methods.add(initMethod);
    }

    public byte[] compile() {
        for (Map.Entry<Instruction, Integer> entry : uses.entrySet()) {
            if (entry.getValue() <= 1) continue;
            createMethod(entry.getKey());
        }

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        classNode.accept(w);
        return w.toByteArray();
    }

    private void createMethod(Instruction i) {
        if (methods.containsKey(i)) return;
        MethodNode methodNode = new MethodNode();
        methods.put(i, methodNode);
        methodNode.name = names.computeIfAbsent(i, (_i) -> "m" + names.size());
        methodNode.access = Opcodes.ACC_PUBLIC;
        methodNode.desc = Type.getMethodDescriptor(i.returnType());

        methodNode.instructions.add(new CpuCheckInstruction().compile(this));
        methodNode.instructions.add(i.compile(this));
        methodNode.instructions.add(new InsnNode(i.returnType().getOpcode(Opcodes.IRETURN)));
        classNode.methods.add(methodNode);
    }

    public String markRoot(Instruction i) {
        prepare(i);
        uses.put(i, 999);
        return names.computeIfAbsent(i, (_i) -> "m" + names.size());
    }

    public void prepare(Instruction instruction) {
        uses.put(instruction, uses.getOrDefault(instruction, 0) + 1);
        if (uses.get(instruction) == 1) instruction.prepare(this);
    }

    public InsnList compile(Instruction i) {
        if (uses.get(i) <= 1) return i.compile(this);
        String name = names.computeIfAbsent(i, (_i) -> "m" + names.size());
        createMethod(i);
        InsnList out = new InsnList();
        out.add(new VarInsnNode(Opcodes.ALOAD, 0));
        out.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, name, Type.getMethodDescriptor(i.returnType())));
        return out;
    }
}
