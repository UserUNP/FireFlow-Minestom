package de.blazemcworld.fireflow.node.io;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.value.ListValue;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.Value;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class NodeMultiInput implements NodeIO.In {

    private final String name;
    private final Value type;
    private final Value listType;
    private boolean hasInset = false;
    private @Nullable List<NodeIO.Out> sources = new ArrayList<>();
    private @Nullable NodeIO.Out listSource = null;

    public NodeMultiInput(String name, Value type) {
        if (type == SignalValue.INSTANCE) throw new IllegalArgumentException("A multi-input cannot be of type signal!");
        this.name = name;
        this.type = type;
        listType = ListValue.get(type);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Value getType() {
        return listType;
    }

    @Override
    public boolean hasInset() {
        return hasInset;
    }

    public List<IntObjectPair<Object>> getInsetList() {
        if (listSource != null || sources == null || !hasInset) throw new IllegalStateException("how");
        return IntStream.range(0, sources.size())
                .filter(i -> sources.get(i).getInset() != null)
                .mapToObj(i -> IntObjectPair.of(i, sources.get(i).getInset()))
                .toList();
    }

    @Override
    public void inset(@Nullable Object inset) {
        if (listSource != null || inset == null) throw new IllegalStateException("how");
        NodeOutput o = new NodeOutput("("+type.getFullName()+")", type);
        o.inset(inset);
        if (sources == null) sources = new ArrayList<>();
        sources.add(o);
        hasInset = true;
    }

    public void removeInset(int index) {
        if (listSource != null || sources == null) throw new IllegalStateException("how");
        NodeIO.Out o = sources.get(index);
        if (!o.hasInset()) throw new IllegalArgumentException("Source " + index + " does not contain an inset!");
        sources.remove(index);
    }

    @Override
    public void setInstruction(Instruction instruction) {
        throw new IllegalStateException("A multi-input cannot have an instruction!");
    }

    @Override
    public void prepare(NodeCompiler ctx) {
        if (sources == null) {
            if (listSource == null) throw new IllegalStateException("how");
            ctx.prepare(listSource);
            return;
        }
        for (NodeIO.Out s : sources) ctx.prepare(s);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        if (sources == null) {
            if (listSource == null) throw new IllegalStateException("how");
            return ctx.compile(listSource, usedVars);
        }
        Instruction[] instructions = new Instruction[sources.size()];
        Instruction list = new RawInstruction(listType.getType(), listType.compile(null));
        for (int i = 0; i < sources.size(); i++) {
            NodeIO.Out s = sources.get(i);
            instructions[i] = new InstanceMethodInstruction(
                    List.class, list, "add", Type.VOID_TYPE,
                    List.of(Pair.of(s.getType().getType(), s))
            );
        }
        return new MultiInstruction(listType.getType(), instructions).compile(ctx, usedVars);
    }

    @Override
    public Type returnType() {
        return listType.getType();
    }

    @Override
    public NodeInputType inputType() {
        return NodeInputType.MULTIPLE;
    }

    @Override
    public void connectValue(NodeIO.Out source) {
        if (source instanceof ListValue l) {
            if (l != listType) throw new IllegalStateException("Attempted to connect values of incompatible types!");
            sources = null;
            listSource = source;
            return;
        }
        if (source.getType() != type) throw new IllegalStateException("Attempted to connect values of incompatible types!");
        if (sources == null) sources = new ArrayList<>();
        sources.add(source);
    }

    @Override
    public NodeInput withDefault(Object value) {
        return null;
    }

    @Override
    public boolean hasDefault() {
        return false;
    }
}
