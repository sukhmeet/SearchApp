package tgt.service.search.providers;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.concurrent.Callable;

import tgt.service.search.SearchResult;

public class SimpleSearchWorker extends SearchWorker {


	
	public SimpleSearchWorker(String fileName, Reader fileReader, String searchPhrase) {
		
		super(fileName, fileReader, searchPhrase);
		
	}

	@Override
	public SearchResult call() throws Exception {
		
		//System.out.println(" SimpleSearchWorker.called for " + fileName);
		
		BufferedReader reader = null;
		if ( searchPhrase == null || searchPhrase.equals("")) {
			return new SearchResult(fileName, 0);
		}
		int count = 0;
		
		try{
			reader = new BufferedReader(fileReader);
			String line = null;
			while( (line=reader.readLine())!=null){
				
					
				int index = -1;
				do {
					index = line.indexOf(searchPhrase, index+1);
					if (index != -1) count++;
				} while(index != -1);
				
			}
			
		} catch (Exception e) {
			System.out.println("got exception for " + fileName);
			System.out.println(e.toString());
		}
		finally{
		
			if ( reader != null ) { 
				reader.close();
			}
		}
		
		return new SearchResult(fileName, count);
	}
}
