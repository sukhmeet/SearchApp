package tgt.service.search.providers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractSearchProvider implements SearchProvider {
	protected List<File> dataFiles = new LinkedList<>();
	@Value("${app.search.docdir}")
	private String dataDir;

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	public List<File> getDataFiles() {
		return dataFiles;
	}

	public void setDataFiles(List<File> dataFiles) {
		this.dataFiles = dataFiles;
	}
	public void loadFileList() {
		File dataDirFile = new File(dataDir);
		if(dataDirFile.isDirectory()){
			for(File file : dataDirFile.listFiles()){
				if(file.isFile() ){
					String fileName = file.getName();
					if( fileName.lastIndexOf(".") > 0 && fileName.substring(fileName.lastIndexOf(".")+1).equals("txt") ) {
						dataFiles.add(file);
						System.out.println("DBP loadFileList added file:" +  file.getName());
					}
				}
			}
		}
		
	}
}
