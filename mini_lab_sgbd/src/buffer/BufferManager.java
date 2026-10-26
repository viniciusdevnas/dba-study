package buffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import storage.disk.DiskManager;
import storage.*;

public class BufferManager {

	private final Frame[] pool;
	private final Map<Integer, Integer> pageTable;
	private final DiskManager diskManager;
	private final int poolSize;
	
	// Declarando ring buffer para scans sequenciais
	private final Frame[] ringBuffer;
	private final int RING_BUFFER_SIZE = 32;
	private int ringPointer = 0;
	
	public BufferManager(int poolSize, DiskManager diskManager) {
		this.poolSize = poolSize;
		this.diskManager = diskManager;
		this.pageTable = new HashMap<Integer, Integer>();
		this.pool = new Frame[poolSize];
		
		// inicializando o pool
		for(int i = 0; i < poolSize; i++) {
			this.pool[i] = new Frame(i);
		}
		
		// inicializando o ringBuffer
		this.ringBuffer = new Frame[RING_BUFFER_SIZE];
		for(int i = 0; i < RING_BUFFER_SIZE; i++) {
			this.ringBuffer[i] = new Frame(i);
		}
	}
	
	public synchronized Page fetchPage(int pageId) throws Exception {
		if(pageTable.containsKey(pageId)) {
			int frameId = pageTable.get(pageId);
			pool[frameId].pin();
			return pool[frameId].getPage();
		}
		
		int targetFrameId = getAvailableFrameOrEvict();
		Frame frame = pool[targetFrameId];
		
		if(frame.isDirty() && frame.getPageId() != null) {
			diskManager.writePage(frame.getPageId(), frame.getPage().getPageByte());
			pageTable.remove(frame.getPageId());
		}
		
		ByteBuffer buffer = diskManager.readPage(pageId);
		Page pageData = new Page(buffer.array());
		frame.loadPage(pageId, pageData);
		
		pageTable.put(pageId, targetFrameId);
		
		return frame.getPage();
		
	}
	public synchronized Page fetchSequentialPage(int pageId) throws FileNotFoundException, IOException {
		Frame currentFrame = ringBuffer[ringPointer];
		// Se a gaveta do anel está suja, limpa ela no disco
        if (currentFrame.isDirty() && currentFrame.getPageId() != null) {
            diskManager.writePage(currentFrame.getPageId(), currentFrame.getPage().getPageByte());
        }
     // Carrega o dado do Scan Sequencial nesse slot temporário
        ByteBuffer buffer = diskManager.readPage(pageId);
        Page pageData = new Page(buffer.array());
        currentFrame.loadPage(pageId, pageData);

        // Anda com o ponteiro do relógio do anel: 0, 1, 2... 31, 0, 1...
        ringPointer = (ringPointer + 1) % RING_BUFFER_SIZE;

        return currentFrame.getPage();
	}
	
	private int getAvailableFrameOrEvict() throws Exception {
        // Rascunho simples: Procura o primeiro frame com pinCount == 0
        for (int i = 0; i < poolSize; i++) {
            if (pool[i].getPinCount() == 0) {
                return i;
            }
        }
        throw new Exception("DBMS Error: Buffer Pool totalmente travado (Out of memory frames)!");
    }
	
	
	public synchronized void unpinPage(int pageId, boolean isDirty) {
	    // 1. Procura na PageTable qual frame está segurando essa página
	    if (pageTable.containsKey(pageId)) {
	        int frameId = pageTable.get(pageId);
	        Frame frame = pool[frameId];
	        
	        // 2. Se quem usou diz que mudou os dados, marca como sujo
	        if (isDirty) {
	            frame.setDirty(true);
	        }
	        
	        // 3. Decrementa o contador de alfinetes com segurança
	        frame.unpin(); 
	    }
	}
}
