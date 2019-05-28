package org.ladeche.liboffsearch.utils;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class Converter {
	
	public static String[][] ArrayListToArray(ArrayList<String> arrayList) {
		
		String[][] array = new String[arrayList.size()][1];
		for(int i=0; i<arrayList.size(); i++){
            array[i][0] = arrayList.get(i);
		}
		return array;
	}

	
	public static DefaultTableModel ArrayListToTableModel(ArrayList<String> arrayList, String[] columnName, int fileNameOption, String rootPath) {
		
		DefaultTableModel tableModel = new DefaultTableModel(columnName, 1); 
		tableModel.setRowCount(0);
		Object[] file = new Object[1];

		
		switch (fileNameOption) {
        // Write Full Path
		case Options.FULLPATH:  
			for(int i=0; i<arrayList.size(); i++){
				file[0] = rootPath.concat(arrayList.get(i).substring(1));
	        	System.out.println(file[0].toString());
	        	tableModel.addRow(file);
			}
			break;
	        // Write Relative Path
		case Options.RELATIVEPATH:
			for(int i=0; i<arrayList.size(); i++){
				file[0] = arrayList.get(i);
	        	System.out.println(file[0].toString());
	        	tableModel.addRow(file);
			}
			break;
	        // Write Only File Name
		case Options.NOPATH:
			for(int i=0; i<arrayList.size(); i++){
				file[0] = arrayList.get(i).substring(arrayList.get(i).lastIndexOf("/")+1);
	        	System.out.println(file[0].toString());
	        	tableModel.addRow(file);
			}
			break;
		}
		return tableModel;
		
	}
	
	public static String buildFileName (Object cellContent, int fileNameOption, String rootPath) {
		
		String fileName = cellContent.toString(); 
		
		switch (fileNameOption) {
        	// Full Path = nothing to do
		case Options.FULLPATH:  
			break;
	        // Relative Path = concat rootPath with filename (without leading dot)
		case Options.RELATIVEPATH:
			fileName=rootPath.concat(fileName.substring(1));
			break;
	        // No Path = unusable
		case Options.NOPATH:
			fileName="";
			break;
		}
		return "\""+fileName+"\"";
		
	}
}
