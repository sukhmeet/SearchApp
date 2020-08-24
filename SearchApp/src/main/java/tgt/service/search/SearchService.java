package tgt.service.search;

import java.util.List;



public interface SearchService {
	
	  public abstract List<SearchResult> search(String searchMethod, String searchPhrase);
	  
}
