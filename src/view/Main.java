package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import annotation.ActionHandle;
import model.Configuration;
import model.ConversionStateChangeListener;
import model.Model;
import model.ReadFileActionListener;
import util.ActionUtil;
import util.LogUtil;
import util.MenuTree;

public class Main extends JFrame implements ActionListener, CaretListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int SHOW_MAX_LINES = 1000;
	private HashMap<String, Method> actionHandleMap;
	private TextAreaMenu txaDisplay;
	private JToolBar statusBar;
	private JLabel[] statusBarLabels;
	private Configuration configuration;
	private Font currentFont;
	private int lastLength;
	private Model model;

	public Main(String[] args) {
		init();
		initMainWindwos();
		initThem();
		initMainPanel();
		initMenu();
		actionHandleMap = new HashMap<String, Method>();
		ActionUtil.scanRegisterActionHandle(this.getClass(), actionHandleMap);
		setVisible(true);
		checkArgs(args);
	}

	private void checkArgs(String[] args) {
		if(args.length > 0) {
			openDoc(new File(args[0]));
		}
	}

	private void initMainWindwos() {
		setTitle("记事本");
		setIconImage(new ImageIcon(Main.class.getResource("notepad.png")).getImage());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) (dim.getWidth() * 0.6), (int) (dim.getHeight() * 0.6));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				preExit();
			}
		});
	}

	/**
	 * 初始化
	 */
	private void init() {
		model = new Model();
		configuration = Configuration.loadConfig();
	}

	private void initMainPanel() {
		setLayout(new BorderLayout()); 
		txaDisplay = new TextAreaMenu();
		txaDisplay.addCaretListener(this);
		JScrollPane scroll = new JScrollPane(txaDisplay);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scroll);
		
		currentFont = txaDisplay.getFont();
		
		statusBarLabels = new JLabel[6];
		String[] texts = {"第0行", "第0列", "光标:0", "行数:1", "字数:0", "UTF-8"};
		statusBar = new JToolBar();
		statusBar.add(new JPanel(new SpringLayout()));
		for(int i=0; i< statusBarLabels.length; ++i) {
			statusBar.addSeparator();
			statusBar.add(statusBarLabels[i] = new JLabel(texts[i]));
			statusBarLabels[i].setBorder(new EmptyBorder(0, 5, 0, 5));
		}
		statusBar.addSeparator();
		add(statusBar, BorderLayout.SOUTH);
		
		txaDisplay.setLineWrap(configuration.isLineWrap());
		txaDisplay.setWrapStyleWord(configuration.isWrapStyleWord());
		statusBarLabels[0].getParent().setVisible(configuration.isShowStateBar());
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		 // type = 1 普通菜单
		 // type = 2 为JcheckMenuItem
		 // 构造菜单参数：菜单名,快捷键字母,快捷键控制键,菜单项类型,是否选中
		MenuTree[] menuTrees = new MenuTree[] {
				new MenuTree( "文件", new String[]{"新建,N,CTRL", "打开,O,CTRL", "保存,S,CTRL", "另存", "退出,Q,CTRL"}),
				new MenuTree( "编辑", new String[]{"查找,F,CTRL", "全选,A,CTRL", "复制,C,CTRL", "粘贴,V,CTRL", "剪切,X,CTRL"}),
				new MenuTree( "格式", new String[]{"自动换行,L,CTRL,2," + configuration.isLineWrap(), "断行不断字,W,CTRL,2," + configuration.isWrapStyleWord(), "字体,T,CTRL"}),
				new MenuTree( "工具", new String[]{"文本转化,E,CTRL", "插件,M,CTRL"}),
				new MenuTree( "查看", new String[]{"显示行号,L,ALT,2," + configuration.isShowLineNumber(), "显示状态栏,S,ALT,2," + configuration.isShowStateBar()}),
				new MenuTree( "帮助", new String[]{"查看帮助,H,ALT", "关于记事本,A,ALT"})};
				
		MenuTree.builderMenu(menuBar, menuTrees, this);
	}

	private void initThem() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			showExceptionMsgDialog(this, "设置主题失败", e.getMessage(), "提示");
			LogUtil.exception(e);
		}
	}

	@ActionHandle("新建")
	public void newDoc() {
		if(model.isDocModifyed()) {
			QuitConfimDialog.show(this, new QuitConfimDialog.OperationDoneListener() {
				
				@Override
				public void onDnoe(int code) {
					if(code == 1) {
						saveDoc();
					} else if(code == 3){
						return;
					}
					Main.this.newDoc();
				}
			});
		} else {
			txaDisplay.setText("");
			model.clearAll();
			model.initNewDoc();
		}
	}

	@ActionHandle("打开")
	public void openDoc() {
		if(model.isDocModifyed()) {
			QuitConfimDialog.show(this, new QuitConfimDialog.OperationDoneListener() {
				
				@Override
				public void onDnoe(int code) {
					if(code == 1) {
						saveDoc();
					} else if(code == 3){
						return;
					}
					model.initNewDoc();
					Main.this.openDoc();
				}
			});
		} else {
			JFileChooser jfc=new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(jfc.showDialog(new JLabel(), "打开") == JFileChooser.APPROVE_OPTION) {
				File file=jfc.getSelectedFile();
				openDoc(file);
			}
		}
	}

	@ActionHandle("保存")
	public boolean saveDoc() {
		String savePath = model.getDocSavePath();
		if(savePath == null || savePath.isEmpty()) {
			JFileChooser jfc=new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(jfc.showDialog(new JLabel(), "保存") != JFileChooser.APPROVE_OPTION)
			{
				return false;
			}
			File saveFile = jfc.getSelectedFile();
			if(Model.fileIsExists(saveFile) && JOptionPane.showConfirmDialog(this,
					"文件已经存在是否覆盖？", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
				return false;
			}
			savePath = saveFile.getAbsolutePath();
			model.setDocSavePath(savePath);
			this.setTitle(saveFile.getName()+" - 记事本");
		}
		
		try {
			String text = txaDisplay.getText();
			model.saveDoc(text, null);
			lastLength = text.length();
		} catch (Exception e) {
			e.printStackTrace();
			showExceptionMsgDialog(Main.this, "保存失败", e.getMessage(), "提示");
			return false;
		}
		return true;
	}
	
	@ActionHandle("另存")
	public void saveAs() {
		try {
			JFileChooser jfc=new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(jfc.showDialog(new JLabel(), "另存") == JFileChooser.APPROVE_OPTION)
			{
				File saveFile=jfc.getSelectedFile();
				if(Model.fileIsExists(saveFile) && JOptionPane.showConfirmDialog(this,
						"文件已经存在是否覆盖？", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
					return;
				}
				model.saveDoc(txaDisplay.getText(), saveFile);
			}
			
		} catch (Exception e) {
			LogUtil.exception(e);
			JOptionPane.showConfirmDialog(null, e.getMessage(), "提示", JOptionPane.PLAIN_MESSAGE);
		}
	}

	@ActionHandle("退出")
	public void preExit() {
		if(model.isDocModifyed()) {
			QuitConfimDialog.show(this, new QuitConfimDialog.OperationDoneListener() {
				
				@Override
				public void onDnoe(int code) {
					if(code == 1) {
						if(!saveDoc())return;
					} else if(code == 3){
						return;
					}
					Main.this.dispose();
				}
			});
		} else {
			Main.this.dispose();
		}
	}

	@ActionHandle("查找")
	public void find() {

	}

	@ActionHandle("全选")
	public void selectAll() {
		txaDisplay.selectAll();
	}

	@ActionHandle("剪切")
	public void cut() {
		txaDisplay.cut();
	}

	@ActionHandle("复制")
	public void copy() {
		txaDisplay.copy();
	}

	@ActionHandle("粘贴")
	public void paste() {
		txaDisplay.paste();
	}

	@ActionHandle("自动换行")
	public void autoBr(Object eventSource) {
		if(eventSource instanceof JMenuItem) {
			boolean lineWrap = ((JMenuItem)eventSource).isSelected();
			txaDisplay.setLineWrap(lineWrap);
			configuration.setLineWrap(lineWrap);
		}
	}
	
	@ActionHandle("断行不断字")
	public void autoBrNotInWorld(Object eventSource) {
		if(eventSource instanceof JMenuItem) {
			boolean wrapStyleWord = ((JMenuItem)eventSource).isSelected();
			txaDisplay.setWrapStyleWord(wrapStyleWord);
			configuration.setWrapStyleWord(wrapStyleWord);
		}
	}

	@ActionHandle("字体")
	private  void setTextStyle() {
		FontFormat a = new FontFormat(Main.this, currentFont, new BiFunction<Font, Color, Boolean>() {
			@Override
			public Boolean apply(Font font, Color color) {
				currentFont = font;
				txaDisplay.setFont(font);
				txaDisplay.setForeground(color);
				return true;
			}
		});
		a.setVisible(true);
	}

	@ActionHandle("显示行号")
	public void showLineNumber(Object eventSource) {
		if(eventSource instanceof JMenuItem) {
			boolean showLineNumber = ((JMenuItem)eventSource).isSelected();
			configuration.setShowLineNumber(showLineNumber);
		}
	}

	@ActionHandle("显示状态栏")
	public void showStateBar(Object eventSource) {
		if(eventSource instanceof JMenuItem) {
			boolean showStateBar = ((JMenuItem)eventSource).isSelected();
			statusBarLabels[0].getParent().setVisible(showStateBar);
			configuration.setShowStateBar(showStateBar);
		}
	}

	@ActionHandle("文本转化")
	public void textConversion() {
		TextConversionDialog dialog = new TextConversionDialog(this);
		dialog.setStartConversionListener(new TextConversionDialog.StartConversionListener() {
			
			@Override
			public void onStart(Class<?> converter, String inPath, String outPath) {
				model.setConversionStateChangeListener(new ConversionStateChangeListener() {
					
					@Override
					public void onStart(int count) {
						
					}
					
					@Override
					public void onProgress(int progress, int count) {
						
					}
					
					@Override
					public void onDone(int successCount, int count) {
						JOptionPane.showConfirmDialog(Main.this, "共转化" + count + "个\n成功" + successCount + "个", "提示",
								JOptionPane.DEFAULT_OPTION);
					}

					@Override
					public void onError(Throwable e) {
						LogUtil.exception(e);
						showExceptionMsgDialog(Main.this, "转化异常", e.getMessage(), "提示");
					}
				});
				
				try {
					model.start(converter, inPath, outPath, Main.this);
				} catch (Throwable e) {
					LogUtil.exception(e);
					showExceptionMsgDialog(Main.this, "转化异常", e.getMessage(), "提示");
				}
				
			}
		});
		dialog.setVisible(true);
	}

	@ActionHandle("查看帮助")
	public void viewHelp() {
		
	}

	@ActionHandle("关于记事本")
	public void viewAbout() {

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		ActionUtil.actionHandle(e, this, actionHandleMap);
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		int offset = e.getDot() ;
		try {
			// 将组件文本中的偏移量转换为行号
			int row = txaDisplay.getLineOfOffset(offset);
			// 取得给定行起始处的偏移量。
            int column = e.getDot() - txaDisplay.getLineStartOffset(row);
            
            int caretPos = txaDisplay.getCaretPosition();
            
            int rows = txaDisplay.getLineCount();
            
            int length = txaDisplay.getText().length();
            if(length != lastLength) {
            	lastLength = length;
            	model.setDocModifyed(true);
            }
            
            updateStateInfo(row, column, caretPos, rows, length, null);
		} catch (BadLocationException e1) {
			LogUtil.exception(e1);
		}
	}
	
	private void updateStateInfo(int row, int column, int caretPos, int rows, int length, String charSet) {
		if(row >= 0)		statusBarLabels[0].setText("第"+row+"行");
		if(column >= 0)		statusBarLabels[1].setText("第"+column+"列");
		if(caretPos >= 0)	statusBarLabels[2].setText("光标:"+caretPos);
		if(rows >= 0)		statusBarLabels[3].setText("行数:"+rows);
		if(length >= 0)		statusBarLabels[4].setText("字数:"+length);
		if(charSet != null)	statusBarLabels[5].setText(charSet);
	}

	public static void showExceptionMsgDialog(Component c, String msg, String cause, String title) {
		if(cause == null) {
			cause = "未知错误";
		}
		JOptionPane.showConfirmDialog(c, msg+" "+cause, "提示", JOptionPane.DEFAULT_OPTION);
	}
	
	private void openDoc(File file) {
		try {
			txaDisplay.setText("");
			model.clearAll();
			txaDisplay.setEditable(false);
			model.readFile(file, new ReadFileActionListener() {
				
				@Override
				public void onOpen(String fileName, String CharsetName) {
					setTitle(fileName+" - 记事本");
					updateStateInfo(-1, -1, -1, -1, -1, model.getDocCharsetName());
				}
				
				@Override
				public void onNewLine(String line, String br, int lineCount, int words) {
					txaDisplay.append(line);
					txaDisplay.append(br);
					updateStateInfo(-1, -1, -1, lineCount, words, null);
				}
				
				@Override
				public void onError(Throwable e) {
				}
				
				@Override
				public void onDeon(List<String> docLines) {
					txaDisplay.setEditable(true);
				}
			});
		} catch (Exception e) {
			LogUtil.exception(e);
			showExceptionMsgDialog(this, "", e.getMessage(), "提示");
		}
	}

	public Model getModel() {
		return model;
	}
}
