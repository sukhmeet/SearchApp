package tgt.service.search.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.stereotype.Component;

import tgt.service.search.SearchResult;



@Component
public class IndexedSearchProvider extends AbstractSearchProvider {
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
    private MappingMongoConverter mappingMongoConverter;

    
	@Autowired
	TermIndexRepository termIndexRepository;

	@Autowired
	Comparator<SearchResult> searchResultComparator;
	
	Set<Character> ignoredChars = new HashSet<Character>(Arrays.asList(' ', ',' , '?', '.', '!', '"', '\'', '’'));
	
	public List<SimpleSearchWorker> getWorkers(Set<String> fileNamesToSearch, String term) {
		List<SimpleSearchWorker> workers = new LinkedList<>();
		for(String fileName: fileNamesToSearch) {
			SimpleSearchWorker worker;
			try {
				worker = new SimpleSearchWorker(fileName, new FileReader(dataDir + File.separator + fileName), term);
				//System.out.println("DBP created worker for :" + fileName);
				workers.add(worker);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			
		}
		return workers;
	}
	
	@Override
	public List<SearchResult> search(String searchPhrase){
		String firstTerm = null;
		List<SearchResult> results = new LinkedList<>();
		StringBuilder sb = new StringBuilder(); 
		  
        sb.append("[");
        for (Character ch : ignoredChars) { 
            sb.append(ch); 
        }
        sb.append("]");
        
		String[] terms = searchPhrase.split(sb.toString());
		
		//System.out.println("terms after split " + Arrays.toString(terms));
		
		if ( terms.length == 1) { // there is a single term in the searchPhrase.
			
			List<SearchIndexEntity> searchResults = termIndexRepository.findByTermRegex(".*" + searchPhrase + ".*");
			
			Map<String,Integer> counts = new HashMap();
			for(SearchIndexEntity searchIndexEntity : searchResults) {
				//System.out.println("DBP IndexedSearchProvider.search " + searchIndexEntity.getTerm() + " " + searchIndexEntity.getCountInFiles());
				
				for(String fileName : searchIndexEntity.countInFiles.keySet()) {
					counts.put(fileName , counts.getOrDefault(fileName, 0) + searchIndexEntity.countInFiles.get(fileName));
				}
			}
			
			for(String fileName : counts.keySet()) {
				SearchResult result = new SearchResult(fileName, counts.get(fileName));	
				results.add(result);
			}
		} else if ( terms.length > 1 ) { // its a phrase with multiple terms. search in file at the offsets.

			List<Set<String>> scanList = new LinkedList<>(); // this will contain all possible filenames for all the terms in the searchPhrase
			Set<String> fileNamesToSearch;// this will contain the filenames to perform actual search on.
			for( String term : terms) {
			
				List<SearchIndexEntity> searchResultsFromDB = termIndexRepository.findByTermRegex(".*" + term + ".*");
				
				Set<String> fileNames = new HashSet<>();
				
				for(SearchIndexEntity searchIndexEntity : searchResultsFromDB) {
					for(String fileName : searchIndexEntity.countInFiles.keySet()) {
						if (searchIndexEntity.countInFiles.get(fileName) > 0) {
							fileNames.add(fileName);
						}
					}
					
				}
				scanList.add(fileNames);
			}
			// take an intersection of all the common files between all the terms
			
			fileNamesToSearch = scanList.get(0);
			
			for (int i = 1; i < scanList.size(); i++) {
				fileNamesToSearch.retainAll(scanList.get(i));
			} 
			
			// for debug only
			/*for( String fileName : fileNamesToSearch) {
				System.out.println("fileNamesToSearch : " + fileName);
			}*/
			
			// perform the search by calling SimpleSearchWorker.
			// TODO - enhancement : perform searches in file starting at a give index
			
			List<Future<SearchResult>> futures = new LinkedList<>();
			
			for(SimpleSearchWorker worker: getWorkers(fileNamesToSearch, searchPhrase)){
				
				Future<SearchResult> future = pool.submit(worker);
				futures.add(future);
			}
			
			for (Future<SearchResult> future : futures) {	
				try {
					SearchResult searchResult = null;
					searchResult = future.get();
					if(searchResult.getNumOccurence() > 0){
						results.add(searchResult);
					}
					
				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}
			
		}
		Collections.sort(results, searchResultComparator);
		return results;
		
		
	}
	public void replaceMongoKeyContainingDot() {
	      mappingMongoConverter.setMapKeyDotReplacement("#");
	}
	private void createIndex() throws IOException {
		
		//System.out.println("DBP IndexedSearchProvider createIndex called");
		
		termIndexRepository.deleteAll();
		
		Map<String, SearchIndexEntity> termsMap = new HashMap<>();
		
		
		for( File f : getDataFiles() ) {
			
			BufferedReader reader = null;
			
			try {
				reader = new BufferedReader(new FileReader(f));
				String line = null;
				long offSetInFile = 0;
				while((line=reader.readLine())!=null){
					int startOffset = -1;
				   	
					for(int i=0; i < line.length(); i++) {
						if ( ! ignoredChars.contains(line.charAt(i))) {
					
							if (startOffset == -1 ) {
								startOffset = i;
							}
						} else {
							
							if (startOffset != -1) {
								
								String term = line.substring(startOffset, i);
								if (term.length() > 1 ) {
									long wordOffset = offSetInFile + startOffset;
									
									termsMap.computeIfAbsent(term, z -> new SearchIndexEntity(term)).countInFiles.merge(f.getName(), 1, (prev, cur) -> prev + cur);
									
								}
								startOffset = -1;
							}
						}
					}
					// handle last word of the line
					if ( startOffset != -1 &&  ! ignoredChars.contains(line.charAt(line.length() - 1)))  {
						
						String term = line.substring(startOffset, line.length());
						if (term.length() > 1 ) {
							long wordOffset = offSetInFile + startOffset;
							termsMap.computeIfAbsent(term, z -> new SearchIndexEntity(term)).countInFiles.merge(f.getName(), 1, (prev, cur) -> prev + cur);
						}
					}
					offSetInFile += line.length() + 1;
					
				}
				
				
				
			} finally{
				if ( reader != null ) { 
					reader.close();
				}
			}
			
		}
		//Adding all the terms to the DB
		//System.out.println("DBP found following terms in all of the files " + termsMap.size());
		/*for (String term : termsMap.keySet()) {
			System.out.println("saving term to Index : " + term);
			
		}*/
		for( SearchIndexEntity searchIndexEntity : termsMap.values()) {
			termIndexRepository.save(searchIndexEntity);
		}
		
		System.out.println("DBP added all terms to mongodb. count : " + termsMap.size());
		
	}
	@Override 
	public void loadFileList() {
		
		try {
			super.loadFileList();
			replaceMongoKeyContainingDot();
			createIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
