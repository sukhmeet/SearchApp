package tgt.service.search.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tgt.service.search.SearchResult;



@Component
public class SimpleSearchProvider extends AbstractSearchProvider {
	private static ExecutorService pool;
		
	@Value("${thread.pool.size}")
	private int poolsize;
	
	public int getPoolsize() {
		return poolsize;
	}

	public void setPoolsize(int poolsize) {
		this.poolsize = poolsize;
	}

	@Autowired
	Comparator<SearchResult> searchResultComparator;
	
	public List<SimpleSearchWorker> getWorkers(String term) {
		List<SimpleSearchWorker> workers = new LinkedList<>();
		for(File file: getDataFiles()) {
			SimpleSearchWorker worker;
			try {
				worker = new SimpleSearchWorker(file.getName(), new FileReader(file), term);
				System.out.println("DBP created worker for :" + file.getName());
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
		//System.out.println("DBP SimpleSearchProvider.search called for phrase :" + term + "  no of dataFiles " + dataFiles.size());
		List<Future<SearchResult>> futures = new LinkedList<>();
		
		for(SimpleSearchWorker worker: getWorkers(searchPhrase)){
			
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
			
			}
		}
		
		Collections.sort(results, searchResultComparator);
		return results;
	}
	
	@PostConstruct
    public void init() {
		
    	pool = Executors.newFixedThreadPool(poolsize) ;
		// load file list and keep it in memory.
		// TODO file data can also be loaded in memory. 
		loadFileList();
    }
	
	@PreDestroy
	public void shutdownHook(){
		pool.shutdown();
	}
	
	
	
	
	

}
