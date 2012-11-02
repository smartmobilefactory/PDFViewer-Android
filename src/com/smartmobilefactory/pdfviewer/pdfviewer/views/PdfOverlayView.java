package com.smartmobilefactory.pdfviewer.pdfviewer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.smartmobilefactory.pdfviewer.R;
import com.smartmobilefactory.pdfviewer.utils.ToggleGroup;

public class PdfOverlayView extends RelativeLayout implements BringUpPdfOverlayListener {
	
	public static final int BLUE = 0;
	public static final int PURPLE = 1;
	public static final int BLUE_BOOK_2 = 2;
	
	private ToggleButton btn_tab_bar_overview;
	private ToggleButton btn_tab_bar_content;
	private ToggleButton btn_tab_bar_source;
	private ImageButton btn_back;
		
	public PdfOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.pdf_overlay_blue, this);
		initButtons(view);
	}
	
	public PdfOverlayView(Context context, int color) {
		super(context);
		
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		switch (color) {
		case BLUE:
			View view_blue = layoutInflater.inflate(R.layout.pdf_overlay_blue, this);
			initButtons(view_blue);
			break;
		case PURPLE:
			View view_purple = layoutInflater.inflate(R.layout.pdf_overlay_lila, this);
			initButtons(view_purple);
			break;
		case BLUE_BOOK_2:
			View view_blue_book_2 = layoutInflater.inflate(R.layout.pdf_overlay_blue, this);
			initButtons(view_blue_book_2);
			break;
		}
	}
	
	private void initButtons(View view) {
		btn_tab_bar_overview = (ToggleButton) view.findViewById(R.id.btn_tab_bar_overview);
		btn_tab_bar_content = (ToggleButton) view.findViewById(R.id.btn_tab_bar_content);
		btn_tab_bar_source = (ToggleButton) view.findViewById(R.id.btn_tab_bar_source);
		btn_back = (ImageButton) view.findViewById(R.id.navibar_back);
		
		ToggleGroup.OnCheckedChangeListener toggleListener = new ToggleGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(ToggleGroup toggleGroup, int id) {
				for (int j = 0; j < toggleGroup.getChildCount(); j++) {
					final ToggleButton view = (ToggleButton) toggleGroup.getChildAt(j);
					view.setChecked(view.getId() == id);
				}
			}
		};
		
		ToggleGroup toggleGroup = (ToggleGroup) view.findViewById(R.id.toggle_group);
		toggleGroup.setOnCheckedChangeListener(toggleListener);
	}
	
	public ImageButton getNaviBackButton() {
		return btn_back;
	}
	
	public ToggleButton getOverviewButton() {
		return btn_tab_bar_overview;
	}
	
	public ToggleButton getContentButton() {
		return btn_tab_bar_content;
	}
	
	public ToggleButton getSourceButton() {
		return btn_tab_bar_source;
	}

	@Override
	public void togglePdfOverlay() {
		if (getVisibility() == View.VISIBLE) {
			hide();
		} else {
			show();
		}
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
	
	private void show() {
		fade(View.VISIBLE, getWidth(), 0.0f);
	}
}
