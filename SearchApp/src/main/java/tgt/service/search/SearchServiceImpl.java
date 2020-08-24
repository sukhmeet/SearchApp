package tgt.service.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tgt.service.search.providers.SearchProviderFactory;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired 
	private SearchProviderFactory searchFactory;
	
	@Override
	public List<SearchResult> search(String searchMethod, String searchPhrase) {
		
		return searchFactory.getSearchProvider(searchMethod).search(searchPhrase);
	}
}
