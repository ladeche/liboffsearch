package org.ladeche.liboffsearch.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecUnit {

    static final Logger logger = LoggerFactory.getLogger(ExecUnit.class);
	private String name;
	private String executableResource;
	
	public ExecUnit (String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return   this.name+":"
				+this.executableResource;
	}
	
	public ExecUnit (String name, String executableResource) {
		this.name = name;
		this.executableResource = executableResource;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExecutableResource() {
		return executableResource;
	}

	public void setExecutableResource(String executableResource) {
		this.executableResource = executableResource;
	}
	
	public BufferedReader execute(String arguments) throws IOException {
    	// Build command string
    	String command = this.executableResource + " " + arguments; 
		logger.debug("Execute "+command);

    	BufferedReader reader = null;
        // run search shell command
        Runtime runtime = Runtime.getRuntime();
        String[] args = { "/bin/bash", "-c", command };
        final Process process = runtime.exec(args);
        try {
        	// manage process result stream
            process.waitFor();
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return reader;
	}
	
	
}
