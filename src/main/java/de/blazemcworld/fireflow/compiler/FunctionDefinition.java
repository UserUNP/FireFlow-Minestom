package de.blazemcworld.fireflow.compiler;

import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.io.NodeIO;
import de.blazemcworld.fireflow.node.io.NodeInput;
import de.blazemcworld.fireflow.node.io.NodeOutput;
import de.blazemcworld.fireflow.value.SignalValue;
import it.unimi.dsi.fastutil.Pair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class FunctionDefinition {
    public final String fnName;
    public final Node fnInputsNode;
    public final Node fnOutputsNode;
    public final List<NodeInput> fnOutputs;
    public final List<NodeOutput> fnInputs;
    private final WeakHashMap<Call, Integer> calls = new WeakHashMap<>();
    private static int callIds = 0;

    public FunctionDefinition(String fnName, List<NodeOutput> fnInputs, List<NodeInput> fnOutputs) {
        this.fnName = fnName;
        this.fnOutputs = fnOutputs;
        this.fnInputs = fnInputs;

        for (NodeOutput each : fnInputs) {
            if (each.getType() == SignalValue.INSTANCE) continue;

            Instruction peek = peekStack();
            each.setInstruction(new Instruction() {
                @Override
                public void prepare(NodeCompiler ctx) {
                    ctx.prepare(peek);
                    for (Call c : calls.keySet()) {
                        for (NodeIO.In other : c.inputs) {
                            if (!other.getName().equals(each.getName()) || !other.getType().equals(each.getType()))
                                continue;
                            ctx.prepare(other);
                        }
                    }
                }

                @Override
                public InsnList compile(NodeCompiler ctx, int usedVars) {
                    InsnList out = new InsnList();
                    LabelNode end = new LabelNode();

                    out.add(ctx.compile(peek, usedVars));
                    for (Map.Entry<Call, Integer> e : calls.entrySet()) {
                        for (NodeIO.In other : e.getKey().inputs) {
                            if (!other.getName().equals(each.getName()) || !other.getType().equals(each.getType())) continue;
                            out.add(new InsnNode(Opcodes.DUP));
                            out.add(new LdcInsnNode(e.getValue()));
                            LabelNode next = new LabelNode();
                            out.add(new JumpInsnNode(Opcodes.IF_ICMPNE, next));
                            out.add(new InsnNode(Opcodes.POP));
                            out.add(ctx.compile(other, usedVars));
                            out.add(new JumpInsnNode(Opcodes.GOTO, end));
                            out.add(next);
                        }
                    }

                    out.add(new InsnNode(Opcodes.POP));
                    out.add(each.getType().compile(null));
                    out.add(end);
                    return out;
                }

                @Override
                public Type returnType() {
                    return each.returnType();
                }
            });
        }

        for (NodeInput each : fnOutputs) {
            if (each.getType() != SignalValue.INSTANCE) continue;

            Instruction peek = peekStack();
            each.setInstruction(new Instruction() {
                @Override
                public void prepare(NodeCompiler ctx) {
                    ctx.prepare(peek);
                    for (Call c : calls.keySet()) {
                        for (NodeIO.Out other : c.outputs) {
                            if (!other.getName().equals(each.getName()) || !other.getType().equals(each.getType()))
                                continue;
                            ctx.prepare(other);
                        }
                    }
                }

                @Override
                public InsnList compile(NodeCompiler ctx, int usedVars) {
                    InsnList out = new InsnList();
                    LabelNode end = new LabelNode();

                    out.add(ctx.compile(peek, usedVars));
                    for (Map.Entry<Call, Integer> e : calls.entrySet()) {
                        for (NodeIO.Out other : e.getKey().outputs) {
                            if (!other.getName().equals(each.getName()) || !other.getType().equals(each.getType()))
                                continue;
                            out.add(new InsnNode(Opcodes.DUP));
                            out.add(new LdcInsnNode(e.getValue()));
                            LabelNode next = new LabelNode();
                            out.add(new JumpInsnNode(Opcodes.IF_ICMPNE, next));
                            out.add(new InsnNode(Opcodes.POP));
                            out.add(ctx.compile(other, usedVars));
                            out.add(new JumpInsnNode(Opcodes.GOTO, end));
                            out.add(next);
                        }
                    }

                    out.add(new InsnNode(Opcodes.POP));
                    out.add(end);
                    return out;
                }

                @Override
                public Type returnType() {
                    return each.returnType();
                }
            });
        }

        fnInputsNode = new DefinitionNode(fnName + " Inputs");
        fnOutputsNode = new DefinitionNode(fnName + " Outputs");
        fnInputsNode.outputs.addAll(fnInputs);
        fnOutputsNode.inputs.addAll(fnOutputs);
    }

    private FunctionDefinition fnDefinitionThis() {
        return this;
    }

    public class DefinitionNode extends Node {
        public DefinitionNode(String name) {
            super(name);
        }

        public FunctionDefinition getDefinition() {
            return fnDefinitionThis();
        }

        @Override
        public String getBaseName() {
            return fnName;
        }
    }

    public Call createCall() {
        return new Call();
    }

    public class Call extends Node {

        private Call() {
            super(fnName);
            calls.put(this, callIds++);

            for (NodeOutput each : fnInputs) {
                NodeInput other = input(each.getName(), each.getType());
                if (each.getType() == SignalValue.INSTANCE) {
                    other.setInstruction(new MultiInstruction(each.returnType(),
                            pushStack(calls.get(this)),
                            each,
                            popStack()
                    ));
                }
            }
            for (NodeInput each : fnOutputs) {
                NodeOutput other = output(each.getName(), each.getType());
                if (each.getType() != SignalValue.INSTANCE) {
                    other.setInstruction(new MultiInstruction(each.returnType(),
                            pushStack(calls.get(this)),
                            each,
                            popStack()
                    ));
                }
            }
        }

        public FunctionDefinition getDefinition() {
            return fnDefinitionThis();
        }
    }

    private static Instruction pushStack(int id) {
        return new InstanceMethodInstruction(
                CompiledNode.class, new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)), "pushFnStack", Type.VOID_TYPE,
                List.of(Pair.of(Type.INT_TYPE, new RawInstruction(Type.INT_TYPE, new LdcInsnNode(id))))
        );
    }

    private static Instruction popStack() {
        return new InstanceMethodInstruction(
                CompiledNode.class, new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                "popFnStack", Type.VOID_TYPE, List.of()
        );
    }

    private static Instruction peekStack() {
        return new InstanceMethodInstruction(
                CompiledNode.class, new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                "peekFnStack", Type.INT_TYPE, List.of()
        );
    }


}
