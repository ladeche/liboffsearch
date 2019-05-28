package org.ladeche.liboffsearch.beans;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileFound {
	
    static final Logger logger = LoggerFactory.getLogger(FileFound.class);
	private String fileFullPath;
	private String fileRelativePath;
	private String fileName;
	private FileExtension fileExt;
	private Integer id;
	
	public FileFound (String fileRelativePath, FileExtension fileExt, String sourcedir) {
		this.fileRelativePath = fileRelativePath;
		this.fileFullPath = sourcedir+"/"+fileRelativePath.substring(2);
		this.fileName = fileRelativePath.substring(fileRelativePath.lastIndexOf(".")+1);
		this.fileExt = fileExt;
	}
	
	@Override
	public String toString() {
		return   this.id+":"
				+this.fileFullPath + ":"
				+this.fileRelativePath + ":" 
				+this.fileName;

	}
	

	public void open() throws IOException {
   	   	this.fileExt.open(this.fileFullPath);
    }

	public FileExtension getFileExt() {
		return fileExt;
	}

	public void setFileExt(FileExtension fileExt) {
		this.fileExt = fileExt;
	}

	
	public String getFileFullPath() {
		return fileFullPath;
	}

	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}

	public String getFileRelativePath() {
		return fileRelativePath;
	}

	public void setFileRelativePath(String fileRelativePath) {
		this.fileRelativePath = fileRelativePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
    
}
