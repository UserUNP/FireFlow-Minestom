package de.blazemcworld.fireflow.value;

import java.util.ArrayList;
import java.util.List;

public class AllValues {

    public static final List<Value> dataOnly = List.of(
            ConditionValue.INSTANCE,
            ListValue.get(NumberValue.INSTANCE),
            MessageValue.INSTANCE,
            NumberValue.INSTANCE,
            PlayerValue.INSTANCE,
            TextValue.INSTANCE
    );

    public static final List<Value> any = new ArrayList<>(dataOnly.size());
    static {
        any.add(SignalValue.INSTANCE);
        any.addAll(dataOnly);
    }

    public static Value get(String name) {
        for (Value value : any) {
            if (value.getBaseName().equals(name)) return value;
        }
        return null;
    }
}
