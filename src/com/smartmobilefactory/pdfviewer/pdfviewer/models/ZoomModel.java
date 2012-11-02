package com.smartmobilefactory.pdfviewer.pdfviewer.models;

import com.smartmobilefactory.pdfviewer.pdfviewer.event.BringUpZoomControlsEvent;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.EventDispatcher;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.ZoomChangedEvent;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.ZoomListener;

public class ZoomModel extends EventDispatcher{

	private float zoom = 1.0f;
	private static final float INCREMENT_DELTA = 0.05f;
	private boolean horizontalScrollEnabled;
	private boolean isCommited;
	
	public float getZoom() {
		return zoom;
	}
	
	public void increaseZoom() {
		setZoom(getZoom() + INCREMENT_DELTA);
	}
	
	public void decreaseZoom() {
		setZoom(getZoom() - INCREMENT_DELTA);
	}
	
	public boolean canDecrement() {
		return zoom > 1.0f;
	}

	public void toggleZoomControls() {
		dispatch(new BringUpZoomControlsEvent());
	}
	
	public void setHorizontalScrollEnabled(final boolean horizontalScrollEnabled) {
		this.horizontalScrollEnabled = horizontalScrollEnabled;
	}

	public boolean isHorizontalScrollEnabled() {
		return horizontalScrollEnabled;
	}
	
	public void commit() {
		if (!isCommited) {
			isCommited = true;
			dispatch(new ZoomListener.CommitZoomEvent());
		} 
	}

	public void setZoom(float zoom) {
		zoom = Math.max(zoom, 1.0f);
		
		if (this.zoom != zoom) {
			final float oldZoom = this.zoom;
			this.zoom = zoom;
			isCommited = false;
			dispatch(new ZoomChangedEvent(zoom, oldZoom));
		}
	}
}
