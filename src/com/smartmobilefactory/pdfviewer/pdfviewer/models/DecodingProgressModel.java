package com.smartmobilefactory.pdfviewer.pdfviewer.models;

import com.smartmobilefactory.pdfviewer.pdfviewer.event.DecodingProgressListener;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.EventDispatcher;

public class DecodingProgressModel extends EventDispatcher {

	private int currentlyDecoding;
	
	public void increase() {
		currentlyDecoding++;
		dispatchChanged();
	}

	private void dispatchChanged() {
		dispatch(new DecodingProgressListener.DecodingProgressEvent(currentlyDecoding));
	}

	public void decrease() {
		currentlyDecoding--;
		dispatchChanged();
	}

}
