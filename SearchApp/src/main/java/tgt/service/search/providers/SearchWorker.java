package tgt.service.search.providers;

import java.io.Reader;
import java.util.concurrent.Callable;

import tgt.service.search.SearchResult;

public abstract class SearchWorker  implements Callable<SearchResult> {

	protected Reader fileReader;
	protected String fileName;
	protected String searchPhrase; 
	
	
	public SearchWorker(String fileName, Reader fileReader, String searchPhrase){
		this.fileReader = fileReader;
		this.fileName = fileName;
		this.searchPhrase = searchPhrase;
	}
	
}
