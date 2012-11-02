package com.smartmobilefactory.pdfviewer.pdfviewer.decoding;

import android.graphics.RectF;
import android.view.View;

import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageAlign;

public interface IDocumentViewController {

	IViewerActivity getBase();

	void invalidatePageSizes(InvalidateSizeReason reason, Page changedPage);
	
	int getFirstVisiblePage();

	int getLastVisiblePage();

	View getView();

	RectF getViewRect();

	int calculateCurrentPage(ViewState viewState);

	public static enum InvalidateSizeReason {
		INIT, LAYOUT, PAGE_ALIGN, ZOOM, PAGE_LOADED;
	}

	void redrawView(ViewState viewState);

	void updateMemorySettings();

	void showDocument();

	void goToPage(int toPage);

	void setAlign(PageAlign align);

	void redrawView();

	void updateAnimationType();
}
