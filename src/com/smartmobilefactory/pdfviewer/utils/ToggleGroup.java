package com.smartmobilefactory.pdfviewer.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class ToggleGroup extends LinearLayout {

	private int mCheckedId = -1;
	private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
	private boolean mProtectFromCheckedChange = false;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private PassThroughHierarchyChangeListener mPassThroughListener;
	
	public ToggleGroup(Context context) {
		super(context);
		setOrientation(VERTICAL);
		init();
	}
	
	public ToggleGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mChildOnCheckedChangeListener = new CheckedStateTracker();
		mPassThroughListener = new PassThroughHierarchyChangeListener();
		super.setOnHierarchyChangeListener(mPassThroughListener);
	}
	
	@Override
	public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
		mPassThroughListener.mOnHierarchyChangeListener = listener;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		if (mCheckedId != -1) {
			mProtectFromCheckedChange = true;
			setCheckedStateForView(mCheckedId, true);
			mProtectFromCheckedChange = false;
			setCheckedId(mCheckedId);
		}
	}
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (child instanceof ToggleButton) {
			final ToggleButton button = (ToggleButton) child;
			
			if (button.isChecked()) {
				mProtectFromCheckedChange = true;
				
				if (mCheckedId != -1) {
					setCheckedStateForView(mCheckedId, false);
				}
				
				mProtectFromCheckedChange = false;
				setCheckedId(button.getId());
			}
		}
		
		super.addView(child, index, params);
	}
	
	public void check(int id) {
		if (id != -1 && (id == mCheckedId)) {
			return;
		}
		
		if (mCheckedId != -1) {
			setCheckedStateForView(mCheckedId, false);
		}
		
		if (id != -1) {
			setCheckedStateForView(id, true);
		}
		
		setCheckedId(id);
	}
	
	private void setCheckedId(int id) {
		mCheckedId = id;
		
		if (mOnCheckedChangeListener != null) {
			mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
		}
	}
	
	private void setCheckedStateForView(int viewId, boolean checked) {
		View checkedView = findViewById(viewId);
		
		if (checkedView != null && checkedView instanceof ToggleButton) {
			((ToggleButton) checkedView).setChecked(checked);
		}
	}

	public int getCheckedRadioButtonId() {
		return mCheckedId;
	}
	
	public void clearCheck() {
		check(-1);
	}
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new ToggleGroup.LayoutParams(getContext(), attrs);
	}
	
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof ToggleGroup.LayoutParams;
	}
	
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	public static class LayoutParams extends LinearLayout.LayoutParams {
		
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}
		
		public LayoutParams(int w, int h) {
			super(w, h);
		}
		
		public LayoutParams(int w, int h, float initWeight) {
			super(w, h, initWeight);
		}
		
		public LayoutParams(ViewGroup.LayoutParams p) {
			super(p);
		}
		
		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}
		
		@Override
		protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
			
			if (a.hasValue(widthAttr)) {
				width = a.getLayoutDimension(widthAttr, "layout_width");
			} else {
				width = WRAP_CONTENT;
			}
			
			if (a.hasValue(heightAttr)) {
				height = a.getLayoutDimension(heightAttr, "layout_height");
			} else {
				height = WRAP_CONTENT;
			}
		}
	}
	
	public interface OnCheckedChangeListener {
		
		public void onCheckedChanged(ToggleGroup group, int checkedId);
	}
	
	private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (mProtectFromCheckedChange) {
				return;
			}
			
			mProtectFromCheckedChange = true;
			
			if (mCheckedId != -1) {
				setCheckedStateForView(mCheckedId, false);
			}
			
			mProtectFromCheckedChange = false;
			
			int id = buttonView.getId();
			setCheckedId(id);
		}
		
	}
	
	private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {

		private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
		
		@Override
		public void onChildViewAdded(View parent, View child) {
			if (parent == ToggleGroup.this && child instanceof ToggleButton) {
				int id = child.getId();
				
				if (id == View.NO_ID) {
					id = child.hashCode();
					child.setId(id);
				}
				
				((ToggleButton) child).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
			}
			
			if (mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewAdded(parent, child);
			}
		}

		@Override
		public void onChildViewRemoved(View parent, View child) {
			if (parent == ToggleGroup.this && child instanceof ToggleButton) {
				((ToggleButton) child).setOnCheckedChangeListener(null);
			}
			
			if (mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
			}
		}
		
	}
}
