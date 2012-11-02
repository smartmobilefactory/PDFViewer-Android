package com.smartmobilefactory.pdfviewer.pdfviewer.event;

import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageIndex;

public interface CurrentPageListener {
	
	void currentPageChanged(PageIndex oldIndex, PageIndex newIndex);
	
	public class CurrentPageChangedEvent extends SafeEvent<CurrentPageListener> {

		private final PageIndex oldIndex;
		private final PageIndex newIndex;
		
		public CurrentPageChangedEvent(final PageIndex oldIndex, final PageIndex newIndex) {
			this.newIndex = newIndex;
			this.oldIndex = oldIndex;
		}
		
		@Override
		public void dispatchSafely(CurrentPageListener listener) {
			listener.currentPageChanged(oldIndex, newIndex);
		}
		
	}
}
