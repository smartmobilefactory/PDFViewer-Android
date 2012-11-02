package com.smartmobilefactory.pdfviewer.pdfviewer;

import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.AbstractCodecContext;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.CodecDocument;
import com.smartmobilefactory.pdfviewer.utils.PDFViewerLibraryLoader;

public class PdfContext extends AbstractCodecContext {

    static {
        PDFViewerLibraryLoader.load();
    }

    @Override
    public CodecDocument openDocument(final String fileName, final String password) {
        return new PdfDocument(this, fileName, password);
    }
}
