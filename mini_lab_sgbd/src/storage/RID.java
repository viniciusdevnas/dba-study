package storage;

public class RID {

	private int pageId;
	private int slotId;
	
	public RID(int pageId, int slotId) {
		this.pageId = pageId;
		this.slotId = slotId;
	}
	
	
	@Override
	public String toString() {
		return "RID [pageId=" + pageId + ", slotId=" + slotId + "]";
	}


	public int getPageId() {
		return pageId;
	}
	public int getSlotId() {
		return slotId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}
	
}
