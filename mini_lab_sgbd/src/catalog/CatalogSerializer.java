package catalog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogSerializer {

	public static List<byte[]> serialize(Map<String, Map<String, TableSchema>> schemas) throws IOException{
		List<byte[]> records = new ArrayList<>();
		
		for (Map.Entry<String, Map<String, TableSchema>> schemaEntry : schemas.entrySet()) {
			String schemaName = schemaEntry.getKey();
			Map<String, TableSchema> tables = schemaEntry.getValue();
			
			for(Map.Entry<String, TableSchema> tableEntry: tables.entrySet()) {
				String tableName = tableEntry.getKey();
				TableSchema tableSchema = tableEntry.getValue();
                List<ColumnSchema> columns = tableSchema.getColumns();
             // Varre coluna por coluna salvando a posição física (index)
                for (int i = 0; i < columns.size(); i++) {
                    ColumnSchema col = columns.get(i);
                    
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);

                    // Escreve os metadados no fluxo de bytes
                    dos.writeUTF(schemaName);
                    dos.writeUTF(tableName);
                    dos.writeUTF(col.getName());
                    dos.writeUTF(col.getType().name());// Salva o nome do tipo (INT, VARCHAR...)
                    dos.writeInt(i); // Mantém a ordem física original

                    dos.flush();
                    records.add(baos.toByteArray());
                }
            }
        }
        return records;
	}
	
	public static Map<String, Map<String, TableSchema>> deserialize(List<byte[]> records) throws IOException {
        Map<String, Map<String, TableSchema>> schemas = new HashMap<>();
        // O "public" sempre precisa estar de pé
        schemas.put("public", new HashMap<>());

        for (byte[] record : records) {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(record));

            String schemaName = dis.readUTF();
            String tableName = dis.readUTF();
            String columnName = dis.readUTF();
            String dataTypeName = dis.readUTF();
            int columnIndex = dis.readInt();

            // 1. Reconstrói o Schema se ele não existir na RAM
            schemas.putIfAbsent(schemaName, new HashMap<>());
            Map<String, TableSchema> tables = schemas.get(schemaName);

            // 2. Reconstrói a tabela se ela não existir na RAM
            tables.putIfAbsent(tableName, new TableSchema());
            TableSchema tableSchema = tables.get(tableName);

            // 3. Recria o perfil da coluna e insere de volta na estrutura
            DataType type = DataType.valueOf(dataTypeName);
            ColumnSchema col = new ColumnSchema(columnName, type);
            
            // Garante que a coluna vai para a posição física correta na inicialização
            tableSchema.addColumnAt(columnIndex, col); 
        }

        return schemas;
    }
}

