package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import util.LogUtil;

/**
 * 
 * @author HeRui
 *
 */
public class Configuration implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Configuration configuration;
	
	private boolean lineWrap;
	private boolean wrapStyleWord;
	private boolean showLineNumber;
	private boolean showStateBar;
	
	private String fileChooserPath;
	
	public static Configuration loadConfig() {
		ObjectInputStream ois = null;
		try {
			File file = new File("editConf.dat");
			if(!file.exists() || file.isDirectory()) {
				file.createNewFile();
			}
			ois = new ObjectInputStream(new FileInputStream(file));
			configuration = (Configuration) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			LogUtil.exception(e);
		} finally {
			if(ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					LogUtil.exception(e);
				}
			}
		}
		
		if(configuration == null) {
			configuration = new Configuration();
			configuration.showStateBar = true;
			save();
		}
		
		return configuration;
	}
	
	public static void save() {
		if(configuration== null) {
			configuration = loadConfig();
		}
		
		ObjectOutputStream oos = null;
		try {
			File file = new File("editConf.dat");
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(configuration);
		} catch (IOException e) {
			LogUtil.exception(e);
		} finally {
			if(oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					LogUtil.exception(e);
				}
			}
		}
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public boolean isLineWrap() {
		return lineWrap;
	}

	public void setLineWrap(boolean lineWrap) {
		this.lineWrap = lineWrap;
		save();
	}

	public boolean isWrapStyleWord() {
		return wrapStyleWord;
	}

	public void setWrapStyleWord(boolean wrapStyleWord) {
		this.wrapStyleWord = wrapStyleWord;
		save();
	}

	public boolean isShowLineNumber() {
		return showLineNumber;
	}

	public void setShowLineNumber(boolean showLineNumber) {
		this.showLineNumber = showLineNumber;
		save();
	}

	public boolean isShowStateBar() {
		return showStateBar;
	}

	public void setShowStateBar(boolean showStateBar) {
		this.showStateBar = showStateBar;
		save();
	}

	public String getFileChooserPath() {
		return fileChooserPath;
	}

	public void setFileChooserPath(String fileChooserPath) {
		this.fileChooserPath = fileChooserPath;
		save();
	}
	
}
