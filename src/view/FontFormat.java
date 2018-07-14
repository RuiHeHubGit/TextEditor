package view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiFunction;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class FontFormat extends JDialog {
	private JLabel nameLb;
	private JLabel styleLb;
	private JLabel sizeLb;
	private JLabel presLb;
	private JTextField nameTx;
	private JTextField styleTx;
	private JTextField sizeTx;
	private JTextField presTx;
	private JList nameLt;
	private JList styleLt;
	private JList sizeLt;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	private JButton approve;
	private JButton cancel;
	private JButton chose;
	private JRadioButton[] language = new JRadioButton[2];
	private ButtonGroup languageg;
	private String Slanguage[] = { new String("中文"), new String("ABC") };

	private static JFrame frame;
	public Font font;
	private static Font curFont = new Font("微软雅黑", 12, 12);// 字体类
	private Color color;// 颜色类
	private static Color curColor = new Color(0, 0, 0);
	private BiFunction<Font, Color, Boolean> onChange;
	

	private JFileChooser fileChoose = new JFileChooser();// 文件选择类实例
	private JDialog colorDlg;// 颜色对话框
	private JColorChooser colorChoose = new JColorChooser();// 颜色选择类实例

	private GraphicsEnvironment environment; // 该类中又获取系统字体的方法；
	private String[] fontNameSet;// 字体‘逻辑名'集
	// 字体‘样式'集的字符串数组
	private String[] fontStyleSet = { "常规", "倾斜", "加粗", "倾斜 加粗" };
	// 字体‘样式'集的常量数组
	private Integer[] fontCon = { Font.PLAIN, Font.ITALIC, Font.BOLD, Font.BOLD | Font.ITALIC };
	// 字体‘大小'集
	private String[] fontSizeSet = { "6", "7", "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26",
			"28", "36", "48", "72" };


	
	public FontFormat(Frame parent, Font font, BiFunction<Font, Color, Boolean> biFunction) {// 无参构造函数
		super(parent, "字体设置", true);
		setLocationRelativeTo(parent);
		this.onChange = biFunction;
		frame = new JFrame();
		this.curFont = font;
		initGUI();
	}

	private void initGUI() {// 字体格式选择器的界面初始化
		try {
			getContentPane().setLayout(null);
			environment = GraphicsEnvironment.getLocalGraphicsEnvironment();// GraphicsEnvironment是一个抽象类，不能实例化，只能用其中的静态方法获取一个实例
			fontNameSet = environment.getAvailableFontFamilyNames();// 获取系统字体
			addMenu();// 加入菜单
			initFont();// 初始化字体
			setSize(380, 337);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setWindowPos();// 使窗口屏幕居中
			setResizable(false);// 大小不可变
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initFont() {// 初始化字体
		if(curFont != null) {
			frame.setFont(curFont);
		}
		// 设置默认字体格式为父窗口font对向的字体格式
		if (frame.getFont() == null) {
			nameTx.setText(fontNameSet[0]);
			styleTx.setText(fontStyleSet[0]);
			sizeTx.setText("12");
			nameLt.setSelectedValue(fontNameSet[0], true);
			styleLt.setSelectedIndex(0);
			sizeLt.setSelectedValue("12", true);
			font = new Font(fontNameSet[0], fontCon[0], 12);
			curFont = font;// 保存原来的字体格式
			curFont = font;// 保存原来的字体格式
			presTx.setFont(font);
			// JOptionPane.showMessageDialog(null, "ccac");
		} else {
			int idxStyle = 0;
			for (int i = 0; i < fontCon.length; i++) {
				if (fontCon[i] == frame.getFont().getStyle())
					idxStyle = i;
			}
			nameTx.setText(frame.getFont().getName());// 改text
			styleTx.setText(fontStyleSet[idxStyle]);
			sizeTx.setText("" + frame.getFont().getSize());
			nameLt.setSelectedValue(frame.getFont().getName(), true);// 改list显示
			styleLt.setSelectedIndex(idxStyle);
			sizeLt.setSelectedValue("" + frame.getFont().getSize(), true);
			font = new Font(fontNameSet[0], fontCon[0], 12);// 保存当前格式
			presTx.setFont(font);// 预览中设为当前模式
		}
	}

	private void addMenu() {// 加入菜单
		// 4个lable---------------------------------------------------------------------------------
		nameLb = new JLabel();
		getContentPane().add(nameLb);
		nameLb.setText("字体：");
		nameLb.setBounds(10, 14, 120, 26);
		nameLb.setFont(new java.awt.Font("SimSun", 1, 13));

		styleLb = new JLabel();
		getContentPane().add(styleLb);
		styleLb.setText("字型：");
		styleLb.setBounds(151, 14, 120, 23);
		styleLb.setFont(new java.awt.Font("SimSun", 1, 13));

		sizeLb = new JLabel();
		getContentPane().add(sizeLb);
		sizeLb.setText("大小：");
		sizeLb.setBounds(275, 14, 79, 24);
		sizeLb.setFont(new java.awt.Font("SimSun", 1, 13));

		presLb = new JLabel();
		getContentPane().add(presLb);
		presLb.setText("预览：");
		presLb.setBounds(151, 150, 120, 80);
		presLb.setFont(new java.awt.Font("SimSun", 1, 14));

		// 4个textfield---------------------------------------------------------------------------------
		nameTx = new JTextField();
		nameTx.setEditable(false);
		getContentPane().add(nameTx);
		nameTx.setBounds(10, 42, 120, 22);

		styleTx = new JTextField();
		styleTx.setEditable(false);
		getContentPane().add(styleTx);
		styleTx.setBounds(151, 42, 100, 21);

		sizeTx = new JTextField();
		sizeTx.setEditable(false);
		getContentPane().add(sizeTx);
		sizeTx.setBounds(275, 42, 79, 22);

		presTx = new JTextField();
		presTx.setEditable(false);
		getContentPane().add(presTx);
		presTx.setBounds(151, 200, 203, 61);
		presTx.setText(Slanguage[1]);

		// 3个下拉条--+监听-----------------------------------------------------------------------------
		jScrollPane1 = new JScrollPane();
		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 74, 120, 210);
		{
			ListModel fontNameModel = new DefaultComboBoxModel(fontNameSet);
			nameLt = new JList();
			jScrollPane1.setViewportView(nameLt);
			nameLt.setModel(fontNameModel);
			nameLt.setBounds(274, 193, 90, 86);
			nameLt.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			nameLt.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					nameLtMouseClicked(evt);
				}
			});
		}

		jScrollPane2 = new JScrollPane();
		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(151, 74, 100, 103);
		{
			ListModel fontStyleModel = new DefaultComboBoxModel(fontStyleSet);
			styleLt = new JList();
			jScrollPane2.setViewportView(styleLt);
			styleLt.setModel(fontStyleModel);
			styleLt.setBounds(310, 215, 70, 102);
			styleLt.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			styleLt.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					styleLtMouseClicked(evt);
				}
			});
		}

		jScrollPane3 = new JScrollPane();
		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(275, 75, 79, 100);
		{
			ListModel fontSizeModel = new DefaultComboBoxModel(fontSizeSet);
			sizeLt = new JList();
			jScrollPane3.setViewportView(sizeLt);
			sizeLt.setModel(fontSizeModel);
			sizeLt.setBounds(300, 218, 54, 102);
			sizeLt.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			sizeLt.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					sizeLtMouseClicked(evt);
				}
			});
		} // -------------------------------------------------------------------------------------

		// 中英选项(---------------------------------------------------------------------------------
		languageg = new ButtonGroup();
		language[0] = new JRadioButton("中");
		getContentPane().add(language[0]);
		language[0].setSelected(false);// 初始化显示
		language[0].setBounds(271, 179, 40, 20);
		language[0].setFont(new java.awt.Font("SimSun", 1, 12));
		languageg.add(language[0]);
		language[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				presTx.setText(Slanguage[0]);
			}
		});

		language[1] = new JRadioButton("英");
		getContentPane().add(language[1]);
		language[1].setSelected(true);
		language[1].setBounds(321, 179, 40, 20);
		language[1].setFont(new java.awt.Font("SimSun", 1, 12));
		languageg.add(language[1]);
		language[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				presTx.setText(Slanguage[1]);
			}
		});

		// 3个按钮+监听---------------------------------------------------------------------------------
		// 确定按钮
		approve = new JButton();
		getContentPane().add(approve);
		approve.setText("确定");
		approve.setBounds(151, 265, 67, 20);
		approve.setFont(new java.awt.Font("微软雅黑", 1, 12));
		approve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				approveActionPerformed(evt);
			}
		});

		// 取消按钮
		cancel = new JButton();
		getContentPane().add(cancel);
		cancel.setText("取消");
		cancel.setBounds(219, 265, 67, 20);
		cancel.setFont(new java.awt.Font("微软雅黑", 1, 12));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelActionPerformed(evt);
			}
		});

		// 颜色选择按钮
		chose = new JButton();
		getContentPane().add(chose);
		chose.setText("颜色");
		chose.setBounds(287, 265, 67, 20);
		chose.setFont(new java.awt.Font("微软雅黑", 1, 12));
		chose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				choseActionPerformed(evt);
			}
		});// -------------------------------------------------------------------------
	}

	private void setWindowPos() {// 窗口居中
		Toolkit kit = Toolkit.getDefaultToolkit();// 抽象类，通过静态方法获取实例
		Dimension frameSize = new Dimension(), screenSize = kit.getScreenSize(); // 获取屏幕的大小
		getSize(frameSize); // 获取窗口大小
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}

	private void nameLtMouseClicked(MouseEvent evt) {// 字体逻辑名列表的鼠标单击事件
		nameTx.setText(nameLt.getSelectedValue().toString());
		font = new Font(nameTx.getText(), font.getStyle(), font.getSize());
		presTx.setFont(font);
	}

	private void styleLtMouseClicked(MouseEvent evt) {// 字体样式列表的鼠标单击事件
		String temp = styleLt.getSelectedValue().toString();
		styleTx.setText(temp);
		int index = 0;
		while (index < 4 && !fontStyleSet[index].equals(temp)) {
			index++;
		}
		font = new Font(font.getName(), fontCon[index], font.getSize());
		presTx.setFont(font);
	}

	private void sizeLtMouseClicked(MouseEvent evt) {// 字体大小列表的鼠标点击事件
		sizeTx.setText(sizeLt.getSelectedValue().toString());
		font = new Font(font.getName(), font.getStyle(), Integer.parseInt(sizeTx.getText()));
		presTx.setFont(font);
	}

	private void approveActionPerformed(ActionEvent evt) {// 确定按钮的触发事件
		String name = nameTx.getText();
		int style = fontCon[styleLt.getSelectedIndex()];
		int size = Integer.parseInt(sizeTx.getText());
		font = new Font(name, style, size);
		frame.setFont(font); // 父窗口的Font对象
		if(onChange != null) {
			if(color == null) {
				color = curColor;
			}
			if(onChange.apply(font , color)) {
				FontFormat.this.curFont = font;// 更新原来保存格式
				FontFormat.this.curColor = color;// 更新颜色
			}
		}
		this.dispose();
	}

	private void cancelActionPerformed(ActionEvent evt) {// 取消按钮的触发事件
		this.dispose();
	}

	private void choseActionPerformed(ActionEvent evt) {// 颜色选择触发事件
		if (colorDlg == null) {
			colorDlg = JColorChooser.createDialog(FontFormat.this, "Select Text Color", true, colorChoose,
					new ColorOKListener(), null);
		}
		colorChoose.setColor(color = presTx.getForeground());
		colorDlg.setVisible(true);
	}

	class ColorOKListener implements ActionListener {// 重写颜色按钮点击监听类覆盖接口ActionListener
		public void actionPerformed(ActionEvent e) {
			Color c = colorChoose.getColor();
			color = c;
			presTx.setForeground(c);
			presTx.repaint();
		}
	}
}