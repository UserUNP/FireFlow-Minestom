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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class NodeMultiInput implements NodeIO.In {

    private final String name;
    private final Value type;
    private final Value listType;
    private final List<NodeIO.Out> sources = new ArrayList<>();

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
        return false;
    }

    public List<IntObjectPair<Object>> getInsetList() {
        return IntStream.range(0, sources.size())
                .filter(i -> sources.get(i).getInset() != null)
                .mapToObj(i -> IntObjectPair.of(i, sources.get(i).getInset()))
                .toList();
    }

    @Override
    public void inset(Object inset) {
        NodeOutput o = new NodeOutput("("+type.getFullName()+")", type);
        o.inset(inset);
        sources.add(o);
    }

    @Override
    public void setInstruction(Instruction instruction) {
        throw new IllegalStateException("A multi-input cannot have an instruction!");
    }

    @Override
    public void prepare(NodeCompiler ctx) {
        for (NodeIO.Out s : sources) ctx.prepare(s);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
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
        if (source.getType() != type) throw new IllegalStateException("Attempted to connect values of incompatible types!");
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

    public int getSourcesCount() {
        return sources.size();
    }
}
