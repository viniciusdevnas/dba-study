package storage;

import java.util.Arrays;

public class Tuple {
    private final Object[] values;

    public Tuple(Object... values) {
        this.values = values;
    }

    public Object getValue(int columnIndex) {
        return values[columnIndex];
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "Tuple " + Arrays.toString(values);
    }

	
}
