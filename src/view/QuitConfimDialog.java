package view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import annotation.ActionHandle;
import util.ActionUtil;

/**
 * 退出确认对话框
 * @author HeRui
 *
 */
public class QuitConfimDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = -3090527678646987636L;
	private static QuitConfimDialog dialog;
	private HashMap<String, Method> actionHandleMap;
	private OperationDoneListener operationDoneListener;
	
	interface OperationDoneListener{
		void onDnoe(int state);
	}
	
	public QuitConfimDialog(Frame parent) {
		super(parent, parent != null?parent.getTitle():"提示", true);
	}
	
	public static void show(Frame parent, OperationDoneListener listener) {
		if(dialog == null) {
			dialog = new QuitConfimDialog(parent);
			dialog.initView();
		}
		dialog.setLocationRelativeTo(parent);
		dialog.operationDoneListener = listener;
		dialog.setVisible(true);
	}

	private void initView() { 
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) (dim.getWidth() * 0.2), (int) (dim.getHeight() * 0.2));
		setResizable(false);
		
		JButton save = new JButton("保存");
		save.addActionListener(this);
		JButton notSave = new JButton("不保存");
		notSave.addActionListener(this);
		JButton cancle = new JButton("取消");
		cancle.addActionListener(this);
		
		setLayout(new GridLayout(2, 1, 0, 20));
		JLabel title = new JLabel("是否保存对当前文件的更改？");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		add(title);
		
		SpringLayout springLayout = new SpringLayout();
		JPanel btnGroup = new JPanel(springLayout);
		add(btnGroup);
		btnGroup.add(save);
		btnGroup.add(notSave);
		btnGroup.add(cancle);
		
		int btnAllW = Spring.width(save).getValue()
				+ Spring.width(notSave).getValue()
				+ Spring.width(cancle).getValue();
		int svp = (int) ((getWidth()-12-btnAllW) / 4.0f);
		springLayout.putConstraint(SpringLayout.WEST, save, svp, SpringLayout.WEST, btnGroup);
		springLayout.putConstraint(SpringLayout.WEST, notSave, svp, SpringLayout.EAST, save);
		springLayout.putConstraint(SpringLayout.WEST, cancle, svp, SpringLayout.EAST, notSave);
		
		actionHandleMap = new HashMap<>();
		ActionUtil.scanRegisterActionHandle(this.getClass(), actionHandleMap);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ActionUtil.actionHandle(e, this, actionHandleMap);
		setVisible(false);
	}
	
	@ActionHandle("保存")
	private void save() {
		if(operationDoneListener != null) {
			operationDoneListener.onDnoe(1);
		}
	}
	
	@ActionHandle("不保存")
	private void notSave() {
		if(operationDoneListener != null) {
			operationDoneListener.onDnoe(2);
		}
	}
	
	@ActionHandle("取消")
	private void cancle() {
		if(operationDoneListener != null) {
			operationDoneListener.onDnoe(3);
		}
	}

}
