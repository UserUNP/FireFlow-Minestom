package de.blazemcworld.fireflow.node.io;

import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.value.Value;
import org.jetbrains.annotations.Nullable;

public interface NodeIO extends Instruction {
    String getName();
    Value getType();
    boolean hasInset();
    void inset(@Nullable Object inset);
    void setInstruction(Instruction instruction);

    interface In extends NodeIO {
        NodeInputType inputType();
        void connectValue(Out source);
        NodeInput withDefault(Object value);
        boolean hasDefault();
    }

    interface Out extends NodeIO {
        Object getInset();
        void connectSignal(In target);
    }
}
