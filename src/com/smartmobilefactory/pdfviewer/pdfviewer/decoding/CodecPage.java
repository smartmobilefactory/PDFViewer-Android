package com.smartmobilefactory.pdfviewer.pdfviewer.decoding;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

public interface CodecPage {

    int getWidth();

    int getHeight();

    Bitmap renderBitmap(int width, int height, RectF pageSliceBounds);

    void recycle();        

	Matrix getMatrix();
}
