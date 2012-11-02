package com.smartmobilefactory.pdfviewer.pdfviewer.event;

public class ZoomChangedEvent extends SafeEvent<ZoomListener> {

	private final float newZoom;
	private final float oldZoom;
	
	public ZoomChangedEvent(final float newZoom, final float oldZoom) {
		this.newZoom = newZoom;
		this.oldZoom = oldZoom;
	}
	
	@Override
	public void dispatchSafely(ZoomListener listener) {
		listener.zoomChanged(newZoom, oldZoom);
	}

}
