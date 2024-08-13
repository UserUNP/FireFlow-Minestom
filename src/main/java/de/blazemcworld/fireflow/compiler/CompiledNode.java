package de.blazemcworld.fireflow.compiler;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import de.blazemcworld.fireflow.space.Space;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class CompiledNode {

    public Map<String, Object> locals = new HashMap<>();
    public Stack<Integer> fnStack = new Stack<>();
    public Space space;
    public CodeEvaluator evaluator;
    private final Stack<Map<String, Object>> internalVars = new Stack<>();
    private long lastCpuCheck = System.nanoTime();

    public CompiledNode() {
        internalVars.push(new HashMap<>());
    }

    @SuppressWarnings("unused") //Used by CpuCheckInstruction
    public void cpuCheck() {
        long now = System.nanoTime();
        evaluator.cpuLeft -= now - lastCpuCheck;
        lastCpuCheck = now;
        if (evaluator.cpuLeft < 0) {
            fnStack.clear();
            throw new CpuLimitException();
        }
    }

    @SuppressWarnings("unused") //Used by FunctionDefinitions
    public void pushFnStack(int id) {
        fnStack.push(id);
        internalVars.push(new HashMap<>());
    }

    @SuppressWarnings("unused") //Used by FunctionDefinitions
    public void popFnStack() {
        fnStack.pop();
        internalVars.pop();
    }

    @SuppressWarnings("unused") //Used by FunctionDefinitions
    public int peekFnStack() {
        if (fnStack.isEmpty()) return -1;
        return fnStack.peek();
    }

    @SuppressWarnings("unused") // Used by asm
    public Object getInternalVar(String key) {
        for (int i = internalVars.size() - 1; i >= 0; i--) {
            Object v = internalVars.get(i).get(key);
            if (v != null) return v;
        }
        return null;
    }

    public void setInternalVar(String key, Object value) {
        internalVars.peek().put(key, value);
    }

    @SuppressWarnings("unused") // Used by asm
    protected void runScheduled(double delay, String id) {
        CompiledNode ctx = evaluator.newContext();
        ctx.locals = this.locals;
        ctx.internalVars.addAll(this.internalVars);

        Task[] t = new Task[] {null};

        Runnable stop = () -> t[0].cancel();
        t[0] = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            evaluator.stopEvents.remove(stop);
            ctx.emit(id);
            return TaskSchedule.stop();
        }, TaskSchedule.tick((int) Math.max(delay, 1)));
        evaluator.stopEvents.add(stop);
    }

    public void emit(String entry) {
        try {
            lastCpuCheck = System.nanoTime();
            this.getClass().getDeclaredMethod(entry).invoke(this);
        } catch (Exception err) {
            if (err instanceof InvocationTargetException invoke) {
                if (invoke.getTargetException() instanceof CpuLimitException) {
                    FireFlow.LOGGER.warn("Reached cpu limit for Space #{}!", space.info.id);
                    evaluator.stop(false);
                    return;
                }

                FireFlow.LOGGER.error("Internal evaluation error!", invoke.getTargetException());
            }
        }
    }
}
