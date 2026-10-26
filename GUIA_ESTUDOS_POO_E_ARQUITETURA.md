# 🏛️ Guia Mestre: Plano de Estudos POO, Arquitetura do SGBD & Backlog de Issues

---

## 📑 Sumário
1. [Bônus Retrô: Cores no Terminal Java (Verde Fósforo Anos 80)](#-bônus-retrô-cores-no-terminal-java-verde-fósforo-anos-80)
2. [Fontes Oficiais e Bibliografia Recomendada](#-1-fontes-oficiais-e-bibliografia-recomendada)
3. [Revisão Essencial: Associação vs. Agregação vs. Composição](#-2-revisão-essencial-associação-vs-agregação-vs-composição)
4. [Plano de Estudos Prático: Herança e Polimorfismo em SGBDs](#-3-plano-de-estudos-prático-herança-e-polimorfismo-em-sgbds)
5. [Guia de Engenharia Reversa para o Desenvolvedor](#-4-guia-de-engenharia-reversa-para-o-desenvolvedor)
6. [Backlog de Furos & Débitos Técnicos (Issues do GitHub)](#-5-backlog-de-furos--issues-para-o-github)

---

## 📟 Bônus Retrô: Cores no Terminal Java (Verde Fósforo Anos 80)

Para pintar o texto do console no Java como aqueles monitores de fósforo verde monocromáticos da década de 80 (terminais VT100/IBM), usamos os **Códigos de Escape ANSI** (*ANSI Escape Codes*).

### Como funciona:
Um código ANSI começa com o caractere `\u001B` (ESC), seguido por `[` e os parâmetros de estilo e cor.

| Efeito | Código ANSI | Descrição |
| :--- | :--- | :--- |
| **Reset** | `\u001B[0m` | Volta à cor padrão do terminal |
| **Verde Clássico** | `\u001B[32m` | Verde padrão retrô |
| **Verde Fósforo Brilhante** | `\u001B[1;32m` | Verde em negrito/alta intensidade (estilo Matrix) |
| **Fundo Preto com Texto Verde** | `\u001B[40;1;32m` | Imita perfeitamente a tela antiga |

### Exemplo de Classe Utilitária para usar no seu SGBD:

```java
package util;

public class RetroTerminal {
    // Códigos ANSI de estilo e cor
    public static final String RESET = "\u001B[0m";
    public static final String GREEN_RETRO = "\u001B[32m";
    public static final String GREEN_BRIGHT = "\u001B[1;32m";
    public static final String AMBER_RETRO = "\u001B[33m"; // Fósforo âmbar/laranja dos anos 80
    public static final String RED_ALERT = "\u001B[1;31m";

    // Exemplo de log formatado
    public static void log(String modulo, String mensagem) {
        System.out.println(GREEN_BRIGHT + "[" + modulo + "] " + GREEN_RETRO + mensagem + RESET);
    }

    public static void alert(String modulo, String mensagem) {
        System.out.println(RED_ALERT + "[ALERTA - " + modulo + "] " + mensagem + RESET);
    }
}
```

**Como usar no seu código:**
```java
RetroTerminal.log("Engine", "Inicializando motor de armazenamento...");
RetroTerminal.log("Catalog", "Schema 'public' carregado na RAM com sucesso.");
```

---

## 📚 1. Fontes Oficiais e Bibliografia Recomendada

> Estas são as referências técnicas mais respeitadas mundialmente para dominar Orientação a Objetos na JVM, indicando exatamente os capítulos e itens necessários.

### 1. Livro: *Use a Cabeça! Java (Head First Java)* — Kathy Sierra & Bert Bates
* **Por que é a referência:** É a melhor explicação didática e visual de como a memória da JVM (Stack e Heap) se comporta durante a herança e o polimorfismo, e como a resolução dinâmica de métodos (*late binding*) acontece.
* **Capítulos Obrigatórios:**
  * **Capítulo 7:** *Melhorando sua visão de mundo: Herança e Polimorfismo*.
  * **Capítulo 8:** *Seja sério: Interfaces e Classes Abstratas*.

### 2. Livro: *Java Efetivo (Effective Java)* — Joshua Bloch
* **Por que é a referência:** Joshua Bloch foi o arquiteto chefe da biblioteca `java.util` da Sun/Oracle. Este livro ensina as armadilhas clássicas que destroem projetos Java quando herança é mal utilizada.
* **Itens Cirúrgicos para Estudar:**
  * **Item 18:** *Prefira composição a herança.* (Explica por que herança acopla classes demais).
  * **Item 19:** *Projete e documente para a herança, ou então proíba-a.*
  * **Item 20:** *Prefira interfaces a classes abstratas.*
  * **Item 22:** *Use interfaces apenas para definir tipos.*

### 3. Livro: *Use a Cabeça! Padrões de Projetos (Head First Design Patterns)* — Eric Freeman & Elisabeth Robson
* **Por que é a referência:** Demonstra como polimorfismo e interfaces resolvem problemas de arquitetura reais em sistemas corporativos.
* **Capítulos Cirúrgicos:**
  * **Capítulo 1:** *Padrão Strategy* — A introdução definitiva a polimorfismo na prática.
  * **Capítulo 6:** *Padrão Command* — O padrão que transforma operações de banco (DDL/DML) em objetos executáveis.

### 4. Documentação Oficial: *The Java™ Tutorials (Oracle)*
* **Trilha Oficial:** *Learning the Java Language -> Interfaces and Inheritance*.
* **Tópicos para Consulta:** Palavras-chave `abstract`, `super`, `default methods`, `instanceof` e especificadores de acesso (`protected`, `package-private`).

---

## 🧩 2. Revisão Essencial: Associação vs. Agregação vs. Composição

Antes de mergulhar em herança, é fundamental dominar as três formas de relacionamento entre classes, pois 90% do design de um SGBD depende delas:

```
┌─────────────────────────────────────────────────────────────┐
│                    TIPOS DE RELACIONAMENTO                  │
├──────────────────┬──────────────────────┬───────────────────┤
│   Associação     │      Agregação       │    Composição     │
│   ("Usa um")     │ ("Tem um" - Fraco)   │("Tem um" - Forte) │
│  Independência   │ Ciclos Independentes │ Morte Compartilhada│
└──────────────────┴──────────────────────┴───────────────────┘
```

### 1. Associação ("Usa um" / "Conhece um")
* **Conceito:** Duas classes colaboram temporariamente. Nenhuma é dona da outra.
* **Exemplo no seu SGBD:** `CatalogManager` e `BufferManager`.
* **Análise:** O `CatalogManager` usa o `BufferManager` para dar `fetchPage` e `unpinPage`. Se o `CatalogManager` for destruído, o `BufferManager` continua ativo no banco de dados atendendo outras partes.

### 2. Agregação ("Tem um" — Relação Fraca)
* **Conceito:** Uma classe contém referências a outras, mas as "partes" existem por si mesmas fora do "todo".
* **Exemplo no seu SGBD:** `DatabaseEngine` e os catálogos dos bancos em `Map<String, CatalogManager> databases`.
* **Análise:** A Engine gerencia vários bancos de dados. Se a Engine for desligada ou reiniciada, os bancos não deixam de existir; seus arquivos continuam no disco e podem ser agregados por outra Engine.

### 3. Composição ("Tem um" — Relação Forte / Posse Exclusiva)
* **Conceito:** Uma classe é proprietária absoluta das suas partes. Ela as cria e as destrói. Se o pai morrer, as partes morrem junto.
* **Exemplo no seu SGBD:** `BufferManager` e seus `Frame`s (`Frame[] pool`), ou `TableSchema` e suas `ColumnSchema`.
* **Análise:** O `BufferManager` instancia os 100 frames diretamente no seu construtor. Um `Frame` não tem razão de existir solto na memória sem pertencer a um Buffer Pool. Se o BufferManager é coletado pelo Garbage Collector, seus frames morrem com ele.

---

## 🗓️ 3. Plano de Estudos Prático: Herança e Polimorfismo em SGBDs

> **Carga Semanal:** 5 horas (Dia 1: 1h de teoria nas fontes / Dias 2 a 5: 4h de prática no seu próprio projeto).

---

### 🟢 Semana 1: Da Programação Estruturada para Objetos (Herança e Encapsulamento)
* **Teoria (1h):**
  * Ler: *Head First Java (Capítulo 7)*.
  * Foco: O que é relação "É UM" (*Is-A*). Visibilidade `protected` vs `private`. Como encadear construtores com `super()`.
* **Prática no seu SGBD (4h):**
  * **Problema atual:** O enum `DataType` atual obriga o uso de condicionais espalhados para saber o tamanho em bytes e o formato de cada tipo.
  * **Desafio Prático:** Criar uma classe abstrata `DataValue` da qual classes filhas herdam:
    * `DataValue` (abstrata) -> define métodos comuns como `abstract byte[] toBytes()`, `abstract int getSize()`.
    * `BigIntValue`, `VarcharValue`, `IntegerValue` (concretas) -> implementam sua própria representação binária.

---

### 🟡 Semana 2: Polimorfismo Dinâmico (Late Binding & Princípio de Liskov)
* **Teoria (1h):**
  * Ler: *Head First Java (Capítulo 8)* e *Effective Java (Item 18)*.
  * Foco: Como a JVM resolve o método certo em tempo de execução via *vtable* (tabela virtual). Princípio de Substituição de Liskov (LSP).
* **Prática no seu SGBD (4h):**
  * **Desafio Prático:** Refatorar o `Serializer.java` e a classe `Tuple`.
  * Em vez de fazer checagens manuais de tipo na tupla, a tupla armazena uma lista polimórfica: `List<DataValue>`.
  * O `Serializer` apenas itera: `for (DataValue val : tuple.getValues()) { stream.write(val.toBytes()); }`. O polimorfismo chama a implementação correta de cada tipo sem um único `if`.

---

### 🟠 Semana 3: Interfaces como Contratos de Isolamento (Strategy Pattern)
* **Teoria (1h):**
  * Ler: *Head First Design Patterns (Capítulo 1)* e *Effective Java (Item 20)*.
  * Foco: Interfaces não guardam estado de dados; estabelecem regras de comportamento que qualquer classe pode assinar.
* **Prática no seu SGBD (4h):**
  * **Problema atual:** O algoritmo de descarte de páginas (*eviction*) do `BufferManager` é estático e rígido.
  * **Desafio Prático:** Criar a interface `EvictionPolicy`:
    ```java
    public interface EvictionPolicy {
        int chooseVictim(Frame[] pool);
        void recordAccess(int frameId);
    }
    ```
  * Criar duas classes que assinam essa interface: `FifoEvictionPolicy` e `ClockEvictionPolicy`.
  * O `BufferManager` passa a receber a interface no construtor.

---

### 🔴 Semana 4: O Padrão Command no Motor do Banco
* **Teoria (1h):**
  * Ler: *Head First Design Patterns (Capítulo 6 - Command Pattern)*.
  * Foco: Transformar ações de sistema em objetos de comando executáveis com contrato único `execute()`.
* **Prática no seu SGBD (4h):**
  * **Desafio Prático:** Criar a interface `DatabaseCommand`:
    ```java
    public interface DatabaseCommand {
        void execute(ExecutionContext ctx) throws Exception;
    }
    ```
  * Criar classes concretas: `CreateTableCommand`, `UseDatabaseCommand`.
  * A `DatabaseEngine` se torna limpa, delegando qualquer execução para `command.execute(this.context)`.

---

## 🧭 4. Guia de Engenharia Reversa para o Desenvolvedor

Este é o roteiro de perguntas para **você** abrir o código-fonte, dissecar a arquitetura e documentar:

1. **Rastreamento do Ciclo de Vida do Dado:**
   * *Pergunta:* Como uma tupla de dados sai da memória e chega no disco?
   * *Roteiro de inspeção:* Siga o caminho `Tuple` -> `Serializer` -> `TableFile.insertRecord()` -> `Page.insertRecord()` -> `DiskManager.writePage()`.
2. **Isolamento de Memória (Buffer Pool):**
   * *Pergunta:* Quem consome o `BufferManager`?
   * *Roteiro de inspeção:* O catálogo usa o buffer pool (`fetchPage(0)` / `unpinPage(0)`). O `TableFile` está usando o buffer pool ou conversando direto com o `DiskManager`?
3. **Mapeamento das Relações:**
   * Para cada classe, anote:
     * O que ela **usa**? (Associação)
     * O que ela **agrega**? (Agregação)
     * O que ela **possui com exclusividade**? (Composição)

---

## 🐛 5. Backlog de Furos & Issues para o GitHub

Copie e cole estas 5 issues diretamente no GitHub para gerenciar seu projeto:

---

### Issue #1: [BUG] `discoveryExistingDatabase` ignora bancos de usuários e só recarrega `system`
* **Tipo:** Bug / Crash
* **Severidade:** Alta
* **Arquivo:** `src/engine/DatabaseEngine.java` (Linha 148)
* **Descrição:**  
  No boot/reboot (`new DatabaseEngine(root)`), o método `discoveryExistingDatabase()` usa a condição:
  ```java
  if (file.isDirectory() && file.getName().equalsIgnoreCase("system"))
  ```
  Isso faz com que o discovery ignore qualquer banco que **não** seja `"system"`. O banco `"system"` já foi carregado no construtor. Como resultado, bancos criados pelo usuário (como `"financeiro_db"`) nunca são descobertos, e `USE <banco>` falha após o reboot.
* **Solução:**  
  Adicionar a negação para ignorar o `"system"` e carregar os demais:
  ```java
  if (file.isDirectory() && !file.getName().equalsIgnoreCase("system"))
  ```

---

### Issue #2: [BUG] `createDatabase` não persiste o diretório físico no HD
* **Tipo:** Bug / Persistência
* **Severidade:** Alta
* **Arquivo:** `src/engine/DatabaseEngine.java` (Linhas 75–97)
* **Descrição:**  
  O método calcula a string `dbPath = this.rootDirectory + "/" + sanitizedPathName;` mas não invoca `new File(dbPath).mkdirs()`. O catálogo nasce na RAM, mas a pasta física nunca é criada no disco.
* **Solução:**  
  Criar a pasta física antes de instanciar o catálogo:
  ```java
  File dbFolder = new File(this.rootDirectory, sanitizedPathName);
  if (!dbFolder.exists()) {
      dbFolder.mkdirs();
  }
  ```

---

### Issue #3: [BUG] Métodos `tableExists` e `dropTable` disparam `NullPointerException`
* **Tipo:** Bug / Crash
* **Severidade:** Alta
* **Arquivo:** `src/catalog/CatalogManager.java` (Linhas 99 e 107)
* **Descrição:**  
  O mapa `schemas` armazena `Map<SchemaName, Map<TableName, TableSchema>>`. O código atual executa `schemas.get(tableName)`. Ao buscar a tabela `"contas"`, ele procura um esquema com esse nome, retornando `null` e quebrando na chamada seguinte `.containsKey(...)`.
* **Solução:**  
  Sobrecarregar os métodos para aceitar o esquema ou usar o esquema padrão `"public"`:
  ```java
  public boolean tableExists(String tableName) {
      return tableExists(DEFAULT_SCHEMA, tableName);
  }
  public boolean tableExists(String schemaName, String tableName) {
      Map<String, TableSchema> tables = schemas.get(schemaName);
      return tables != null && tables.containsKey(tableName);
  }
  ```

---

### Issue #4: [ARQUITETURA] Caminho fixo do arquivo de metadados (`catalog.db`) para `"data/system"`
* **Tipo:** Débito Técnico
* **Severidade:** Média-Alta
* **Arquivos:** `src/catalog/CatalogManager.java` (Linha 118) e `src/storage/TableFile.java` (Linha 36)
* **Descrição:**  
  O método `saveToDisk()` e o construtor do `TableFile` estão apontando fixamente para `"data/system/catalog.db"`, ignorando o diretório passado para o banco no construtor. Isso faz os catálogos de todos os bancos concorrerem no mesmo arquivo físico.
* **Solução:**  
  Salvar o `dbDirectory` passado no construtor como atributo de instância e montar o arquivo relativo a cada banco (`dbDirectory + "/catalog.db"`).

---

### Issue #5: [FEATURE] Desacoplamento entre criação de metadados e alocação do arquivo de tabela
* **Tipo:** Arquitetura / Design Pattern
* **Severidade:** Média
* **Arquivos:** `src/catalog/CatalogManager.java`, `src/storage/TableFile.java`
* **Descrição:**  
  O `createTable` registra o schema no catálogo, mas não inicializa o `TableFile` nem o arquivo `.db` de dados da tabela no diretório físico do banco.
* **Solução:**  
  Aplicar o padrão Command (`CreateTableCommand`) para orquestrar a inclusão no catálogo e a criação física do arquivo de dados via um serviço de armazenamento.
