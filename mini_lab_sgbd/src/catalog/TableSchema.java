package catalog;

import java.util.ArrayList;
import java.util.List;

public class TableSchema {
    private final List<ColumnSchema> columns;

    public TableSchema() {
        this.columns = new ArrayList<>();
    }

    public void addColumn(String name, DataType type) {
        this.columns.add(new ColumnSchema(name, type));
    }

    public List<ColumnSchema> getColumns() {
        return columns;
    }

    public int getColumnCount() {
        return columns.size();
    }
}