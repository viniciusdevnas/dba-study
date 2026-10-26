package mini_lab_sgbd;

import engine.DatabaseEngine;
import catalog.CatalogManager;
import catalog.TableSchema;
import catalog.DataType;

public class DBMSMemoryIntegrationTest {

    // Cores ANSI (Estilo terminal retrô anos 80)
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BRIGHT_GREEN = "\u001B[1;32m";
    private static final String AMBER = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED = "\u001B[1;31m";

    public static void main(String[] args) {
        System.out.println(BRIGHT_GREEN + "====== INICIANDO TESTE DE INTEGRAÇÃO REAL (VIA ENGINE) ======\n" + RESET);

        String rootDirectory = "data";

        try {
            // ----------------------------------------------------------------
            // CENÁRIO 1: PRIMEIRA INICIALIZAÇÃO DO BANCO (BOOT DO SISTEMA)
            // ----------------------------------------------------------------
            System.out.println(AMBER + "--- [Cenário 1] Ligando a DatabaseEngine pela primeira vez ---" + RESET);
            
            // A Engine vai ligar o DiskManager, o BufferManager e criar o Catálogo do Sistema automaticamente!
            DatabaseEngine engine = new DatabaseEngine(rootDirectory);
            
            System.out.println(GREEN + "\n[Engine] Criando um banco de dados de usuário chamado 'financeiro_db'..." + RESET);
            engine.createDatabase("financeiro_db");
            
            System.out.println(GREEN + "\n[Engine] Mudando o contexto para o 'financeiro_db'..." + RESET);
            engine.useDatabase("financeiro_db");
           
            // Pegamos o catálogo do banco que está ativo na sessão
            CatalogManager catalog = engine.getActiveCatalog();

            System.out.println(CYAN + "\n[DDL] Criando tabela de 'contas' no catálogo do 'financeiro_db'..." + RESET);
            TableSchema contasSchema = new TableSchema();
            contasSchema.addColumn("id", DataType.BIGINT);
            contasSchema.addColumn("descricao", DataType.VARCHAR);
            
            // Amarra a tabela no catálogo local do banco ativo
            catalog.createTable("public", "contas", contasSchema);

            System.out.println(GREEN + "\n[Persistência] Salvando o dicionário de dados no disco através do Buffer Pool..." + RESET);
            catalog.saveToDisk();
            
            System.out.println(BRIGHT_GREEN + "\n--- [Cenário 1] Sucesso. Desligando o servidor para limpar a RAM ---" + RESET);
            System.out.println(BRIGHT_GREEN + "========================================================================\n" + RESET);


            // ----------------------------------------------------------------
            // CENÁRIO 2: REINICIALIZAÇÃO DO BANCO (RECOVERY VIA DISCOVERY)
            // ----------------------------------------------------------------
            System.out.println(AMBER + "--- [Cenário 2] Reiniciando a DatabaseEngine (Simulando Boot Real) ---" + RESET);
            
            // Instanciamos uma Engine totalmente nova. 
            // O construtor vai rodar o discoveryExistingDatabase() e achar a pasta 'financeiro_db' no HD
            DatabaseEngine freshEngine = new DatabaseEngine(rootDirectory);
            
            System.out.println(GREEN + "\n[Engine] Tentando dar 'USE' no banco que foi salvo no Cenário 1..." + RESET);
            freshEngine.useDatabase("financeiro_db");
            
            CatalogManager freshCatalog = freshEngine.getActiveCatalog();

            System.out.println(CYAN + "\n[Validação] Checando se os metadados sobreviveram ao reboot..." + RESET);
            if (freshCatalog.tableExists("contas")) {
                System.out.println(BRIGHT_GREEN + "🎉 SUCESSO ABSOLUTO! A tabela 'contas' foi recuperada com sucesso via Buffer Pool." + RESET);
                
                // Inspeciona as colunas para garantir que o binário foi decodificado certo
                TableSchema recoveredSchema = freshCatalog.getTableSchema("public", "contas");
                System.out.println(GREEN + "📊 Estrutura recuperada da tabela 'contas':" + RESET);
                recoveredSchema.getColumns().forEach(col -> 
                    System.out.println(GREEN + "   -> Coluna: " + col.getName() + " | Tipo: " + col.getType() + RESET)
                );
            } else {
                System.err.println(RED + "❌ ERRO: O banco subiu, mas a tabela 'contas' sumiu do catálogo!" + RESET);
            }

        } catch (Exception e) {
            System.err.println(RED + "\n❌ FALHA NO TESTE DE INTEGRAÇÃO:" + RESET);
            e.printStackTrace();
        }
    }
}