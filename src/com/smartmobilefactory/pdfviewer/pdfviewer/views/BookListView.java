package com.smartmobilefactory.pdfviewer.pdfviewer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.smartmobilefactory.pdfviewer.R;

public class BookListView extends LinearLayout {

	private ImageButton book_left;
	private ImageButton book_right;
	
	public BookListView(Context context) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.booklist, this);
		book_left = (ImageButton) view.findViewById(R.id.book_left);
		book_right = (ImageButton) view.findViewById(R.id.book_right);
	}
	
	public ImageButton getBookLeft() {
		return book_left;
	}
	
	public ImageButton getBookRight() {
		return book_right;
	}
}
