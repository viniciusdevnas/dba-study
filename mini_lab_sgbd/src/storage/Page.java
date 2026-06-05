package storage;

import java.nio.ByteBuffer;

public class Page {
    
    public static final int PAGE_SIZE = 4096; // Seu tamanho escolhido de 4KB
    private static final int HEADER_NUM_SLOTS_OFFSET = 0; 
    private static final int HEADER_FREE_SPACE_OFFSET = 4; 
    private static final int SLOT_SIZE = 8; 
    
    private final byte[] data;
    private final ByteBuffer buffer;
    
    // VARIÁVEIS DE CONTROLE DA RAM
    private int slotCount;
    private int freeSpacePointer; // Guarda o OFFSET de onde começa o dado de trás pra frente (ex: 4096)
    
    /**
     * Construtor para Páginas NOVAS (Zeradinhas)
     */
    public Page() {
        this.data = new byte[PAGE_SIZE];
        this.buffer = ByteBuffer.wrap(data);
        
        // Estado inicial de uma Slotted Page vazia
        this.slotCount = 0;
        this.freeSpacePointer = PAGE_SIZE; // Próximo dado entra no final (byte 4096)
        
        // GRAVA ESSE ESTADO INICIAL NO CABEÇALHO FÍSICO (Array de bytes)
        setNumSlots(0);
        setFreeSpacePointer(PAGE_SIZE);
    }
    
    /**
     * Construtor para reconstruir Páginas vindas do DISCO
     */
    public Page(byte[] bufferArray) {
        this.data = bufferArray;
        this.buffer = ByteBuffer.wrap(data);
        
        // RECONSTRUÇÃO ESSENCIAL: Resgata o passado do banco lendo os primeiros bytes
        this.slotCount = buffer.getInt(HEADER_NUM_SLOTS_OFFSET);
        this.freeSpacePointer = buffer.getInt(HEADER_FREE_SPACE_OFFSET);
        
        // Proteção contra arquivos corrompidos ou completamente zerados
        if (this.freeSpacePointer == 0 && this.slotCount == 0) {
            this.freeSpacePointer = PAGE_SIZE;
            setFreeSpacePointer(PAGE_SIZE);
        }
    }
    
    private void setNumSlots(int count) {
        this.slotCount = count; // Atualiza a RAM
        buffer.putInt(HEADER_NUM_SLOTS_OFFSET, count); // Atualiza os bytes
    }
    
    private int getNumSlots() {
        return buffer.getInt(HEADER_NUM_SLOTS_OFFSET);
    }

    private void setFreeSpacePointer(int pointer) {
        this.freeSpacePointer = pointer; // Atualiza a RAM
        buffer.putInt(HEADER_FREE_SPACE_OFFSET, pointer); // Atualiza os bytes
    }

    /**
     * Retorna a QUANTIDADE REAL de bytes livres restantes na página
     */
    public int getFreeSpaceAmount() {
        int tamanhoDoHeader = 8; 
        int tamanhoDosSlots = this.slotCount * SLOT_SIZE; 
        
        int fimDoDiretorioDeSlots = tamanhoDoHeader + tamanhoDosSlots;
        int inicioDosDadosSurgindo = this.freeSpacePointer;
        
        return inicioDosDadosSurgindo - fimDoDiretorioDeSlots; 
    }
    
    public int insertRecord(byte[] recordBytes) {
        int numSlot = getNumSlots();
        
        // O próximo slot do diretório vai ocupar este offset na frente da página
        int nextSlotOffset = 8 + (numSlot * SLOT_SIZE);
        
        // O espaço necessário é o tamanho do registro + o tamanho do slot que vai apontar para ele
        int spaceRequired = SLOT_SIZE + recordBytes.length;
        
        // Verifica se a quantidade de espaço livre suporta o novo registro
        if (getFreeSpaceAmount() < spaceRequired) {
            return -1; // Sem espaço nesta página! (Retornar -1 avisa o TableFile para dar Cache Miss)
        }
        
        // Calcula o novo ponteiro recuando o tamanho do dado do final da página
        int newFreeSpacePointer = this.freeSpacePointer - recordBytes.length;
        setFreeSpacePointer(newFreeSpacePointer);
        
        // Copia os bytes reais do registro lá para o final do array físico
        System.arraycopy(recordBytes, 0, data, newFreeSpacePointer, recordBytes.length);
        
        // Escreve o Slot no diretório da frente [offset, length]
        buffer.putInt(nextSlotOffset, newFreeSpacePointer); 
        buffer.putInt(nextSlotOffset + 4, recordBytes.length); 
        
        // Atualiza a quantidade de slots ativos
        setNumSlots(numSlot + 1);
        
        return numSlot; // Retorna o slotId correto (0, 1, 2...) para compor o RID
    }
    
    public byte[] getRecord(int slotNumber) {
        int numSlots = getNumSlots();
        if (slotNumber < 0 || slotNumber >= numSlots) {
            return null; 
        }
        
        int indexSlotOffset = 8 + (slotNumber * SLOT_SIZE);
        
        int recordOffset = buffer.getInt(indexSlotOffset);
        int recordLength = buffer.getInt(indexSlotOffset + 4);
        
        if (recordOffset == 0 || recordLength == 0)
            return null; // Registro deletado
        
        byte[] recordBytes = new byte[recordLength];
        System.arraycopy(data, recordOffset, recordBytes, 0, recordLength);
        
        return recordBytes;
    }
    
    public byte[] getPageByte() {
        return this.data;
    }
    
    public void deleteRecord(int slotNumber) {
        int numSlots = getNumSlots();
        if (slotNumber < 0 || slotNumber >= numSlots)
            return;
        
        int slotIndexOffset = 8 + (slotNumber * SLOT_SIZE);
        
        // No modelo Slotted Page, a deleção lógica apenas zera o slot
        buffer.putInt(slotIndexOffset, 0);
        buffer.putInt(slotIndexOffset + 4, 0);
    }
    
    public void compact() {
        int numSlots = getNumSlots();
        byte[] temporaryData = new byte[PAGE_SIZE];
        ByteBuffer temporaryBuffer = ByteBuffer.wrap(temporaryData);
        int activeSlotCount = 0;
         
        int currentSpaceFree = PAGE_SIZE;
        
        for (int i = 0; i < numSlots; i++) {
            int slotIndexOffset = 8 + (i * SLOT_SIZE);
            
            int oldOffset = buffer.getInt(slotIndexOffset);
            int length = buffer.getInt(slotIndexOffset + 4);
            
            if (oldOffset > 0 && length > 0) {
                currentSpaceFree -= length;
            
                System.arraycopy(data, oldOffset, temporaryData, currentSpaceFree, length);
                int newSlotIndexOffset = 8 + (activeSlotCount * SLOT_SIZE);

                temporaryBuffer.putInt(newSlotIndexOffset, currentSpaceFree);
                temporaryBuffer.putInt(newSlotIndexOffset + 4, length);

                activeSlotCount++;
            }   
        }
        
        temporaryBuffer.putInt(HEADER_FREE_SPACE_OFFSET, currentSpaceFree);
        temporaryBuffer.putInt(HEADER_NUM_SLOTS_OFFSET, activeSlotCount);
        
        System.arraycopy(temporaryData, 0, this.data, 0, PAGE_SIZE);
        
        // Atualiza as variáveis locais da RAM pós-compactação
        this.slotCount = activeSlotCount;
        this.freeSpacePointer = currentSpaceFree;
    }
}