package com.smartmobilefactory.pdfviewer.pdfviewer.decoding;

public interface CodecContext {

    /**
     * Open appropriate document
     *
     * @param fileName
     *            document file name
     * @param password
     *            optional document password
     * @return an instance of a document
     */
    CodecDocument openDocument(String fileName, String password);

    /**
     * @return context handler
     */
    long getContextHandle();

    /**
     * Recycle instance.
     */
    void recycle();

    /**
     * @return <code>true</code> if instance has been recycled
     */
    boolean isRecycled();

}
