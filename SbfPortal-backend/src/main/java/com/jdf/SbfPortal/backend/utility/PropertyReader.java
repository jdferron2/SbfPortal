package com.jdf.SbfPortal.backend.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyReader {
	private static Properties prop = new Properties();
	private static String path = "C:\\Users\\jferron\\Documents\\RankXML\\";
	private static InputStream input = null;
	static {
		resetProperties();
	}
	public PropertyReader(){		
	}

	public static String getProperty(String name) {
		return prop.getProperty(name);
	}

	public static void resetProperties() {
		try {
			if (prop != null) prop.clear();
			input = new FileInputStream(path+"ff_portal.properties");
			prop.load(input);
			input.close();
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
	
		}
	}
}
