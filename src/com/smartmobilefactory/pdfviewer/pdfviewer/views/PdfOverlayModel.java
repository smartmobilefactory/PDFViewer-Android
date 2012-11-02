package com.smartmobilefactory.pdfviewer.pdfviewer.views;

import com.smartmobilefactory.pdfviewer.pdfviewer.event.EventDispatcher;

public class PdfOverlayModel extends EventDispatcher {

	public void togglePdfOverlay() {
		dispatch(new BringUpPdfOverlayEvent());
	}
}
