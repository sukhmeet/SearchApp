package tgt.service.search.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tgt.service.search.SearchResult;



@Component
public class RegExSearchProvider extends SimpleSearchProvider implements SearchProvider {

	@Autowired
	Comparator<SearchResult> searchResultComparator;
	
	@Override
	public List<SearchWorker> getWorkers(String searchPhrase) {
		List<SearchWorker> workers = new LinkedList<>();
		for(File file: getDataFiles()) {
			RegExSearchWorker worker;
			try {
				worker = new RegExSearchWorker(file.getName(), new FileReader(file), searchPhrase);
				//System.out.println("DBP created worker for :" + file.getName());
				workers.add(worker);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return workers;
	}
	@Override
	public List<SearchResult> search(String searchPhrase){
		//System.out.println("DBP SimpleSearchProvider.search called for phrase :" + searchPhrase + "  no of dataFiles " + dataFiles.size());
		List<Future<SearchResult>> futures = new LinkedList<>();
		
		for(SearchWorker worker: getWorkers(searchPhrase)){
			
			Future<SearchResult> future = pool.submit(worker);
			futures.add(future);
		}
		
		
		List<SearchResult> results = new ArrayList<>();
		for (Future<SearchResult> future : futures) {	
			try {
				SearchResult searchResult = null;
				searchResult = future.get();
				
				//System.out.println("DBP searchResult :" + searchResult.getFileName() + " : " + searchResult.getNumOccurence());
				
				if(searchResult.getNumOccurence() > 0){
					results.add(searchResult);
				}
				
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		
		Collections.sort(results, searchResultComparator);
		return results;
	}

}
