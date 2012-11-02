package com.smartmobilefactory.pdfviewer.pdfviewer.event;


public class BringUpZoomControlsEvent extends SafeEvent<BringUpZoomControlsListener> {

	@Override
	public void dispatchSafely(BringUpZoomControlsListener listener) {
		listener.toggleZoomControls();
	}

}
