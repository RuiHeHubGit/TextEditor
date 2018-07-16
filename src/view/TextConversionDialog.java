package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;

import annotation.ActionHandle;
import model.Configuration;
import model.Model;
import util.ActionUtil;
import util.CompilerUtil;

public class TextConversionDialog extends JDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static HashMap<String, Class<?>> converterMap;
	private static Vector<String> converterNameVector;
	private Main parent;
	private HashMap<String, Method> actionHandleMap;
	private ListModel<String> converterListModel;
	private JList<String> converterList;
	private JTextField currentConverterName;
	private JTextField inPathEdit;
	private JTextField outPathEdit;
	private JTextField converterResult;
	private StartConversionListener startConversionListener;
	private Class<?> converterClass;
	private String inPath;
	private String outPath;
	
	public static interface StartConversionListener {
		void onStart(Class<?> converter, String inPath, String outPath);
	}
	
	public void setStartConversionListener(StartConversionListener startConversionListener) {
		this.startConversionListener = startConversionListener;
	}
	
	public StartConversionListener getStartConversionListener() {
		return startConversionListener;
	}

	public TextConversionDialog(Main parent) {
		super(parent, true);
		setLocationRelativeTo(parent);
		this.parent = parent;
		setTitle("文本转化工具");
		setSize(600, 400);
		setLocationRelativeTo(null);
		initMainPanel();
	}

	private void initMainPanel() {
		setLayout(new BorderLayout());
		initConverterList();
		initOperationBtns();
		initInfoPanel();
	}

	private void initConverterList() {
		JLabel listTitle = new JLabel("转化器列表");
		add(listTitle, BorderLayout.NORTH);
		
		converterList = new JList<String>();
		add(converterList, BorderLayout.CENTER);
		
		if(converterMap == null) {
			converterMap = new HashMap<>();
		}
		if(converterNameVector == null) {
			converterNameVector = new Vector<>();
		}
		
		converterListModel =  new DefaultComboBoxModel<String>(converterNameVector);  //数据模型
		converterListModel.addListDataListener(new ListDataListener() {
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				System.out.println(e);
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				System.out.println(e);
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				System.out.println(e);
			}
		});
		converterList.setModel(converterListModel);
		if(converterNameVector.size() > 0) {
			converterClass = converterMap.get(converterNameVector.get(0));
		}
	}

	private void initOperationBtns() {
		JPanel btnGroup = new JPanel();
		btnGroup.setLayout(new BoxLayout(btnGroup, BoxLayout.Y_AXIS));
		btnGroup.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		add(btnGroup, BorderLayout.EAST);
		
		Insets insets = new Insets(5, 10, 5, 10);
		
		JButton useDirectionBtn = new JButton("使用说明");
		useDirectionBtn.setMargin(insets);
		useDirectionBtn.addActionListener(this);
		btnGroup.add(useDirectionBtn);
		
		btnGroup.add(Box.createVerticalStrut(5));
		JButton addConverterBtn = new JButton("添转化器");
		addConverterBtn.setMargin(insets);
		addConverterBtn.addActionListener(this);
		btnGroup.add(addConverterBtn);
		
		btnGroup.add(Box.createVerticalStrut(5));
		JButton setInBtn = new JButton("选择输入");
		setInBtn.setMargin(insets);
		setInBtn.addActionListener(this);
		btnGroup.add(setInBtn);
		
		btnGroup.add(Box.createVerticalStrut(5));
		JButton setOutBtn = new JButton("选择输出");
		setOutBtn.addActionListener(this);
		setOutBtn.setMargin(insets);
		btnGroup.add(setOutBtn);
		
		btnGroup.add(Box.createVerticalStrut(5));
		JButton startBtn = new JButton("开始转化");
		startBtn.addActionListener(this);
		startBtn.setMargin(insets);
		btnGroup.add(startBtn);
		
		actionHandleMap = new HashMap<>();
		ActionUtil.scanRegisterActionHandle(this.getClass(), actionHandleMap);
	}
	
	private void initInfoPanel() {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
		add(infoPanel, BorderLayout.SOUTH);
		
		Color valueColor = new Color(60, 10, 150);
		
		infoPanel.add(Box.createVerticalStrut(5));
		JPanel inPathInfoPanel = new JPanel();
		inPathInfoPanel.setLayout(new BoxLayout(inPathInfoPanel, BoxLayout.X_AXIS));
		infoPanel.add(inPathInfoPanel);
		inPathInfoPanel.add(new JLabel("输入路径："));
		inPathInfoPanel.add(inPathEdit = new JTextField());
		inPathEdit.setForeground(valueColor);
		
		infoPanel.add(Box.createVerticalStrut(5));
		JPanel outPathInfoPanel = new JPanel();
		outPathInfoPanel.setLayout(new BoxLayout(outPathInfoPanel, BoxLayout.X_AXIS));
		infoPanel.add(outPathInfoPanel);
		infoPanel.add(Box.createVerticalStrut(5));
		outPathInfoPanel.add(new JLabel("输出路径："));
		outPathInfoPanel.add(outPathEdit = new JTextField());
		outPathEdit.setForeground(valueColor);
		
		infoPanel.add(Box.createVerticalStrut(5));
		JPanel conversionNamePanel = new JPanel();
		conversionNamePanel.setLayout(new BoxLayout(conversionNamePanel, BoxLayout.X_AXIS));
		infoPanel.add(conversionNamePanel);
		conversionNamePanel.add(new JLabel("  转化器："));
		conversionNamePanel.add(currentConverterName = new JTextField(converterClass != null ? converterClass.getTypeName():""));
		currentConverterName.setEditable(false);
		currentConverterName.setBorder(null);
		currentConverterName.setForeground(valueColor);
		
		infoPanel.add(Box.createVerticalStrut(5));
		JPanel converResultPanel = new JPanel();
		converResultPanel.setLayout(new BoxLayout(converResultPanel, BoxLayout.X_AXIS));
		infoPanel.add(converResultPanel);
		converResultPanel.add(new JLabel("转化结果："));
		converResultPanel.add(converterResult = new JTextField());
		converterResult.setEditable(false);
		converterResult.setBorder(null);
		converterResult.setForeground(valueColor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ActionUtil.actionHandle(e, this, actionHandleMap);
	}
	
	@ActionHandle("使用说明")
	public void showUseDirection() {
		new UseDirectionDialog(parent);
	}
	
	@ActionHandle("添转化器")
	public void addConverter() {
		JFileChooser jfc=new JFileChooser(Configuration.getConfiguration().getFileChooserPath());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "*.java;*.class;*.jar";
			}
			
			@Override
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".java")
						|| f.getName().toLowerCase().endsWith(".class")
						|| f.getName().toLowerCase().endsWith(".jar");
			}
		});
		
		if(jfc.showDialog(new JLabel(), "打开") == JFileChooser.APPROVE_OPTION) {
			File file=jfc.getSelectedFile();
			Configuration.getConfiguration().setFileChooserPath(file.getPath());
			//编译插件
			try {
				Class<?> clazz = CompilerUtil.CompilerAndLoader(file);
				Model.getConverterData(clazz);
				if(converterMap.put(clazz.getTypeName(), clazz) == null) {
					converterNameVector.add(clazz.getTypeName());
					converterList.setModel(converterListModel);
				}
				
				if(converterNameVector.size() == 1) {
					converterClass = clazz;
					currentConverterName.setText(clazz.getName());
				}
			} catch (Throwable e) {
				Main.showExceptionMsgDialog(this, "添加转化器失败", e.getMessage(), "提示");
			}
			
		}
	}
	
	@ActionHandle("选择输入")
	public void setInPath() {
		JFileChooser jfc=new JFileChooser("./");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(jfc.showDialog(new JLabel(), "选择") == JFileChooser.APPROVE_OPTION) {
			File file=jfc.getSelectedFile();
			this.inPath = file.getAbsolutePath();
			inPathEdit.setText(this.inPath);
		}
	}
	
	@ActionHandle("选择输出")
	public void setOutPath() {
		JFileChooser jfc=new JFileChooser("./");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(jfc.showDialog(new JLabel(), "选择") == JFileChooser.APPROVE_OPTION) {
			File file=jfc.getSelectedFile();
			this.outPath = file.getAbsolutePath();
			outPathEdit.setText(this.outPath);
		}
	}
	

	@ActionHandle("开始转化")
	public void startConvertion() {
		if(startConversionListener != null) {
			startConversionListener.onStart(converterClass, inPath, outPath);
		}
	}
}
