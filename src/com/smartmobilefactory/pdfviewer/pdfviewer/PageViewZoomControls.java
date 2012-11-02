package com.smartmobilefactory.pdfviewer.pdfviewer;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.smartmobilefactory.pdfviewer.pdfviewer.event.BringUpZoomControlsListener;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.ZoomModel;

public class PageViewZoomControls extends LinearLayout implements BringUpZoomControlsListener {

	public PageViewZoomControls(final Context context, final ZoomModel zoomModel) {
		super(context);
		hide();
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.BOTTOM);
		addView(new ZoomRoll(context, zoomModel));
	}

	private void hide() {
		fade(View.GONE, 0.0f, getWidth());
	}

	private void fade(final int visibility, final float startDelta, final float endDelta) {
		final Animation anim = new TranslateAnimation(0, 0, startDelta, endDelta);
		anim.setDuration(500);
		startAnimation(anim);
		setVisibility(visibility);
	}

	@Override
	public void toggleZoomControls() {
		if (getVisibility() == View.VISIBLE) {
			hide();
		} else {
			show();
		}
	}

	private void show() {
		fade(View.VISIBLE, getWidth(), 0.0f);
	}
}
