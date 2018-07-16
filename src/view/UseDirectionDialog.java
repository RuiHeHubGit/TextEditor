package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UseDirectionDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UseDirectionDialog(Frame parent) {
		super(parent, "文本转化说明指导", true);
		setLocationRelativeTo(null);
		initView();
		setVisible(true);
	}

	private void initView() {
		int DIALOG_WHITE = 500;//宽度
		int DIALOG_HEIGHT = 400;//高度
		Point point = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		this.setBounds(point.x - DIALOG_WHITE / 2, point.y - DIALOG_HEIGHT / 2, DIALOG_WHITE, DIALOG_HEIGHT);
		setSize(DIALOG_WHITE, DIALOG_HEIGHT);
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(mainPanel);
		mainPanel.add(new JLabel("文本转化工具使用："), BorderLayout.NORTH);
		
		JPanel contentPanel = new JPanel(new BoxLayout(this, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(new JLabel("一、文本转化工具使用流程"));
		contentPanel.add(new JLabel("1、添加或选择转化器，可从java源码类，字节码和jar包中添加"));
		contentPanel.add(new JLabel("2、选择输入路径，需要转化的文件存放路径"));
		contentPanel.add(new JLabel("3、选择输出路径，转换后的存放路径，转化器可自己决定存放位置"));
		contentPanel.add(new JLabel("4、点击开始按钮启动转化器开始转化，也可以取消转化"));
		contentPanel.add(new JLabel("5、如果转化器有配置界面，可在配置在开始转化"));
		
		contentPanel.add(Box.createVerticalStrut(15));
		contentPanel.add(new JLabel("二、使用的转化器要求，实现下面三个方法可被调用"));
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(new JLabel("1、启动视图，设为模态窗口可用于处理前做一些设置或取消转化，此方法可以不实现"));
		contentPanel.add(new JLabel("public boolean showView(JFrame parent);"));
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(new JLabel("2、文件扫描过滤器,返回false代表文件忽略当前文件，此方法可以不实现"));
		contentPanel.add(new JLabel("public boolean filefilter(File file);"));
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(new JLabel("3、转化器，有效的转化器必须含有此方法"));
		contentPanel.add(new JLabel("public boolean conversion(File inFile, File saveDir, String code);"));
		contentPanel.add(Box.createVerticalStrut(10));
		
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		JButton closeBtn = new JButton("关闭");
		btnPanel.add(closeBtn);
		closeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
}
