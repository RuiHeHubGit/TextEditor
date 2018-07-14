package model;

import java.lang.reflect.Method;

public class ConvertorData {
	private Object converter;
	private Method showView;
	private Method filefilter;
	private Method conversion;
	
	public Object getConverter() {
		return converter;
	}
	
	public void setConverter(Object converter) {
		this.converter = converter;
	}
	
	public Method getShowView() {
		return showView;
	}
	
	public void setShowView(Method showView) {
		this.showView = showView;
	}
	
	public Method getFilefilter() {
		return filefilter;
	}
	
	public void setFilefilter(Method filefilter) {
		this.filefilter = filefilter;
	}
	
	public Method getConversion() {
		return conversion;
	}
	
	public void setConversion(Method conversion) {
		this.conversion = conversion;
	}
	
	
}
