package engine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buffer.BufferManager;
import catalog.CatalogManager;
import catalog.CatalogSerializer;
import storage.TableFile;
import storage.disk.DiskManager;

public class DatabaseEngine {

	private final Map<String, CatalogManager> databases;
	private final String rootDirectory;
	private final BufferManager globalBufferManager;
	private final DiskManager globalDiskManager;
	private String activeDatabaseName;
	private CatalogManager systemCatalog;
	
    
	private static final String SYSTEM_DB_NAME = "system";

    public DatabaseEngine(String rootDirectory) throws Exception {
		this.rootDirectory = rootDirectory;
		this.databases = new HashMap<String, CatalogManager>();
		this.activeDatabaseName = null;
        
		// 1. Defesa nativa de caminhos no Windows
		File rootFolder = new File(rootDirectory);
		if (rootFolder.exists() && rootFolder.isFile()) {
			rootFolder.delete();
		}
		if (!rootFolder.exists()) {
			rootFolder.mkdirs();
		}

		// Garante a existência física da pasta 'data/system'
		File systemFolder = new File(rootFolder, SYSTEM_DB_NAME);
		if (!systemFolder.exists()) {
			systemFolder.mkdirs();
		}

		// Determina o caminho exato do arquivo físico do catálogo do sistema
		File systemCatalogFile = new File(systemFolder, "catalog.db"); 
		boolean isFirstRun = !systemCatalogFile.exists() || systemCatalogFile.length() == 0;

		System.out.println("LOG [Engine]: Acionando Catálogo do Sistema global...");
		
		// 2. Criamos o DiskManager dedicado EXCLUSIVAMENTE para o arquivo do sistema
		// (Isso evita o AccessDeniedException na pasta!)
		DiskManager systemDiskManager = new DiskManager(systemCatalogFile.getPath());
		
		// O seu BufferManager global é inicializado usando o gerenciador de disco do sistema no boot
        this.globalBufferManager = new BufferManager(100, systemDiskManager);

        String systemDir = systemFolder.getPath();
        this.systemCatalog = new CatalogManager(systemDir, this.globalBufferManager);
        databases.put(SYSTEM_DB_NAME, systemCatalog);
        
        // 3. Se o arquivo acabou de ser criado (0 bytes), popula a RAM e força o salvamento
        if (isFirstRun) {
            System.out.println("LOG [Engine]: Primeira execução detectada. Persistindo metadados iniciais...");
            this.systemCatalog.saveToDisk(); 
        }
        
        // Atribui o disk manager do sistema ao escopo global (opcional, para conformidade da classe)
        this.globalDiskManager = systemDiskManager;
        
        this.discoveryExistingDatabase();
	}
    
	public synchronized void createDatabase(String dbName) throws Exception {
		String sanitizedPathName = dbName.trim().toLowerCase();

		if (sanitizedPathName.isEmpty()) {
			throw new Exception("DBMS Error: Database name cannot be empty.");
		}

		if (this.databases.containsKey(sanitizedPathName)) {
			throw new Exception("DBMS Error: Database '" + dbName + "' already exists.");
		}

		String dbPath = this.rootDirectory + "/" + sanitizedPathName;

		System.out.println("LOG [Engine]: Initializing physical storage for database '" + dbName + "'...");

		CatalogManager newCatalog = new CatalogManager(dbPath, globalBufferManager);

		//falta criar um schema padrao e navegar entre schemas
		this.databases.put(sanitizedPathName, newCatalog);

		System.out.println("LOG [Engine]: Database '" + dbName + "' successfully created.");
	}

	public synchronized void dropDatabase(String dbName) throws Exception {
		String sanitizedPathName = dbName.trim().toLowerCase();

		if (!this.databases.containsKey(sanitizedPathName)) {
			throw new Exception("DBMS Error: Database '" + dbName + "' does not exist.");
		}

		if (sanitizedPathName.equals(this.activeDatabaseName)) {
			this.activeDatabaseName = null;
		}

		this.databases.remove(sanitizedPathName);

		File dbDirectory = new File(this.rootDirectory + "/" + sanitizedPathName);
		if (dbDirectory.exists()) {
			deleteFolderRecursively(dbDirectory);
		}

	}
	
	public synchronized void useDatabase(String dbName) throws Exception {
        String sanitizedPathName = dbName.trim().toLowerCase();
        
        if (!this.databases.containsKey(sanitizedPathName)) {
            throw new Exception("DBMS Error: Database '" + dbName + "' does not exist.");
        }
        
        this.activeDatabaseName = sanitizedPathName;
        System.out.println("LOG [Engine]: Context switched to database '" + this.activeDatabaseName + "'.");
    }
	
	public synchronized CatalogManager getActiveCatalog() throws Exception {
        if (this.activeDatabaseName == null) {
            throw new Exception("DBMS Error: No database selected. Please execute 'USE <database_name>' first.");
        }
        return this.databases.get(this.activeDatabaseName);
    }

	
	private void discoveryExistingDatabase() throws Exception {
		File root = new File(rootDirectory);
		if(!root.exists()) {
			root.mkdirs();
			return;
		}
		
		File[] subFiles = root.listFiles();
		
		for (File file : subFiles) {
			if(file.isDirectory() && !file.getName().equalsIgnoreCase("system")){
				String dbName = file.getName().toLowerCase();
				String dbPath = rootDirectory + "/" + dbName;
				
				System.out.println("LOG [Engine]: Existing database discovered: [" + dbName + "]. Loading metadata cluster...");
				CatalogManager restoredCatalog = new CatalogManager(dbPath, globalBufferManager);
				this.databases.put(dbName, restoredCatalog);
			}
			
			
		}
	}
	private void deleteFolderRecursively(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File child : files) {
					deleteFolderRecursively(child);
				}
			}
		}

		file.delete();
	}

}