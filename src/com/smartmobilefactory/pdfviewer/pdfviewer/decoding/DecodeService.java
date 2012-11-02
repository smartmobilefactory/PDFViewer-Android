package com.smartmobilefactory.pdfviewer.pdfviewer.decoding;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageLink;


public interface DecodeService {

    void open(String fileName, String password);

    void decodePage(ViewState viewState, PageTreeNode node);

    void stopDecoding(PageTreeNode node, String reason);

    int getPageCount();

    List<OutlineLink> getOutline();
    
    List<PageLink> getPageLinks(int pageNumber);

    CodecPageInfo getPageInfo(int pageIndex);
    
    CodecPage getPage(int pageIndex);

    void recycle();

    Rect getNativeSize(final float pageWidth, final float pageHeight, final RectF nodeBounds, float pageTypeWidthScale);

    Rect getScaledSize(float viewWidth, float pageWidth, float pageHeight, RectF nodeBounds, float zoom, float pageTypeWidthScale);

    void updateViewState(ViewState viewState);
    
    interface DecodeCallback {

        void decodeComplete(CodecPage page, Bitmap bitmap);
    }
}
