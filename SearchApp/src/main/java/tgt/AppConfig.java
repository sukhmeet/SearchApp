package tgt;

import java.util.Comparator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import tgt.service.search.SearchResult;

// TODO - remove configuration if not needed

@Configuration
public class AppConfig {
	
	@Bean
	@Scope("singleton")
	public Comparator<SearchResult> getSearchResultComparator() {
		Comparator<SearchResult> searchResultcomparator = new Comparator<SearchResult>() {
			@Override
			public int compare(SearchResult o1, SearchResult o2) {
				return o2.getNumOccurence() - o1.getNumOccurence();
			}
		};
		return searchResultcomparator;
	}
	
}
