package tgt.service.search.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tgt.service.search.SearchResult;

public class RegExSearchWorker extends SimpleSearchWorker {

	public RegExSearchWorker(String fileName, Reader fileReader, String searchPhrase) {
		super(fileName, fileReader, searchPhrase);
	
	}
		
	@Override
	public SearchResult call() throws Exception {
		
		//System.out.println(" RegExSearchWorker.called for " + fileName);
		
		BufferedReader reader = null;
		if ( searchPhrase == null || searchPhrase.equals("")) {
			return new SearchResult(fileName, 0);
		}
		int count = 0;
		Pattern r = Pattern.compile(searchPhrase);
		
		try{
			reader = new BufferedReader(fileReader);
			String line = null;
			while( (line=reader.readLine())!=null){
				
				
				Matcher m = r.matcher(line);
				while (m.find()) {
				    count++;
				}
				
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
