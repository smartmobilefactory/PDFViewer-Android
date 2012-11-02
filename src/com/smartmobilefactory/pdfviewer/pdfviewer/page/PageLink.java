package com.smartmobilefactory.pdfviewer.pdfviewer.page;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.smartmobilefactory.pdfviewer.log.LogContext;

public class PageLink {

    private static final LogContext LCTX = LogContext.ROOT.lctx("dddd");

    private final int rect_type;
    private final int[] data;
    private final String url;
    private final Paint paint;
    private RectF zoomedRect;
    private float zoomLevel;
    private int left;
    private int top;
    private float viewHeigth;

    PageLink(final String l, final int type, final int[] dt) {
        rect_type = type;
        data = dt;
        url = l;
        paint = new Paint();
        paint.setColor(Color.RED);
        zoomedRect = new RectF();
        zoomLevel = 1.0f;
        left = 0;
        top = 0;
    }

    public int getType() {
        return rect_type;
    }

    public RectF getRectF() {
        return new RectF(data[0], data[1], data[2], data[3]);
    }

    public void setPageZoomLevel(float zoom) {
    	this.zoomLevel = zoom;
    }
    
    public RectF getRealRectF() {
    	return new RectF(getRectF().left * zoomLevel, 
    					 viewHeigth - getRectF().bottom * zoomLevel, 
    					 getRectF().right * zoomLevel, 
    					 viewHeigth - getRectF().top * zoomLevel);
    }
    
    public String getUrl() {
    	return url;
    }
    
    public void updateRect(Canvas canvas, float zoom, float zoomLevel, int newLeft, int newTop, float viewHeight) {
    	setPageZoomLevel(zoomLevel);
    	setLeft(newLeft);
    	setTop(newTop);
    	setViewHeight(viewHeight);
    	update(zoom);
//    	canvas.drawRect(zoomedRect, paint);
    }
    
    private void update(float zoom) {
    	float left = getRealRectF().left * zoom;
    	float top = getRealRectF().top * zoom;
    	float right = getRealRectF().right * zoom;
    	float bottom = getRealRectF().bottom * zoom;
    	zoomedRect = new RectF(left - this.left, top - this.top, right - this.left, bottom - this.top);
	}

	public void debug() {
        if (LCTX.isDebugEnabled()) {
            LCTX.d(url);
            LCTX.d(rect_type + "   " + data.toString() + "   " + data.length);
            for (int i = 0; i < data.length; i++) {
                LCTX.d("data[" + i + "]" + data[i]);
            }
        }
    }

	public void setLeft(int newLeft) {
		left = newLeft;
	}

	public void setTop(int newTop) {
		top = newTop;
	}
	
	public void setViewHeight(float viewHeight) {
		this.viewHeigth = viewHeight;
	}

	public boolean isPressed(float x, float y) {
		return x > zoomedRect.left && x < zoomedRect.right && y > zoomedRect.top && y < zoomedRect.bottom;
	}
}
