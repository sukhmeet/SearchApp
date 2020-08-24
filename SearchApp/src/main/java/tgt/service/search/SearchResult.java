package tgt.service.search;

public class SearchResult {
	String fileName;
	int numOccurence;
	public String getFileName() {
		return fileName;
	}
	public SearchResult(String fileName, int numOccurence) {
		
		this.fileName = fileName;
		this.numOccurence = numOccurence;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getNumOccurence() {
		return numOccurence;
	}
	public void setNumOccurence(int numOccurence) {
		this.numOccurence = numOccurence;
	}
}
