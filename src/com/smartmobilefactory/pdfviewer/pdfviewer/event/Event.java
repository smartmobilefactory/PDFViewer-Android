package com.smartmobilefactory.pdfviewer.pdfviewer.event;

public interface Event<T> {
	
	void dispatchOn(Object listener);
}
