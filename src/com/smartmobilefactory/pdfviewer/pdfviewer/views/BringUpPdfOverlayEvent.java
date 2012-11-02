package com.smartmobilefactory.pdfviewer.pdfviewer.views;

import com.smartmobilefactory.pdfviewer.pdfviewer.event.SafeEvent;

public class BringUpPdfOverlayEvent extends SafeEvent<BringUpPdfOverlayListener> {

	@Override
	public void dispatchSafely(BringUpPdfOverlayListener listener) {
		listener.togglePdfOverlay();
	}

}
