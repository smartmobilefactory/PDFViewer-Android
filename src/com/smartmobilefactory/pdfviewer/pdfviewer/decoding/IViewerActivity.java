package com.smartmobilefactory.pdfviewer.pdfviewer.decoding;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.smartmobilefactory.pdfviewer.pdfviewer.models.DecodingProgressModel;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.DocumentModel;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.ZoomModel;
import com.smartmobilefactory.pdfviewer.pdfviewer.multitouch.MultiTouchZoom;
import com.smartmobilefactory.pdfviewer.pdfviewer.views.PdfOverlayModel;

public interface IViewerActivity {

    Context getContext();

    Activity getActivity();

    DecodeService getDecodeService();

    DocumentModel getDocumentModel();

    View getView();

    IDocumentViewController getDocumentController();

    ZoomModel getZoomModel();

    MultiTouchZoom getMultiTouchZoom();

    DecodingProgressModel getDecodingProgressModel();

    void createDocumentView();

	PdfOverlayModel getPdfOverlayModel();
}
