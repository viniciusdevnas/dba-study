package catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableSchema {
    private final List<ColumnSchema> columns;

    public TableSchema() {
        this.columns = new ArrayList<>();
    }

    public void addColumn(String name, DataType type) {
        this.columns.add(new ColumnSchema(name, type));
    }

    public int getColumnCount() {
        return columns.size();
    }
    
    public void dropColumn(String nameColumn) throws Exception {
        // Tenta remover o elemento que case com o nome informado (case-insensitive)
        // O método retorna 'true' se encontrou e removeu, ou 'false' se não achou nada
        boolean removed = columns.removeIf(col -> col.getName().equalsIgnoreCase(nameColumn));

        if (!removed) {
            throw new Exception("Erro de SGBD: A coluna '" + nameColumn + "' não existe nesta tabela!");
        }

        System.out.println("LOG [Catalog]: Coluna '" + nameColumn + "' removida com sucesso.");
    }
    
    public void alterColumn(String nameColumn, DataType newDataType) throws Exception {
        // 1. Procura o índice da coluna (Garante que a classe ColumnSchema tem o método equals implementado!)
        int index = columns.indexOf(new ColumnSchema(nameColumn, null));
        
        // Validação de segurança obrigatória para o SGBD não quebrar se o usuário digitar o nome errado
        if (index == -1) {
            throw new Exception("Erro de SGBD: A coluna '" + nameColumn + "' não existe nesta tabela!");
        }
        
        // 2. Monta o novo perfil da coluna
        ColumnSchema column = new ColumnSchema(nameColumn, newDataType);
        
        // 3. A CORREÇÃO: Troca o ponteiro antigo pelo novo exatamente na mesma posição
        columns.set(index, column); 
        
        System.out.println("LOG [Catalog]: Coluna '" + nameColumn + "' alterada com sucesso.");
    }
    
    /**
     * Adiciona a coluna exatamente na posição física (index) desejada,
     * sem precisar que o ColumnSchema saiba qual é o seu próprio índice.
     */
    public synchronized void addColumnAt(int index, ColumnSchema column) {
        // Garantia de expansão: Se o índice for maior que o tamanho atual da lista,
        // nós preenchemos os espaços vazios com null para não dar IndexOutOfBoundsException
        while (columns.size() <= index) {
            columns.add(null);
        }

        // Insere ou substitui a coluna exatamente no slot físico correto
        columns.set(index, column);
    }

    /**
     * Retorna a lista de colunas limpa (removendo possíveis slots nulos de segurança)
     * na ordem física exata para o seu Serializer.
     */
    public List<ColumnSchema> getColumns() {
        List<ColumnSchema> validColumns = new ArrayList<>();
        for (ColumnSchema col : this.columns) {
            if (col != null) {
                validColumns.add(col);
            }
        }
        return Collections.unmodifiableList(validColumns);
    }


}