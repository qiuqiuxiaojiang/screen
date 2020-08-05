package com.capitalbio.common.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

public class PropertyUtils {
	public static Properties prop = null;
	public static String getProperty(String property) {
		if (prop == null) {
			loadProperties("config.properties");
		}
		return prop.getProperty(property);
	}
	public static String getProperty(String property, String defvalue) {
		if (prop == null) {
			loadProperties("config.properties");
		}
		return prop.getProperty(property, defvalue);
	}
	private static void loadProperties(String fileName) {
		ClassPathResource cr = new ClassPathResource(fileName);
		prop = new Properties();
        try {
        	prop.load(cr.getInputStream());
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}
	
	public static Properties getProperties() {
		if (prop == null) {
			loadProperties("config.properties");
		}
		return prop;
	}
}
