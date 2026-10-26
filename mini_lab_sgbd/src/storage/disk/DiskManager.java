package storage.disk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import javax.imageio.IIOException;

public class DiskManager {
	private static final int PAGE_SIZE = 4096;
	private final File file;
	private FileChannel channel;
	
	public DiskManager(String dbFilePath) throws IOException {
		this.file = new File(dbFilePath);
		channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.READ);
	}
	
	public ByteBuffer readPage(int pageId) throws FileNotFoundException, IOException {
		ByteBuffer buffer = ByteBuffer.allocate(PAGE_SIZE);
		
		try {
			// --- AJUSTE GEMINI: DEFESA PARA ARQUIVO NOVO / PARADOXO DO BOOT ---
			// Se o canal acabou de ser criado (0 bytes) e pedirem a página 0,
			// retornamos o buffer limpo de 4KB para o CatalogManager estruturar a RAM.
			if (channel.size() == 0 && pageId == 0) {
				buffer.position(0);
				return buffer;
			}
			// ------------------------------------------------------------------

			long offset = (long) pageId * PAGE_SIZE;
			
			if (offset > channel.size()) {
				throw new IOException("A página " + pageId + " não existe");
			}
			
			channel.position(offset);
			while (buffer.hasRemaining()) {
				int bytesRead = channel.read(buffer);
				if (bytesRead == -1) {
					throw new IOException("Fim de arquivo inesperado ao ler a página: " + pageId);
				}
			}
			
			buffer.flip();
			return buffer;
		} catch (IOException e) {
			System.err.println(e.getMessage());
	        System.err.println("ERRO CRÍTICO I/O: Falha ao ler a página " + pageId);
	        throw e; // Lança para o Buffer Pool tratar a falha
	    }
	}
	
	public boolean writePage(int pageId, byte[] pageData) {
		
		try {
			long offset = (long) pageId * PAGE_SIZE;
			
			ByteBuffer buffer = ByteBuffer.wrap(pageData);
			channel.position(offset);
			
			while(buffer.hasRemaining()) {
				channel.write(buffer);
			}
			
			channel.force(true);
			System.out.println("persistido com sucesso");

			return true;
			
			
			
		} catch (IOException e) {
			System.err.println("ERRO CRÍTICO DE I/O ao escrever a página " + pageId + ": " + e.getMessage());
            e.printStackTrace();
            
            return false; // Avisa a camada de cima que a escrita falhou!
		}
		
	}
	
	public void close() {
		try {
			if(channel != null || channel.isOpen()) {
				channel.close();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public long getChannelSize() throws IOException {
		return channel.size();
	}
}
