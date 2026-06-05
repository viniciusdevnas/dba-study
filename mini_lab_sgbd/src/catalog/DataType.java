package catalog;

/**
 * Sistema de Tipos Primitivos do SGBD.
 * Controla os tamanhos e as regras de conversão de dados no baixo nível do disco.
 */
public enum DataType {
    // === TIPOS FIXOS ===
    INT(4),          // Integer padrão (4 bytes)
    BOOLEAN(1),      // Byte puro (1 byte: 1=true, 0=false)
    BIGINT(8),       // Long padrão (8 bytes)
    TIMESTAMP(8),    // Identico ao BIGINT no disco (8 bytes guardando Unix Epoch em milissegundos)
    UUID(16),        // Identificador Único Universal (16 bytes fixos no disco - 2 Longs colados)

    // === TIPOS VARIÁVEIS ===
    VARCHAR(-1);     // Tamanho dinâmico (necessita de prefixo de tamanho de 4 bytes)

    private final int fixedSize;

    DataType(int fixedSize) {
        this.fixedSize = fixedSize;
    }

    /**
     * Retorna o espaço exato em bytes que o tipo ocupa.
     * @return tamanho em bytes ou -1 se for variável.
     */
    public int getFixedSize() {
        return fixedSize;
    }

    /**
     * Identifica se o tipo é dinâmico e varia de registro para registro.
     */
    public boolean isVariable() {
        return this.fixedSize == -1;
    }
}
