package tgt;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import tgt.service.search.SearchResult;
import tgt.service.search.providers.SimpleSearchProvider;
import tgt.service.search.providers.SimpleSearchWorker;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SearchApplication.class)


@SpringBootTest
class SearchApplicationTests {
	
	class TestData {
		String fileName;
		String content;
		public TestData(String fileName, String content) {
			this.fileName = fileName;
			this.content = content;
		}
		
	}
	
	@Autowired
	SimpleSearchProvider simpleSearchProvider;
	
	List<TestData> testDataList;
	
	List<SimpleSearchWorker> workers;
	@BeforeEach
	public void init() {
		
		if ( testDataList == null) {
			testDataList = new ArrayList<>();
			testDataList.add(new TestData("file1.txt", "This is a test file. the test is about the word counter program. \nit prints the count of the word or phrase in each of the files."));
			testDataList.add(new TestData("file2.txt", "test results are sorted according to the frquency of the occuruence of each of the words in a test file"));
			testDataList.add(new TestData("file3.txt", "to write a good word counter program you must write efficient code. each file should be parsed properly. the test files should contain multiple searchPhrases. the test"));
		}
		
		
		
	}
	@Test
	public void testSimpleSearchSingleWord() {
		String searchPhrase = "count";
		
		System.out.println("DBP testSimpleSearchSingleWord searchPhrase : " + searchPhrase);
		
		SimpleSearchProvider simpleSearchProviderSpy = Mockito.spy(simpleSearchProvider);
		Mockito.doNothing().when(simpleSearchProviderSpy).loadFileList();
		 
		workers = new LinkedList<>();
		for (TestData testData : testDataList) {
			workers.add(new SimpleSearchWorker(testData.fileName, new StringReader(testData.content) , searchPhrase));
		}
		
		Mockito.doReturn(workers).when(simpleSearchProviderSpy).getWorkers(searchPhrase);
		
		List<SearchResult> results = simpleSearchProviderSpy.search(searchPhrase);
		// For debug only
		printTestResults(results); 
		assertEquals(2, results.size());
		assertEquals("file1.txt" , results.get(0).getFileName()); 
		assertEquals(2, results.get(0).getNumOccurence()); 
		assertEquals("file3.txt" , results.get(1).getFileName()); 
		assertEquals(1, results.get(1).getNumOccurence()); 
		
	}
	@Test
	public void testSimpleSearchPhrase() {
		String searchPhrase = "the test";
		
		System.out.println("DBP testSimpleSearchPhrase searchPhrase : " + searchPhrase);
		
		SimpleSearchProvider simpleSearchProviderSpy = Mockito.spy(simpleSearchProvider);
		Mockito.doNothing().when(simpleSearchProviderSpy).loadFileList();
		 
		
		workers = new LinkedList<>();
		for (TestData testData : testDataList) {
			workers.add(new SimpleSearchWorker(testData.fileName, new StringReader(testData.content) , searchPhrase));
		}
		
		Mockito.doReturn(workers).when(simpleSearchProviderSpy).getWorkers(searchPhrase);
		
		List<SearchResult> results = simpleSearchProviderSpy.search(searchPhrase);
		
		// For debug only
		printTestResults(results); 
				
		
		assertEquals(2, results.size());
		assertEquals("file3.txt" , results.get(0).getFileName()); 
		assertEquals(2, results.get(0).getNumOccurence()); 
		assertEquals("file1.txt" , results.get(1).getFileName()); 
		assertEquals(1, results.get(1).getNumOccurence()); 
		
	}
	@Test
	public void testSimpleSearchEmptyPhrase() {
		String searchPhrase = "";
		System.out.println("DBP testSimpleSearchEmptyPhrase searchPhrase : " + searchPhrase);
		SimpleSearchProvider simpleSearchProviderSpy = Mockito.spy(simpleSearchProvider);
		Mockito.doNothing().when(simpleSearchProviderSpy).loadFileList();
		 
		
		workers = new LinkedList<>();
		for (TestData testData : testDataList) {
			workers.add(new SimpleSearchWorker(testData.fileName, new StringReader(testData.content) , searchPhrase));
		}
		
		Mockito.doReturn(workers).when(simpleSearchProviderSpy).getWorkers(searchPhrase);

		List<SearchResult> results = simpleSearchProviderSpy.search(searchPhrase);
		
		// For debug only
		printTestResults(results); 
		
		
		assertEquals(0, results.size());
	}
	void printTestResults(List<SearchResult> results) {
		for(SearchResult sr : results) {
			System.out.println("DBP searchResult " + sr.getFileName() + " : " + sr.getNumOccurence());
		}
	}
}
	
	

