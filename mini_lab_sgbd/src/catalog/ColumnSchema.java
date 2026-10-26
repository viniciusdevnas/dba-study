package catalog;

import java.util.Objects;

public class ColumnSchema {
	
	private final String name;
    private final DataType type;
    
    @Override
	public int hashCode() {
		return Objects.hash(name, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnSchema other = (ColumnSchema) obj;
		return Objects.equals(name, other.name) && type == other.type;
	}
	

    public ColumnSchema(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public DataType getType() { return type; }
}
