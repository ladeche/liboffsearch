package org.ladeche.liboffsearch.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author L.Dechambe
 *
 */
public class FileExtension {

    static final Logger logger = LoggerFactory.getLogger(FileExtension.class);
	private String fileExt;
	private ExecUnit fileSearchTool;
	private ExecUnit fileOpenTool;
	private Boolean active;
	private String description;

	public FileExtension (String fileExt) {
		this.fileExt = fileExt;
	}

	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getFileExt() {
		return fileExt;
	}
	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public ExecUnit getFileSearchTool() {
		return fileSearchTool;
	}
	public void setFileSearchTool(ExecUnit fileSearchTool) {
		this.fileSearchTool = fileSearchTool;
	}

	public ExecUnit getFileOpenTool() {
		return fileOpenTool;
	}
	public void setFileOpenTool(ExecUnit fileOpenTool) {
		this.fileOpenTool = fileOpenTool;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return this.fileExt + ":"
				+this.fileSearchTool + ":"
				+this.fileOpenTool + ":"
				+this.description + ":"
				+this.active;

	}

	public ArrayList<FileFound> search (String sourceDir, String keyWord) throws IOException {

		logger.debug("Search arguments : dir="+sourceDir+" kw="+keyWord+" type="+this.getFileExt());
		
		ArrayList<FileFound> filesFound = new ArrayList<FileFound>();

		String arguments = " \"" + sourceDir + "\"" +
				" " + this.fileExt +
				" \"" + keyWord + "\"";

		BufferedReader reader = this.fileSearchTool.execute(arguments);

		String line = "";
		FileFound filefound; 

		// loop on result stream to fill result array  
		while ((line = reader.readLine()) != null) {
			logger.debug(line.toString());
			filefound = new FileFound(line,this,sourceDir);
			filesFound.add(filefound);
		}

		return filesFound;
	}

	public void open(String fileFullPath) throws IOException {

		String arguments = "\"" + fileFullPath + "\"";

		this.fileOpenTool.execute(arguments);
	}

}
