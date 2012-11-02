package com.smartmobilefactory.pdfviewer.pdfviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.smartmobilefactory.pdfviewer.R;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.ZoomModel;

public class ZoomRoll extends View {

	private final Bitmap left;
	private final Bitmap right;
	private final Bitmap center;
	private final Bitmap serifs;
	private VelocityTracker velocityTracker;
	private final Scroller scroller;
	private float lastX;
	private static final int MAX_VALUE = 10000;
	private final ZoomModel zoomModel;
	private static final float MULTIPLIER = 400.0f;

	public ZoomRoll(final Context context, final ZoomModel zoomModel) {
		super(context);
		this.zoomModel = zoomModel;
		left = BitmapFactory.decodeResource(context.getResources(), R.drawable.left);
		right = BitmapFactory.decodeResource(context.getResources(), R.drawable.right);
		center = BitmapFactory.decodeResource(context.getResources(), R.drawable.center);
		serifs = BitmapFactory.decodeResource(context.getResources(), R.drawable.serifs);
		
		scroller = new Scroller(context);
		
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(left.getHeight(), right.getHeight()));
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		final Paint paint = new Paint();
		canvas.drawBitmap(center, new Rect(0, 0, center.getWidth(), center.getHeight()), 
				new Rect(0, 0, getWidth(), getHeight()), paint);
		float currentOffset = -getCurrentValue() % 40;
		
		while (currentOffset < getWidth()) {
			canvas.drawBitmap(serifs, currentOffset, (getHeight() - serifs.getHeight()) / 2.0f, paint);
			currentOffset += serifs.getWidth();
		}
		
		canvas.drawBitmap(left, 0, 0, paint);
		canvas.drawBitmap(right, getWidth() - right.getWidth(), getHeight() - right.getHeight(), paint);
		
		Paint textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(24);
		textPaint.setAntiAlias(true);
		String zoomText = String.format("%.2f", zoomModel.getZoom());
		canvas.drawText(zoomText + "x", 6, getHeight() / 2 + 12, textPaint);
	}
	
	private float getCurrentValue() {
		return (zoomModel.getZoom() - 1.0f) * MULTIPLIER;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		super.onTouchEvent(event);
		
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		
		velocityTracker.addMovement(event);
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!scroller.isFinished()) {
					scroller.abortAnimation();
					zoomModel.commit();
				}
				
				lastX = event.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				setCurrentValue(getCurrentValue() - (event.getX() - lastX));
				lastX = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				velocityTracker.computeCurrentVelocity(1000);
				scroller.fling((int) getCurrentValue(), 0, (int) -velocityTracker.getXVelocity(), 0, 0, MAX_VALUE, 0, 0);
				velocityTracker.recycle();
				velocityTracker = null;
				
				if (!scroller.computeScrollOffset()) {
					zoomModel.commit();
				}
				
				break;
		}
		invalidate();
		return true;
	}
	
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			setCurrentValue(scroller.getCurrX());
			invalidate();
		} else {
			zoomModel.commit();
		}
	}

	private void setCurrentValue(float currentValue) {
		if (currentValue < 0.0) {
			currentValue = 0.0f;
		}
		
		if (currentValue > MAX_VALUE) {
			currentValue = MAX_VALUE;
		}
		
		final float zoom = 1.0f + currentValue / MULTIPLIER;
		zoomModel.setZoom(zoom);
	}
}
