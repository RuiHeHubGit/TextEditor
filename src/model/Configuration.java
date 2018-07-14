package model;
/**
 * 
 * @author HeRui
 *
 */
public class Configuration {
	private static Configuration configuration;
	private static Configuration preConfiguration;
	
	private boolean lineWrap;
	private boolean wrapStyleWord;
	private boolean showLineNumber;
	private boolean showStateBar;
	
	public static Configuration loadConfig() {
		if(configuration == null) {
			configuration = new Configuration();
			configuration.showStateBar = true;
		}
		if(!readConfig()) {
			
		}
		return configuration;
	}

	public static boolean save() {
		return true;
	}
	
	private static boolean readConfig() {
		return false;
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(Configuration configuration) {
		Configuration.configuration = configuration;
	}

	public static Configuration getPreConfiguration() {
		return preConfiguration;
	}

	public static void setPreConfiguration(Configuration preConfiguration) {
		Configuration.preConfiguration = preConfiguration;
	}

	public boolean isLineWrap() {
		return lineWrap;
	}

	public void setLineWrap(boolean lineWrap) {
		this.lineWrap = lineWrap;
	}

	public boolean isWrapStyleWord() {
		return wrapStyleWord;
	}

	public void setWrapStyleWord(boolean wrapStyleWord) {
		this.wrapStyleWord = wrapStyleWord;
	}

	public boolean isShowLineNumber() {
		return showLineNumber;
	}

	public void setShowLineNumber(boolean showLineNumber) {
		this.showLineNumber = showLineNumber;
	}

	public boolean isShowStateBar() {
		return showStateBar;
	}

	public void setShowStateBar(boolean showStateBar) {
		this.showStateBar = showStateBar;
	}
}
