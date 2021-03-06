package model;

import java.util.List;

public interface ReadFileActionListener {
	void onOpen(String fileName, String CharsetName);
	
	void onNewLine(String line, String br, int lineCount, int words);
	
	void onDeon(List<String> docLines);
	
	void onError(Throwable e);
}
