package model;

public interface ConversionStateChangeListener {
	void onStart(int count);
	void onProgress(int progress, int count);
	void onDone(int successCount, int count);
	void onError(Throwable e);
}
