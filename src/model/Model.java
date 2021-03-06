package model;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.omg.CORBA.OMGVMCID;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class Model {
	static byte[] ANSI_ASICC = {(byte) 0xA1, (byte) 0xC, 0x30};
	static byte[] UNICODE = {(byte) 0xE4, (byte) 0xB8, (byte) 0xA5};
	static byte[] UTF_8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
	static byte[] UTF_16_LE= {(byte) 0xFF,(byte) 0xFE};
	static byte[] UTF_16_BE = {(byte) 0xFE,(byte) 0xFF};
	static byte[][] BOMS = {UNICODE, UTF_8, UTF_16_LE, UTF_16_BE};
	
	private ConversionStateChangeListener conversionStateChangeListener;
	private ReadFileActionListener readFileActionListener;
	private ConvertorData convertorData;
	private String inPath;
	private String outPath;
	private List<String> docLines;
	private Thread readThread;
	private boolean readRun;
	
	private String docOpenPath;
	private String docSavePath;
	private String docCharsetName;
	private byte[] docFileBom;
	private String lineSeparator;
	private boolean docModifyed;
	
	public Model() {
		docLines = new ArrayList<>();
		initNewDoc();
	}
	
	public void start(Class<?> converter, String inPath, String outPath, JFrame parent) throws Exception{
		if(converter == null ) {
			throw new IllegalArgumentException("未设置转化器");
		}
		if(inPath == null || inPath.isEmpty()) {
			throw new IllegalArgumentException("未设置输入路径");
		}
		if(outPath == null || outPath.isEmpty()) {
			throw new IllegalArgumentException("未设置输出路径");
		}
		
		File saveDir = new File(outPath);
		if(!saveDir.exists()) {
			saveDir.mkdirs();
		}
		if(!saveDir.isDirectory()) {
			throw new IllegalArgumentException("请设置有效的输出路径");
		}
		
		this.convertorData = getConverterData(converter);
		this.inPath = inPath;
		this.outPath = outPath;
		
		if(convertorData.getShowView() != null) {
			if((boolean) convertorData.getShowView().invoke(convertorData.getConverter(), parent)) {
				throw new Error("取消转化");
			}
		}
		conversion(scanFiles(inPath), saveDir);
	}

	public List<File> scanFiles(String inPath) {
		File inFile = new File(inPath);
		if(!inFile.exists()) {
			throw new IllegalArgumentException("请设置有效的输出路径");
		}
		File[] inList = inFile.listFiles();
		List<File> fileList = new ArrayList<>();
		for (File f : inList) {
			if (f.isFile() && f.exists()) {
				if(convertorData.getFilefilter() != null) {
					try {
						if (((boolean)convertorData.getFilefilter().invoke(convertorData.getConverter(), f))) {
							fileList.add(f);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("执行文件过滤失败："+e.getMessage());
					}
				} else {
					fileList.add(f);
				}
			}
		}

		return fileList;

	}
	
	/**
	 * 获取转化器数据
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static ConvertorData getConverterData(Class clazz) throws Exception {
		ConvertorData convertorData = new ConvertorData();
		
		Method[] methods = clazz.getDeclaredMethods();
		for(Method m : methods) {
			Class<?>[] parameterTypes = m.getParameterTypes();
			if(m.getName().equals("showView")
					&& parameterTypes.length == 1
					&& parameterTypes[0] == JFrame.class
					&& (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
				convertorData.setShowView(m);
			} else if(m.getName().equals("filefilter")
					&& parameterTypes.length == 1
					&& parameterTypes[0] == File.class
					&& (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
				convertorData.setFilefilter(m);
			} else if(m.getName().equals("conversion")
					&& parameterTypes.length == 3
					&& parameterTypes[0] == File.class
					&& parameterTypes[1] == File.class
					&& parameterTypes[2] == String.class
					&& (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
				convertorData.setConversion(m);
			}
		}
		
		if(convertorData.getConversion() != null) {
			convertorData.setConverter(clazz.newInstance());
		}
		
		if(convertorData.getConverter() == null) {
			throw new Exception("无效的转化器");
		}
		
		return convertorData;
	}

	/**
	 * 调用文本转化器转化
	 * @param fileList
	 * @param saveDir
	 * @param handle
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void conversion(List<File> fileList, File saveDir) throws IllegalArgumentException, Exception {
		if (fileList.size() == 0) {
			throw new IllegalArgumentException("没有需要转换的文件！");
		}
		if(conversionStateChangeListener != null) {
			conversionStateChangeListener.onStart(fileList.size());
		}
		
		int count = fileList.size();
		int successCount = 0;
		
		for (int i = 0; i < fileList.size(); i++) {
			try {
				if((boolean) convertorData.getConversion().invoke(convertorData.getConverter(),
						fileList.get(i), saveDir,
						getFileCharset(fileList.get(i).getAbsolutePath()))) {
					++successCount;
					if(conversionStateChangeListener != null) {
						conversionStateChangeListener.onProgress(successCount, count);
					}
				}
			} catch (Exception | Error e) {
				if(conversionStateChangeListener != null) {
					conversionStateChangeListener.onError(e);
				}
			}
		}
		if(conversionStateChangeListener != null) {
			conversionStateChangeListener.onDone(successCount, count);
		}
		
	}
	
	/**
	 * 获取文件编码
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String getFileCharset(String filePath) throws Exception {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		
		detector.add(new ParsingDetector(false));
		detector.add(ASCIIDetector.getInstance());
		detector.add(UnicodeDetector.getInstance());
		Charset charset = null;
		File file = new File(filePath);
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(filePath));
			charset = detector.detectCodepage(is, 8);
		} catch (Exception e) {
			throw e;
		} finally {
			if(is != null) {
				is.close();
			}
		}
 
		String charsetName = "GBK";
		if (charset != null) {
			if (charset.name().equals("US-ASCII")) {
				charsetName = "ISO_8859_1";
			} else if (charset.name().startsWith("UTF")) {
				charsetName = charset.name();// 例如:UTF-8,UTF-16BE.
			}
		}
		return charsetName;
	}
	
	private byte[] getBOM(String filePath) throws IOException {
		
		File file = new File(filePath);
		InputStream is = null;
		try {
			byte[] buf = new byte[5];
			is = new BufferedInputStream(new FileInputStream(filePath));
			is.read(buf);
			int len = buf.length;
			if(len >= 3) {
				for(int i=0; i<BOMS.length; ++i) {
					int blen = 0;
					for(int j=0; j<BOMS[i].length; ++j) {
						if(BOMS[i][j] == buf[j]) {
							++blen;
						} else {
							break;
						}
					}
					if(blen == BOMS[i].length) {
						return BOMS[i];
					}
				}
			}
		}catch(Exception e) {
			
		} finally {
			if(is != null) {
				is.close();
			}
		}
		return null;
	}
	
	public static boolean fileIsExists(File file) {
		if(file == null || !file.exists() || !file.isFile()) {
			return false;
		}
		return true;
	}

	public void readFile(File file, ReadFileActionListener listener){
		if(fileIsExists(file)) {
			Exception e = new IllegalArgumentException("无效的文件");
			if(readFileActionListener != null) {
				readFileActionListener.onError(e);
			}
		}
		
		if(readThread != null) {
			readRun = false;
			try {
				readThread.join();
			} catch (InterruptedException e) {}
		}
		
		readFileActionListener = listener;
		docOpenPath = file.getAbsolutePath();
		docSavePath = docOpenPath;
		
		readRun = true;
		readThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				BufferedReader reader = null;
				try {
					docCharsetName = getFileCharset(file.getAbsolutePath());
					docFileBom = getBOM(file.getAbsolutePath());
			        if(readFileActionListener != null) {
						readFileActionListener.onOpen(file.getName(), docCharsetName);
					}
			        
			        FileInputStream inputStream = new FileInputStream(file);
			        if(docFileBom != null) {
			        	inputStream.read(docFileBom);
			        }
					reader = new BufferedReader(
							new InputStreamReader(inputStream, docCharsetName));
					int lineCount = 0;
					int words = 0;
					String line;
					while(readRun && (line = reader.readLine()) != null) {
						//docLines.add(line);
						words += line.length();
						++lineCount;
						if(readFileActionListener != null) {
							readFileActionListener.onNewLine(line, lineSeparator, lineCount, words);
						}
					}
				} catch (Exception e) {
					if(readFileActionListener != null) {
						readFileActionListener.onError(e);
					}
				} finally {
					readThread = null;
					readRun = false;
					try {
						if(reader != null) {
							reader.close();
						}
					} catch (IOException e) {
						if(readFileActionListener != null) {
							readFileActionListener.onError(e);
						}
					}
					
					if(readFileActionListener != null) {
						readFileActionListener.onDeon(docLines);
					}
				}
			}
		});
		readThread.start();
	}
	
	public void saveDoc(File outFile) throws Exception {
		//另存为时传入outFile，否则传入null
		if(outFile == null) {
			outFile = new File(docSavePath);
		}
				
		if(!fileIsExists(outFile)) {
			outFile.createNewFile();
			if(!outFile.exists()) {
				throw new Exception("保存失败");
			}
		}
		BufferedWriter writer = null;
		try {
			FileOutputStream fout = new FileOutputStream(outFile);
			if(docFileBom != null) {
				fout.write(docFileBom); //写入编码标识文件头
			}
			writer = new BufferedWriter(new OutputStreamWriter(fout,  docCharsetName));
			for(String line : docLines) {
				writer.write(line);
				writer.write(lineSeparator);
			}
			docModifyed = false;
		} catch (Exception e) {
			throw new IOException("保存失败");
		} finally {
			if(writer != null) {
				writer.close();
			}
		}
		
	}
	
	/**
	 * 暂时用这个方法，实现docline在用上面的保存方法
	 * @param text
	 * @param outFile
	 * @throws Exception
	 */
	public void saveDoc(String text, File outFile) throws Exception {
		//另存为时传入outFile，否则传入null
		if(outFile == null) {
			outFile = new File(docSavePath);
		}
		
		if(!outFile.exists()) {
			outFile.createNewFile();
			if(!outFile.exists()) {
				throw new Exception("保存失败");
			}
		}
		BufferedWriter writer = null;
		try {
			if(docCharsetName == null) {
				docCharsetName = "utf-8";
				docFileBom = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF}; //utf-8
			}
			
			FileOutputStream fout = new FileOutputStream(outFile);
			if(docFileBom != null) {
				fout.write(docFileBom); //写入编码标识文件头
			}
			
			writer = new BufferedWriter(new OutputStreamWriter(fout,  docCharsetName));
			writer.write(text);
			
			docModifyed = false;
		} catch (Exception e) {
			throw new IOException("保存失败");
		} finally {
			if(writer != null) {
				writer.close();
			}
		}
		
	}
	
	public void initNewDoc() {
		lineSeparator = System.lineSeparator();
		docCharsetName = "utf-8";
		docFileBom = null;
		docModifyed = false;
	}

	public ConversionStateChangeListener getConversionStateChangeListener() {
		return conversionStateChangeListener;
	}

	public void setConversionStateChangeListener(ConversionStateChangeListener conversionStateChangeListener) {
		this.conversionStateChangeListener = conversionStateChangeListener;
	}

	public ReadFileActionListener getReadFileActionListener() {
		return readFileActionListener;
	}

	public void setReadFileActionListener(ReadFileActionListener readFileActionListener) {
		this.readFileActionListener = readFileActionListener;
	}
	
	public void insertOrUpdateLine(int index, String line) {
		if(index < docLines.size()-1) {
			docLines.set(index, line);
		} else {
			docLines.add(line);
		}
	}
	
	public void removeLine(int index) {
		if(index >=0 && index < docLines.size()) {
			docLines.remove(index);
		}
	}
	
	public void clearAll() {
		docLines.clear();
	}
	
	public int getDocLineCount() {
		return docLines.size();
	}

	public String getDocSavePath() {
		return docSavePath;
	}

	public void setDocSavePath(String docSavePath) {
		this.docSavePath = docSavePath;
	}

	public String getDocOpenPath() {
		return docOpenPath;
	}

	public String getDocCharsetName() {
		return docCharsetName;
	}

	public boolean isDocModifyed() {
		return docModifyed;
	}

	public void setDocModifyed(boolean docModifyed) {
		this.docModifyed = docModifyed;
	}

	
}
