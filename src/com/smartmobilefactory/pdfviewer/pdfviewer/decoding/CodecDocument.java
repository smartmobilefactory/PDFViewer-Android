package com.smartmobilefactory.pdfviewer.pdfviewer.decoding;

import java.util.List;

import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageLink;

public interface CodecDocument {

    int getPageCount();

    CodecPage getPage(int pageNuber);

    CodecPageInfo getPageInfo(int pageNuber);

    List<PageLink> getPageLinks(int pageNuber);

    List<OutlineLink> getOutline();

    void recycle();

    /**
     * @return <code>true</code> if instance has been recycled
     */
    boolean isRecycled();
}
