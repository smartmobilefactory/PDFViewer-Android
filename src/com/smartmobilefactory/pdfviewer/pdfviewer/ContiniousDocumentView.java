package com.smartmobilefactory.pdfviewer.pdfviewer;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.AbstractDocumentView;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.IViewerActivity;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.Page;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.ViewState;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.DocumentModel;
import com.smartmobilefactory.pdfviewer.pdfviewer.settings.SettingsManager;

public class ContiniousDocumentView extends AbstractDocumentView {

	public ContiniousDocumentView(final IViewerActivity base) {
		super(base);
	}

	@Override
	public synchronized final void invalidatePageSizes(final InvalidateSizeReason reason, final Page changedPage) {
		if (!isInitialized()) {
			return;
		}
		
		if (reason == InvalidateSizeReason.PAGE_ALIGN) {
			return;
		}
		
		if (reason == InvalidateSizeReason.ZOOM) {
			return;
		}
		
		final int width = getWidth();
		
		if (changedPage == null) {
			float heightAccum = 0;
			
			for (final Page page : getBase().getDocumentModel().getPages()) {
				final float pageHeight = width / page.getAspectRatio();
				page.setBounds(new RectF(0, heightAccum, width, heightAccum + pageHeight));
				heightAccum += pageHeight;
			}
		} else {
			float heightAccum = changedPage.getBounds(1.0f).top;
			
			for (final Page page : getBase().getDocumentModel().getPages(changedPage.index.viewIndex)) {
				final float pageHeight = width / page.getAspectRatio();
				page.setBounds(new RectF(0, heightAccum, width, heightAccum + pageHeight));
				heightAccum += pageHeight;
			}
		}
	}

	@Override
	public int calculateCurrentPage(final ViewState viewState) {
		int result = 0;
		long bestDistance = Long.MAX_VALUE;
		
		final int viewY = Math.round(viewState.viewRect.centerY());
		
		if (viewState.firstVisible != -1) {
			for (final Page page : getBase().getDocumentModel().getPages(viewState.firstVisible, viewState.lastVisible + 1)) {
				final RectF bounds = viewState.getBounds(page);
				final int pageY = Math.round(bounds.centerY());
				final long dist = Math.abs(pageY - viewY);
				
				if (dist < bestDistance) {
					bestDistance = dist;
					result = page.index.viewIndex;
				}
			}
		}
		
		return result;
	}

	@Override
	protected void onScrollChanged(final int newPage, final int direction) {
		if (inZoom.get()) {
			return;
		}
		
		base.getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final ViewState viewState = updatePageVisibility(newPage, direction, getBase().getZoomModel().getZoom());
				final DocumentModel dm = getBase().getDocumentModel();
				final Page page = dm.getPageObject(viewState.currentIndex);
				
				if (page != null) {
					dm.setCurrentPageIndex(page.index);
					redrawView(viewState);
				}
			}
		});
	}
	
	@Override
	public void updateAnimationType() {
		// This mode do not use animation
	}

	@Override
	protected final void goToPageImpl(final int toPage) {
		final DocumentModel dm = getBase().getDocumentModel();
		
		if  (toPage >= 0 && toPage < dm.getPageCount()) {
			final Page page = dm.getPageObject(toPage);
			
			if (page != null) {
				final RectF viewRect = this.getViewRect();
				final RectF bounds = page.getBounds(getBase().getZoomModel().getZoom());
				
				dm.setCurrentPageIndex(page.index);
				scrollTo(getScrollX(), Math.round(bounds.top - (viewRect.height() - bounds.height()) / 2 ));
			}
		}
	}

	@Override
	protected final void verticalConfigScroll(final int direction) {
		final int scrollHeight = SettingsManager.getAppSettings().getScrollHeight();
		final int dy = (int) (direction * getHeight() * (scrollHeight / 100.0));
		
		getScroller().startScroll(getScrollX(), getScrollY(), 0, dy);
		scrollBy(0, dy);
		redrawView();
	}

	@Override
	protected final void verticalDpadScroll(final int direction) {
		final int dy = direction * getHeight() / 2;
		getScroller().startScroll(getScrollX(), getScrollY(), 0, dy);
		scrollBy(0, dy);
		redrawView();
	}

	@Override
	protected final Rect getScrollLimits() {
		final int width = getWidth();
		final int height = getHeight();
		final Page lpo = getBase().getDocumentModel().getLastPageObject();
		final float zoom = getBase().getZoomModel().getZoom();
		
		final int bottom = lpo != null ? (int) lpo.getBounds(zoom).bottom - height : 0;
		final int right = (int) (width * zoom) - width;
		return new Rect(0, 0, right, bottom);
	}

	@Override
	protected boolean isPageVisibleImpl(Page page, ViewState viewState) {
		return RectF.intersects(viewState.viewRect, viewState.getBounds(page));
	}

	@Override
	public synchronized final void drawView(final Canvas canvas, final ViewState viewState) {
		final DocumentModel dm = getBase().getDocumentModel();
		
		for (int i = viewState.firstVisible; i <= viewState.lastVisible; i++) {
			final Page page = dm.getPageObject(i);
			
			if (page != null) {
				page.draw(canvas, viewState);
			}
		}
	}
	
	@Override
	protected final void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
		int page = -1; 
		
		if (changed) {
			page = base.getDocumentModel().getCurrentViewPageIndex();
		}
		
		super.onLayout(changed, left, top, right, bottom);
		
		invalidatePageSizes(InvalidateSizeReason.LAYOUT, null);
		invalidateScroll();
		commitZoom();
		
		if (page > 0) {
			goToPage(page);
		}
	}
}
