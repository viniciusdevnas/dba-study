package buffer;

import storage.Page;

public class Frame {

	private final int frameId;
	private Integer pageId;
	private Page page;
	private int pinCount;
	private boolean isDirty;
	
	public Frame(int frameId) {
		this.frameId = frameId;
		this.pageId = null;
		this.page = null;
		this.pinCount = 0;
		this.isDirty = false;
	}
	public synchronized void pin() {
        this.pinCount++;
    }

    public synchronized void unpin() {
        if (this.pinCount > 0) {
            this.pinCount--;
        } else {
            throw new IllegalStateException("Erro no SGBD: Tentativa de dar unpin em uma página que já está com pinCount zero! Frame: " + frameId);
        }
    }
    
    public synchronized void loadPage(Integer pageId, Page page) {
    	this.pageId = pageId;
    	this.page = page;
    	this.pinCount = 1;
    	this.isDirty = false;
    }
    
    public synchronized void clear() {
    	this.pageId = null;
    	this.page = null;
    	this.pinCount = 0;
    	this.isDirty = false;
    }
	public int getFrameId() {
		return frameId;
	}
	public Integer getPageId() {
		return pageId;
	}
	public Page getPage() {
		return page;
	}
	public int getPinCount() {
		return pinCount;
	}
	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
    
	
}
