package tgt.service.search.providers;

import java.util.List;

import tgt.service.search.SearchResult;



public interface SearchProvider {
	public abstract List<SearchResult> search(String searchPhrase);
}
