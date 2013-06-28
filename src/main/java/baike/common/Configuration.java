package baike.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	public static String get(String name, String defaultVal) {
		if(properties == null) loadProperties();
		String val = properties.getProperty(name);
		return val != null ? val : defaultVal;
	}
	
	private static final String CONFIG_FILE_NAME = ".baike-classifier.conf";
	private static Properties properties;
	private static void loadProperties() {
		properties = new Properties();
		
		String userHome = System.getProperty("user.home");
		String configFile = userHome + "/" + CONFIG_FILE_NAME;
		try {
			properties.load(new FileInputStream(configFile));
		} catch (IOException e) {
			throw new RuntimeException("Can't load the config file at " + configFile);
		}
	}
}
