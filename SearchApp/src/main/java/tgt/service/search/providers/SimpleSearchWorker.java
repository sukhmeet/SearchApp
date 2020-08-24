package tgt.service.search.providers;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.concurrent.Callable;

import tgt.service.search.SearchResult;

public class SimpleSearchWorker implements Callable<SearchResult> {

	private Reader fileReader;
	private String fileName;
	private String term; 
	
	
	public SimpleSearchWorker(String fileName, Reader fileReader, String term){
		this.fileReader = fileReader;
		this.fileName = fileName;
		this.term = term;
	}
	
	@Override
	public SearchResult call() throws Exception {
		BufferedReader reader = null;
		if ( term == null || term.equals("")) {
			return new SearchResult(fileName, 0);
		}
		int count = 0;
		try{
			reader = new BufferedReader(fileReader);
			String line = null;
			while((line=reader.readLine())!=null){
				int index = -1;
				do{
					index = line.indexOf(term, index+1);
					if(index!=-1) count++;
				} while(index!=-1);
				
				//System.out.println(file.getName() + ": line " + line + " count=" + count );
			}
		} finally{
			if ( reader != null ) { 
				reader.close();
			}
		}
		return new SearchResult(fileName, count);
	}
}
