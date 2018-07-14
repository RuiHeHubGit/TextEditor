package util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.AbstractButton;

import annotation.ActionHandle;

public class ActionUtil {
	
	public static void scanRegisterActionHandle(Class<?> listenerClass, HashMap<String, Method> actionHandleMap) {
		Method[] methods = listenerClass.getDeclaredMethods();
		for (Method m  : methods) {
			ActionHandle annotation = m.getAnnotation(ActionHandle.class);
			if(annotation != null) {
				m.setAccessible(true);
				actionHandleMap.put(annotation.value(),  m);
			}
		}
	}
	
	public static void actionHandle(ActionEvent e, ActionListener listener, HashMap<String, Method> actionHandleMap) {
		Object tag = e.getSource();
		String text = null;
		if(tag instanceof  AbstractButton) {
			text = ((AbstractButton)tag).getText();
		}
		
		if(text != null) {
			Method handel = actionHandleMap.get(text);
			if (handel != null) {
				try {
					if(handel.getParameterTypes().length == 0) {
						handel.invoke(listener);
					} else if(handel.getParameterTypes().length == 1){
						handel.invoke(listener, tag);
					}
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
			}
			System.out.println(text);
		}
	}
}
