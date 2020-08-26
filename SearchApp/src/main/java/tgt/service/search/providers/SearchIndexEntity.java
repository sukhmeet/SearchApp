package tgt.service.search.providers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

public class SearchIndexEntity {
	  @Id
	  private String term;
	  Map<String, Integer> countInFiles = new HashMap();
	  
	public Map<String, Integer> getCountInFiles() {
		return countInFiles;
	}

	
	public String getTerm() {
		return term;
	}

	public SearchIndexEntity(String term) {
		this.term = term;
	}   
}
