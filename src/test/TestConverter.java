package test;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 转化器
 * @author HeRui
 *
 */
public class TestConverter implements ActionListener{
	private JDialog dialog;
	private boolean cancel;
	/**
	 * 显示视图
	 */
	public boolean showView(JFrame parent) {
		dialog = new JDialog(parent, "测试化器", true);
		dialog.setSize(400, 200);
		dialog.setLayout(new FlowLayout(FlowLayout.CENTER));
		dialog.add(new JLabel("本转化器实现下面三个方法"));
		dialog.add(new JLabel("public boolean showView(JFrame parent); //  启动视图，设为模态窗口可用于处理前做一些设置设置或取消转化"));
		dialog.add(new JLabel("public boolean filefilter(File file); // 文件扫描过滤器"));
		dialog.add(new JLabel("public boolean conversion(File file, String code); // 转化方法，必须含有此方法才是能支持的转化器"));
		JButton startBtn, cacnelBtn;
		dialog.add(startBtn = new JButton("开始转化"));
		dialog.add(cacnelBtn = new JButton("取消转化"));
		startBtn.addActionListener(this);
		cacnelBtn.addActionListener(this);
		
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		
		return cancel;
	}
	
	/**
	 * 过滤文件
	 */
	public boolean filefilter(File file) {
		return true;
	}
	
	/**
	 * 执行转化
	 */
	public boolean conversion(File file, File saveDir, String code) {
		System.out.println("开始转化："+file.getAbsolutePath()+"， "+code);
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("开始转化")) {
			dialog.dispose();
		} else {
			cancel = true;
			dialog.dispose();
		}
	}
}
