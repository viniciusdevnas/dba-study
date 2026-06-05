package catalog;

public class ColumnSchema {
    private final String name;
    private final DataType type;

    public ColumnSchema(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public DataType getType() { return type; }
}
