package catalog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buffer.BufferManager;
import storage.TableFile;

public class CatalogManager {

    private final boolean isFirstRun;

    // The master in-memory catalog structure: SchemaName -> (TableName ->
    // TableSchema)
    private Map<String, Map<String, TableSchema>> schemas;

    private final BufferManager bufferManager;

    // The system table file operating with your new independent constructor
    // overload
    private final TableFile catalogTableFile;
    private static final int CATALOG_TABLE_ID = 0;
    private static final String DEFAULT_SCHEMA = "public";

    public CatalogManager(String dbDirectory, BufferManager bufferManager) throws Exception {

        this.bufferManager = bufferManager;
        // 1. Physical check: Does the catalog database file already exist on disk?
        java.io.File catalogFile = new java.io.File(dbDirectory + "/system/catalog.db");
        this.isFirstRun = !catalogFile.exists();

        // 2. Build the fixed system schema required to read/write the catalog itself
        TableSchema systemSchema = createSystemCatalogSchema();

        // 3. CORRECT CONSTRUCTOR OVERLOAD: Uses only the ID and the system schema
        // Your TableFile internal logic will safely point its DiskManager to
        // "data/system/system_catalog.db"
        this.catalogTableFile = new TableFile(CATALOG_TABLE_ID, systemSchema);

        // 4. BOOTSTRAP FLUX
        if (this.isFirstRun) {
            System.out.println("LOG [Catalog]: First execution. Creating empty metadata structures...");
            this.schemas = new HashMap<>();
            this.schemas.put(DEFAULT_SCHEMA, new HashMap<>());

            // Persists the initial empty state to disk immediately
            this.saveToDisk();
        } else {

            // se não for a primeira chamada eu seu como acessar ele no disco porque tenho
            // tablefile proprio do sistema
            System.out.println("LOG [Catalog]: Metadata file detected. Loading schemas into memory...");
            this.schemas = new HashMap<>();
            this.loadFromDisk();
        }
    }

    public void createSchema(String schemaName) {
        if (schemas.containsKey(schemaName)) {
            throw new IllegalArgumentException("Erro de DBA: O esquema " + schemaName + " já existe.");
        }
        schemas.putIfAbsent(schemaName, new HashMap<>());
        System.out.println("LOG [Catalog]: Novo schema '" + schemaName + "' criado com sucesso.");
    }

    public void createTable(String schemaName, String tableName, TableSchema schema) {
        if (!schemas.containsKey(schemaName)) {
            throw new IllegalArgumentException("Erro de DBA. O esquema " + schemaName + " não existe no catálogo.");
        }

        Map<String, TableSchema> tables = schemas.get(schemaName);

        if (tables.containsKey(tableName)) {
            throw new IllegalArgumentException(
                    "Erro: A tabela '" + tableName + "' já existe no schema '" + schemaName + "'.");
        }

        tables.put(tableName, schema);
        schemas.put(schemaName, tables);

        System.out.println(
                "LOG: Tabela '" + tableName + "' amarrada com sucesso dentro do esquema '" + schemaName + "'.");
    }

    public TableSchema getTableSchema(String schemaName, String tableName) {
        Map<String, TableSchema> tabelasDoSchema = schemas.get(schemaName);

        if (tabelasDoSchema == null || !tabelasDoSchema.containsKey(tableName)) {
            throw new IllegalArgumentException(
                    "Erro de Busca: A tabela '" + schemaName + "." + tableName + "' não foi encontrada.");
        }

        return tabelasDoSchema.get(tableName);
    }

    /**
     * Verifica se uma tabela existe no sistema.
     */
    public boolean tableExists(String tableName) {

        Map<String, TableSchema> tables = schemas.get(tableName);
        return tables.containsKey(tableName);
    }

    /**
     * Remove uma tabela do dicionário de dados (Equivale ao "DROP TABLE").
     */
    public void dropTable(String tableName) {
        Map<String, TableSchema> tables = schemas.get(tableName);

        if (!tables.containsKey(tableName)) {
            throw new IllegalArgumentException(
                    "Erro de SGBD: Impossível deletar. Tabela '" + tableName + "' não encontrada.");
        }
        tables.remove(tableName);
        System.out.println("LOG [Catalog]: Tabela '" + tableName + "' removida dos metadados.");
    }

    public void saveToDisk() {
        try {
            File directory = new File("data/system");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            List<byte[]> recordBinary = CatalogSerializer.serialize(this.schemas);

            // 1. Traz a página 0 para a RAM e incrementa o pinCount internamente no pool
            bufferManager.fetchPage(CATALOG_TABLE_ID);

            // 2. Escreve os registros binários estruturados usando o TableFile
            this.catalogTableFile.writeAllRecords(recordBinary);

            // 3. CORREÇÃO: Avisa o BufferManager que terminamos e que a página está SUJA
            // (true)
            // O BufferManager vai achar o frame da página 0, dar o setDirty(true) e o
            // unpin() de forma elegante
            bufferManager.unpinPage(CATALOG_TABLE_ID, true);

            System.out.println("LOG [Catalog]: Dicionário de dados persistido com sucesso via Buffer Pool.");

        } catch (Exception e) {
            System.err.println("Erro crítico de DBA ao salvar metadados no disco: " + e.getMessage());
        }
    }

    /**
     * Define a estrutura física (as colunas) da tabela do sistema
     */
    private static TableSchema createSystemCatalogSchema() {
        TableSchema schema = new TableSchema();

        // Adiciona as colunas necessárias para rastrear os metadados do banco
        schema.addColumn("schema_name", DataType.VARCHAR);
        schema.addColumn("table_name", DataType.VARCHAR);
        schema.addColumn("column_name", DataType.VARCHAR);
        schema.addColumn("data_type", DataType.VARCHAR);

        return schema;
    }

    // ALTERADO POR GEMINI (CORREÇÃO DE ARQUITETURA)
    public synchronized void loadFromDisk() {
        try {
            // 1. Traz a página física do catálogo para o pool de memória RAM
            bufferManager.fetchPage(CATALOG_TABLE_ID);

            // 2. O TableFile lê os registros binários a partir dos bytes da página
            List<byte[]> binaryRecords = this.catalogTableFile.readAllRecords();

            // 3. Passa os bytes para o Serializer reconstruir o mapa em memória
            Map<String, Map<String, TableSchema>> decodedMaps = CatalogSerializer.deserialize(binaryRecords);

            this.schemas.clear();
            this.schemas.putAll(decodedMaps);

            // 4. CORREÇÃO: Operação de leitura concluída. Liberamos o pino.
            // Como foi apenas uma leitura, passamos 'false' no dirtyBit porque os dados não
            // mudaram!
            bufferManager.unpinPage(CATALOG_TABLE_ID, false);

            System.out.println("LOG [Catalog]: Metadata successfully reloaded into RAM.");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to load catalog metadata from disk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Getter to inspect the schemas during tests
    public Map<String, Map<String, TableSchema>> getSchemas() {
        return this.schemas;
    }

    /**
     * DDL: Adds a new column to the end of the table schema (Sequential insertion).
     */
    public synchronized void addColumn(String schemaName, String tableName, String columnName, DataType dataType)
            throws Exception {
        TableSchema tableSchema = getTableSchemaOrThrow(schemaName, tableName);

        // Uses the simple method from your TableSchema model
        tableSchema.addColumn(columnName, dataType);

        System.out
                .println("LOG [Catalog]: Column '" + columnName + "' added sequentially to table '" + tableName + "'.");

        // Persists the metadata changes immediately
        this.saveToDisk();
    }

    /**
     * DDL: Adds or replaces a column at a specific physical index (Crucial for
     * bootstrap/deserialization).
     */
    public synchronized void addColumnAt(String schemaName, String tableName, int index, ColumnSchema column)
            throws Exception {
        TableSchema tableSchema = getTableSchemaOrThrow(schemaName, tableName);

        // Uses the slotted/index method from your TableSchema model
        tableSchema.addColumnAt(index, column);

        System.out.println("LOG [Catalog]: Column '" + column.getName() + "' placed at index " + index + " for table '"
                + tableName + "'.");

        this.saveToDisk();
    }

    /**
     * DDL: Drops a column from the table schema by its name.
     */
    public synchronized void dropColumn(String schemaName, String tableName, String columnName) throws Exception {
        TableSchema tableSchema = getTableSchemaOrThrow(schemaName, tableName);

        // Delegates the physical removal to the model
        tableSchema.dropColumn(columnName);

        System.out.println("LOG [Catalog]: Column '" + columnName + "' dropped from table '" + tableName + "'.");

        this.saveToDisk();
    }

    /**
     * DDL: Alters the data type of an existing column.
     * Replaces the old index lookup with a safe name-based iteration.
     */
    public synchronized void alterColumn(String schemaName, String tableName, String columnName, DataType newDataType)
            throws Exception {
        TableSchema tableSchema = getTableSchemaOrThrow(schemaName, tableName);

        // Safe lookup directly over the valid columns list
        List<ColumnSchema> currentColumns = tableSchema.getColumns();
        int targetIndex = -1;

        for (int i = 0; i < currentColumns.size(); i++) {
            if (currentColumns.get(i).getName().equalsIgnoreCase(columnName)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) {
            throw new Exception("DBMS Error: Column '" + columnName + "' does not exist in table '" + tableName + "'!");
        }

        // Creates the new column profile and updates it via the index slot method
        ColumnSchema alteredColumn = new ColumnSchema(columnName, newDataType);
        tableSchema.addColumnAt(targetIndex, alteredColumn);

        System.out.println("LOG [Catalog]: Column '" + columnName + "' successfully altered to " + newDataType + ".");

        this.saveToDisk();
    }

    /**
     * Private helper to fetch the TableSchema from the map or throw a clean
     * database exception.
     */
    private TableSchema getTableSchemaOrThrow(String schemaName, String tableName) throws Exception {
        Map<String, TableSchema> tableMap = this.schemas.get(schemaName);
        if (tableMap == null) {
            throw new Exception("DBMS Error: Schema '" + schemaName + "' does not exist!");
        }

        TableSchema tableSchema = tableMap.get(tableName);
        if (tableSchema == null) {
            throw new Exception("DBMS Error: Table '" + tableName + "' does not exist in schema '" + schemaName + "'!");
        }

        return tableSchema;
    }

}
