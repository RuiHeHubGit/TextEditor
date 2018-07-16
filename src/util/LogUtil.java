package util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class LogUtil {
	public static boolean debug = true;
	public static String infoSavePath;
	public static String debugSavePath;
	public static String errorSavePath;
	public static String exceptionSavePath = "exception.log";
	private static String datePattern = "yyyy-MM-dd hh:mm:ss";
	
	public static void info(String text) {
		
	}
	
	public static void debug(String text) {
		
	}
	
	public static void error(String text) {
		
	}

	public static void exception(Throwable e) {
		if(e == null) {
			return;
		}
		PrintWriter writer = null;
		try {
			String data = getDate();
			if(debug) {
				System.err.println(data);
				e.printStackTrace();
			}
			writer = new PrintWriter(new FileWriter(exceptionSavePath, true));
			writer.println(data);
			e.printStackTrace(writer);
			writer.println();
		} catch (Exception e1) {
			System.err.println("LogUtilError");
			e1.printStackTrace();
		} finally {
			if(writer != null) {
				writer.close();
			}
		}
	}

	private static String getDate() {
		return ZonedDateTime.now().format(DateTimeFormatter.ofPattern(datePattern));
	}
}
