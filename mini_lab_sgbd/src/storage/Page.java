package storage;

import java.nio.ByteBuffer;

public class Page {
	
	public static final int PAGE_SIZE = 4096;
	private static final int HEADER_NUM_SLOTS_OFFSET = 0; // 4 bytes para int
    private static final int HEADER_FREE_SPACE_OFFSET = 4; // 4 bytes para int
    private static final int SLOT_SIZE = 8; // Cada slot tem 8 bytes: [offset (4B), length (4B)]
    
    private final byte[] data;
    private final ByteBuffer buffer;
    
    public Page() {
    	this.data = new byte[PAGE_SIZE];
    	this.buffer = ByteBuffer.wrap(data);
    }
    
    private void setNumSlots(int count) {
    	buffer.putInt(HEADER_NUM_SLOTS_OFFSET, count);
    }
    private int getNumSlots() {
    	return buffer.getInt(HEADER_NUM_SLOTS_OFFSET);
    }
    
    public int getFreeSpacePointer() {
        return buffer.getInt(HEADER_FREE_SPACE_OFFSET);
    }

    private void setFreeSpacePointer(int pointer) {
        buffer.putInt(HEADER_FREE_SPACE_OFFSET, pointer);
    }
    
    
    public int insertRecord (byte[] recordBytes) {
    	int numSlot = getNumSlots();
    	int freeSpacePointer = getFreeSpacePointer();
    	
    	int nextSlotOffset = HEADER_FREE_SPACE_OFFSET + 4 + (numSlot * SLOT_SIZE);
    	
    	int spaceRequired = SLOT_SIZE + recordBytes.length;
    	
    	if((freeSpacePointer - nextSlotOffset) < spaceRequired)
    		return 1;
    	
    	int newFreeSpacePointer = freeSpacePointer - recordBytes.length;
    	setFreeSpacePointer(newFreeSpacePointer);
    	
    	System.arraycopy(recordBytes, 0, data, newFreeSpacePointer, recordBytes.length);
    	
    	// 3. Escreve o Slot no diretório [offset, length]
        int slotIndexOffset = nextSlotOffset;
        buffer.putInt(slotIndexOffset, newFreeSpacePointer); // offset do dado
        buffer.putInt(slotIndexOffset + 4, recordBytes.length); // tamanho do dado
        
        setNumSlots(numSlot + 1);
        
        return 0;
    	
    }
    
    public byte[] getRecord(int slotNumber) {
    	int numSlots = getNumSlots();
        if (slotNumber < 0 || slotNumber >= numSlots) {
            return null; // Slot não existe
        }
        
        int indexSlotOffset = (HEADER_FREE_SPACE_OFFSET + 4) + (slotNumber * SLOT_SIZE);
        
        int recordOffset = buffer.getInt(indexSlotOffset);
        int recordLength = buffer.getInt(indexSlotOffset + 4);
        
        if(recordOffset == 0)
        	return null;
        
        byte[] recordBytes = new byte[recordLength];
        
        
        System.arraycopy(data, recordOffset, recordBytes, 0, recordLength);
        
        return recordBytes;
    }
    
    public byte[] getPageByte() {
    	return this.data;
    }
    
    public void deleteRecord(int slotNumber) {
    	int numSlots = getNumSlots();
    	if(slotNumber < 0 || slotNumber >= numSlots)
    		return;
    	
    	int slotIndexOffset = (HEADER_FREE_SPACE_OFFSET + 4) + (numSlots * SLOT_SIZE);
    	
    	buffer.putInt(slotIndexOffset, 0);
    	buffer.putInt(slotIndexOffset + 4, 0);
    }
    
    public void compact() {
    	int numSlots = getNumSlots();
    	byte[] temporaryData = new byte[PAGE_SIZE];
    	ByteBuffer temporaryBuffer = buffer.wrap(temporaryData);
    	int slotCount = 0;
    	 
    	
    	int currentSpaceFree = PAGE_SIZE;
    	
    	for(int i = 0; i < numSlots; i++) {
    		
    		// isso daqui é um indice para acessar e manipular o array de bytes
    		int slotIndexOffset = (HEADER_FREE_SPACE_OFFSET + 4) + (i * SLOT_SIZE);
    		
    		//entrega o endereço no array de bytes
    		int oldOffset = buffer.getInt(slotIndexOffset);
    		int length = buffer.getInt(slotIndexOffset + 4);
    		
    		if(oldOffset > 0 && length > 0) {
    			currentSpaceFree -= length;
    		
    			System.arraycopy(data, oldOffset, temporaryData, currentSpaceFree, length);
        		int newSlotIndexOffset = (HEADER_FREE_SPACE_OFFSET + 4) + (slotCount * SLOT_SIZE);

        		// guarda o inicio do array
    			temporaryBuffer.putInt(newSlotIndexOffset, currentSpaceFree);
    			// guarda o tamanho do array
    			temporaryBuffer.putInt(newSlotIndexOffset + 4, length);

    			slotCount++;
    			 			    			
    		}   
    	}
    	// Atualiza o ponteiro de espaço livre no cabeçalho da página temporária
	    temporaryBuffer.putInt(HEADER_FREE_SPACE_OFFSET, currentSpaceFree);
	    temporaryBuffer.putInt(HEADER_NUM_SLOTS_OFFSET, slotCount);
	    
	    // Substitui o array de dados antigo pelo compactado
	    System.arraycopy(temporaryData, 0, this.data, 0, PAGE_SIZE);
    }
}
