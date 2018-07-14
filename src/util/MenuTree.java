package util;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuTree {
    String paramter;
    MenuTree[] children;
    
    public MenuTree(String paramter) {
		this(paramter, (MenuTree[])null);
	}
    
	public MenuTree(String paramter, MenuTree[] children) {
		this.paramter = paramter;
		this.children = children;
	}
	
	
	public MenuTree(String paramter, String[] paramters) {
		this(paramter, parseMenuTressChildren(paramters));
	}

	private static MenuTree[] parseMenuTressChildren(String[] paramters) {
		MenuTree[] children = null;
		if(paramters != null) {
			children = new MenuTree[paramters.length];
			for(int i=0; i<paramters.length; ++i) {
				children[i] = new MenuTree(paramters[i]);
			}
		}
		return children;
	}

	public static boolean builderMenu(JMenuBar menuBar, MenuTree[] menuTrees, ActionListener listener) {
		if(menuBar == null || menuTrees == null || menuTrees.length == 0) {
			return false;
		}
		for(int i=0, l=menuTrees.length; i<l; ++i) {
			MenuData menuData = MenuData.parse(menuTrees[i].paramter);
			if(menuData != null) {
				JMenu item = new JMenu(menuData.name);
				menuBar.add(item);
				builderMenuItem(item, menuTrees[i], listener);
			}
		}
		return true;
	}
	
	public static boolean builderMenuItem(JMenuItem item, MenuTree menuTree, ActionListener listener) {
		if(item == null || menuTree == null || menuTree.children == null || menuTree.children.length == 0) {
			return false;
		}
		
		MenuTree[] children = menuTree.children;
		for(int i=0, l=children.length; i<l; ++i) {
			MenuData data = MenuData.parse(children[i].paramter);
			if(data != null) {
				JMenuItem childItem;
				if(data.type == 2) {
					childItem = new JCheckBoxMenuItem(data.name, data.checked);
				} else {
					childItem = new JMenuItem(data.name);
				}
				if(data.keyStroke != null) {
					childItem.setAccelerator(data.keyStroke);
				}
				childItem.addActionListener(listener);
				item.add(childItem);
				
				if(children[i].children != null && children[i].children.length > 0) {
					builderMenuItem(childItem, children[i], listener);
				}
			}
		}
		
		
		return false;
	}
	
	static class MenuData {
		String name;
		KeyStroke keyStroke;
		boolean checked = false;
		int type = 0;

		public MenuData(String name, KeyStroke keyStroke, int type, boolean checked) {
			this.name = name;
			this.keyStroke = keyStroke;
			this.checked = checked;
			this.type = type;
		}

		public static MenuData parse(String paramter) {
			if(paramter == null) {
				return null;
			}
			String[] paras = paramter.split(",");
			if(paras.length == 0) {
				return new MenuData(paramter, null, 1, false);
			}
			
			if(paras.length == 1) {
				return new MenuData(paras[0], null, 1, false);
			}
			
			if(paras.length == 3) {
				return new MenuData(paras[0], getKeyStroke(paras[1], paras[2]), 1, false);
			}
			
			if(paras.length == 4) {
				return new MenuData(paras[0], getKeyStroke(paras[1], paras[2]), getMenuType(paras[3]), false);
			}
			
			if(paras.length == 5) {
				return new MenuData(paras[0], getKeyStroke(paras[1], paras[2]), getMenuType(paras[3]), Boolean.parseBoolean(paras[4]));
			}
			return null;
		}

		private static KeyStroke getKeyStroke(String charStr, String string) {
			if(charStr.length() != 1 || !Character.isLetter(charStr.charAt(0))) {
				return null;
			}
			int keyEvent = getKeyEvent(string);
			if(keyEvent > 0)
				return KeyStroke.getKeyStroke(charStr.charAt(0), keyEvent);
			else {
				return KeyStroke.getKeyStroke(charStr.charAt(0));
			}
		}

		private static int getMenuType(String string) {
			int type = Integer.parseInt(string);
			if(type >= 0 && type < 3) {
				return type;
			}
			return 1;
		}

		private static int getKeyEvent(String string) {
			if("CTRL".equals(string)) {
				return KeyEvent.CTRL_MASK;
			} else if("ALT".equals(string)){
				return KeyEvent.ALT_MASK;
			} else {
				return 0;
			}
		}
	}
	
}
