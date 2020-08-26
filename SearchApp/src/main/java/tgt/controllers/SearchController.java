package tgt.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tgt.service.search.SearchResult;
import tgt.service.search.SearchService;

@RestController
@RequestMapping("/")
public class SearchController {
	
	@Autowired 
	private SearchService searchService;
	
	@GetMapping("/search") 
	List<SearchResult> search(@RequestParam String searchMethod, @RequestParam String searchPhrase) {
		
		List<SearchResult> results = searchService.search(searchMethod, searchPhrase);
		
		// For Debug only
		/*if ( results != null) {
			for(SearchResult searchResult : results) {
				System.out.println("Search result : " + searchResult.getFileName() + " " + searchResult.getNumOccurence());
			}
		}*/
		System.out.println(searchMethod + " : " +searchPhrase + ".");
		
		return results;
		
	}
}
