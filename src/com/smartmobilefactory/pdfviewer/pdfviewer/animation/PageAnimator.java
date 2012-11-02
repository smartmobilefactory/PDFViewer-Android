package com.smartmobilefactory.pdfviewer.pdfviewer.animation;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.ViewState;

public interface PageAnimator {

    PageAnimationType getType();

    void init();

    void resetPageIndexes(final int currentIndex);

    boolean handleTouchEvent(MotionEvent event);

    void draw(Canvas canvas, final ViewState viewState);

    void setViewDrawn(boolean b);

    void FlipAnimationStep();

    int getBackIndex();

    int getForeIndex();
}
