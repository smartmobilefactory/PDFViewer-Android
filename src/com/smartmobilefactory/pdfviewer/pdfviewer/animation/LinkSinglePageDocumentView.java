package com.smartmobilefactory.pdfviewer.pdfviewer.animation;

import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.smartmobilefactory.pdfviewer.SourceWebActivity;
import com.smartmobilefactory.pdfviewer.pdfviewer.BaseViewerActivity;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.ViewState;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageLink;

public class LinkSinglePageDocumentView extends SinglePageDocumentView {

	private Paint paint;
	private List<PageLink> pageLinks;
	private float zoom;
	private float zoomLevel;
	private int newLeft;
	private int newTop;
	private float viewHeight;
	private int currentSite;
	private GestureDetector gestureDetector;
	
	public LinkSinglePageDocumentView(final BaseViewerActivity baseActivity) {
		super(baseActivity);
		paint = new Paint();
		paint.setColor(Color.RED);
		zoom = 1.0f;
		newLeft = 0;
		newTop = 0;
		gestureDetector = new GestureDetector(getContext(), new GestureListener());
	}

	@Override
	protected void init() {
		super.init();
		goToPageImpl(currentSite);
	}
	
	public void setCurrentSite(int site) {
		this.currentSite = site;
	}
	
	public void setViewHeight(float viewHeight) {
		this.viewHeight = viewHeight;
	}
	
	public void setPageLinks(List<PageLink> pageLinks) {
		this.pageLinks = pageLinks;
	}
	
	@Override
	public final void zoomChanged(float newZoom, float oldZoom) {
		zoom = newZoom;
		
		super.zoomChanged(newZoom, oldZoom);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		newLeft = l;
		newTop = t;
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	@Override
	public final void drawView(Canvas canvas, ViewState viewState) {
		super.drawView(canvas, viewState);
		
		if (getBase().getDocumentModel().getCurrentPageObject() != null) {
			this.viewHeight = getBase().getDocumentModel().getCurrentPageObject().getBounds(1.0f).height();	
			zoomLevel = viewHeight / getBase().getDecodeService().getPageInfo(0).getHeight();
				
			if (pageLinks != null) {
				for (PageLink link : pageLinks) {
					link.updateRect(canvas, zoom, zoomLevel, newLeft, newTop, (viewHeight + (getHeight() - viewHeight) / 2));
				}
			}
		}
	}

	public void setZoomLevel(float zoom) {
		this.zoomLevel = zoom;
	}
	
	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		
		gestureDetector.onTouchEvent(event);
		
		return super.onTouchEvent(event);
	}

	private void openLink(final PageLink link) {
		try {
			goToPage(Integer.parseInt(link.getUrl()) - 1);
		} catch (NumberFormatException e) {
			Intent intent = new Intent(getContext(), SourceWebActivity.class);
			intent.putExtra("url", link.getUrl());
			getContext().startActivity(intent);
		}
	}

//	public void setCurrentSite(int current) {
//		this.currentSite = current;
//	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			
			if (pageLinks != null) {
				
				for (PageLink link : pageLinks) {
									
					if (link.isPressed(e.getX(), e.getY())) {
//						Log.d("LinkSinglePage", link.getUrl() + "");
						openLink(link);
						break;
					}
				}
			}
			return true;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
			
			if (velocityTracker == null) {
				velocityTracker = VelocityTracker.obtain();
			}
			velocityTracker.addMovement(e1);
			
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}
