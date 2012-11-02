package com.smartmobilefactory.pdfviewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.jakewharton.android.viewpagerindicator.CirclePageIndicator;
import com.smartmobilefactory.pdfviewer.R;
import com.smartmobilefactory.pdfviewer.pdfviewer.PdfViewerActivity;
import com.smartmobilefactory.pdfviewer.pdfviewer.views.BookListView;
import com.smartmobilefactory.pdfviewer.pdfviewer.views.PdfOverlayView;

public class BookSelectorActivity extends Activity {

	private ViewPager viewPager;
	private final static int NUM_VIEWS = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library);
		
		initActivity();
	}

	private void initActivity() {
		ImageButton btn_impressum = (ImageButton) findViewById(R.id.btn_impressum);
		btn_impressum.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(BookSelectorActivity.this, PDFViewerAboutActivity.class);
				startActivity(intent);
			}
		});
		
		viewPager = (ViewPager) findViewById(R.id.library_pager);
		viewPager.setAdapter(new LibraryPagerAdapter());
		
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.library_pager_indicator);
		indicator.setViewPager(viewPager);
		indicator.setStrokeColor(getResources().getColor(R.color.inactive_blue));
		indicator.setFillColor(getResources().getColor(R.color.active_blue));
	}
	
	private void showDocument(final int id, int overlay_color) {
		
		File file = new File(getExternalFilesDir(null), ".tmp");
		
		try {
			InputStream is_hydro = getResources().openRawResource(id);			
			OutputStream os = new FileOutputStream(file);
						
			byte[] buffer = new byte[is_hydro.available()];
			is_hydro.read(buffer);
			os.write(buffer);
			is_hydro.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Uri uri = Uri.fromFile(file);
		
		Intent intent = new Intent(this, PdfViewerActivity.class);
		intent.setData(uri);
		intent.putExtra("color", overlay_color);
        startActivity(intent);
	}
	
	private class LibraryPagerAdapter extends PagerAdapter {
		
		@Override
		public void destroyItem(View container, int position, Object view) {
			((ViewPager) container).removeView((BookListView) view);
		}

		@Override
		public void finishUpdate(View container) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			return NUM_VIEWS;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			
			BookListView bookList = new BookListView(BookSelectorActivity.this);
			
			if (position == 0) {
				bookList.getBookLeft().setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						showDocument(R.raw.alice, PdfOverlayView.PURPLE);
					}
				});
	
				bookList.getBookRight().setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						showDocument(R.raw.fables, PdfOverlayView.BLUE);
					}
				});
			} else if (position == 1) {
				bookList.getBookLeft().setImageDrawable(getResources().getDrawable(R.drawable.pic_buch_alice));
				bookList.getBookLeft().setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showDocument(R.raw.alice, PdfOverlayView.BLUE_BOOK_2);
					}
				});
				
				bookList.getBookRight().setVisibility(View.INVISIBLE);
			}
			
			((ViewPager) container).addView(bookList, 0);
			
			return bookList;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (BookListView) object;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View container) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
