package tgt.service.search.providers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchProviderFactory {

	private final Map<String, SearchProvider> searchProvider = new HashMap<>();
	@Autowired
	private SimpleSearchProvider ssp;
	@Autowired
	private RegExSearchProvider regsp;
	@Autowired
	private IndexedSearchProvider ixsp;
	
	@PostConstruct
    public void init() {
		
		searchProvider.put("simple", ssp);
		searchProvider.put("regex", regsp);
		searchProvider.put("indexed", ixsp);
    }

    public SearchProvider getSearchProvider(String searchMethod) {
    	// TODO throw exception
        return searchProvider.get(searchMethod);
    }
	
}
