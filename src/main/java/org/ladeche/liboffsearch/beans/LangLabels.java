package org.ladeche.liboffsearch.beans;

import java.awt.Image;

public class LangLabels {
	
	private String code;
	private Image flag;
	private String btnDirectory;
	private String btnSearch;
	private String tblHeader;
	
	public LangLabels (String code) {
		this.code=code;
	}
	
	@Override
	public String toString() {
		return   this.code+":"
				+this.btnDirectory + ":"
				+this.btnSearch + ":" 
				+this.tblHeader;

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Image getFlag() {
		return flag;
	}

	public void setFlag(Image flag) {
		this.flag = flag;
	}
	
	public String getBtnDirectory() {
		return btnDirectory;
	}

	public void setBtnDirectory(String btnDirectory) {
		this.btnDirectory = btnDirectory;
	}

	public String getBtnSearch() {
		return btnSearch;
	}

	public void setBtnSearch(String btnSearch) {
		this.btnSearch = btnSearch;
	}

	public String getTblHeader() {
		return tblHeader;
	}

	public void setTblHeader(String tblHeader) {
		this.tblHeader = tblHeader;
	}

}
