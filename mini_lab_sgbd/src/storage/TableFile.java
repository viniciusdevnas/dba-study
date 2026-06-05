package storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import catalog.TableSchema;
import storage.disk.DiskManager;

public class TableFile {
	private static final int PAGE_SIZE = 4096;
	private final DiskManager diskManager;
	private final int tableId;
	private final TableSchema schema;
	
	private final Map<Integer, Integer> freeSpaceMap;
	
	private int numPages;
	
	public TableFile(DiskManager diskManager, int tableId, TableSchema schema) throws Exception {
		this.diskManager = diskManager;
		this.tableId = tableId;
		this.freeSpaceMap = new ConcurrentHashMap<Integer, Integer>();
		this.schema = schema;
		this.numPages = 0;
		
		initializeFreeSpace();

	}
	
	private void initializeFreeSpace() throws Exception {
		long fileSize = diskManager.getChannelSize();
		this.numPages = (int)(fileSize / PAGE_SIZE);
		System.out.println("Carregando TableFile [" + tableId + "]. Páginas encontradas no disco: " + numPages);
		
		for(int i = 0; i < numPages; i ++) {
			
			ByteBuffer buffer = diskManager.readPage(i);
			
			Page page = new Page(buffer.array());
			
			int freeSpace = page.getFreeSpaceAmount();
			
			freeSpaceMap.put(i, freeSpace);
		}
		System.out.println("Warm-up concluído. Free Space Map carregado com sucesso.");
	}
	
	
	private synchronized int findPageWithSpace(int requiredSpace) {
		
		for(Map.Entry<Integer, Integer> entry: freeSpaceMap.entrySet()) {
			int pageId = entry.getKey();
			int availableSpace = entry.getValue();
			
			if(availableSpace >= requiredSpace) 
				return pageId;
		}
		return -1;
	}
	
	public synchronized RID insertRecord(Tuple tuple) throws FileNotFoundException, IOException {
		byte[] recordData = Serializer.serialize(tuple, this.schema);
		int requiredSpace = recordData.length;
		
		int targetPageId = findPageWithSpace(requiredSpace);
		
		Page page;
		
		if(targetPageId != -1) {
			System.out.println("inserindo na página existente: " + targetPageId);
			
			ByteBuffer buffer = diskManager.readPage(targetPageId);
			
			page = new Page(buffer.array());
			
		}else {
			targetPageId = numPages;
			numPages++;
			
			System.out.println("Nenhuma página com espaço. Criando nova página: " + targetPageId);
			page = new Page();
		}
		
		int slotId = page.insertRecord(recordData);
		if(slotId == -1) {
			throw new IOException("Falha crítica: Página relatou espaço mas rejeitou a inserção.");
		}
		
		freeSpaceMap.put(targetPageId, page.getFreeSpaceAmount());
		
		boolean sucess = diskManager.writePage(targetPageId, page.getPageByte());
		
		if(!sucess) {
			throw new IOException("Falha crítica de I/O: DiskManager não conseguiu persistir a página " + targetPageId);
		}
		
		return new RID(targetPageId, slotId);
	}
	
	
	public synchronized Tuple getRecordAsTuple(RID rid) throws Exception {
		int targetPageId = rid.getPageId();
		
		ByteBuffer pageBuffer = diskManager.readPage(targetPageId);
		Page page = new Page(pageBuffer.array());
		byte[] recordByte = page.getRecord(rid.getSlotId());
		
		if(recordByte == null)
			return null;
		
		return Serializer.deserialize(recordByte, this.schema);
	}
}
