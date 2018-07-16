package util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class LogUtil {
	public final static int ERROR = 1;
	public final static int INFO = 2;
	public final static int DEBUG = 3;
	public static boolean debug = true;
	public static int leave = DEBUG;
	public static String infoSavePath = "info.log";
	public static String debugSavePath = "debug.log";
	public static String errorSavePath = "error.log";
	public static String exceptionSavePath = "exception.log";
	private static String datePattern = "yyyy-MM-dd hh:mm:ss";
	
	public static void debug(String text) {
		if(leave < 3) {
			return;
		}
		writeToFile(getDate() + " [debug]", text, debugSavePath);
	}
	
	public static void info(String text) {
		if(leave < 2) {
			return;
		}
		writeToFile(getDate() + " [info]", text, infoSavePath);
	}
	
	public static void error(String text) {
		if(leave < 1) {
			return;
		}
		writeToFile(getDate() + " [error]", text, errorSavePath);
	}
	
	private static void writeToFile(String firstLine, String text, String savePath) {
		PrintWriter writer = null;
		try {
			if(debug) {
				System.err.println(firstLine);
				System.err.println(text);
			}
			writer = new PrintWriter(new FileWriter(savePath, true));
			writer.println(firstLine);
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

	public static void exception(Throwable e) {
		if(e == null) {
			return;
		}
		PrintWriter writer = null;
		try {
			String firstLine = getDate() + " [Exception]";
			if(debug) {
				System.err.println(firstLine);
				e.printStackTrace();
			}
			writer = new PrintWriter(new FileWriter(exceptionSavePath, true));
			writer.println(firstLine);
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
