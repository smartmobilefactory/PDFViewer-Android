package com.smartmobilefactory.pdfviewer.utils;

public class PDFViewerLibraryLoader {

    private static boolean alreadyLoaded = false;

    public static void load() {
        if (alreadyLoaded) {
            return;
        }
        System.loadLibrary("pdfviewer");
        alreadyLoaded = true;
    }
    
    public static native void free();
}
