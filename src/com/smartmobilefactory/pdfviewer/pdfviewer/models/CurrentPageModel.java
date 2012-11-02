package com.smartmobilefactory.pdfviewer.pdfviewer.models;

import com.smartmobilefactory.pdfviewer.log.LogContext;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.CurrentPageListener;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.EventDispatcher;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageIndex;
import com.smartmobilefactory.pdfviewer.utils.CompareUtils;

public class CurrentPageModel extends EventDispatcher {

    protected static final LogContext LCTX = LogContext.ROOT.lctx("DocModel");

    protected PageIndex currentIndex = PageIndex.FIRST;

    public void setCurrentPageIndex(final PageIndex newIndex) {
        if (!CompareUtils.equals(currentIndex, newIndex)) {
            if (LCTX.isDebugEnabled()) {
                LCTX.d("Current page changed: " + "currentIndex" + " -> " + newIndex);
            }

            final PageIndex oldIndex = this.currentIndex;
            this.currentIndex = newIndex;

            dispatch(new CurrentPageListener.CurrentPageChangedEvent(oldIndex, newIndex));
        }
    }

    public PageIndex getCurrentIndex() {
        return this.currentIndex;
    }

    public int getCurrentViewPageIndex() {
        return this.currentIndex.viewIndex;
    }

    public int getCurrentDocPageIndex() {
        return this.currentIndex.docIndex;
    }
}
