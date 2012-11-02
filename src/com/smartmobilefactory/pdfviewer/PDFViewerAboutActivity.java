package com.smartmobilefactory.pdfviewer;

import com.smartmobilefactory.pdfviewer.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;

public class PDFViewerAboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		
		WebView web = (WebView) findViewById(R.id.web_view);
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("file:///android_asset/impressum.html");
		
		ImageButton btn_navi_back = (ImageButton) findViewById(R.id.navibar_back);
		btn_navi_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
