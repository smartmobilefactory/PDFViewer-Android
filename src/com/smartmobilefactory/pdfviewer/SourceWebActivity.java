package com.smartmobilefactory.pdfviewer;

import com.smartmobilefactory.pdfviewer.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SourceWebActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.source_web);
		
		String url = getIntent().getStringExtra("url");
		
		Log.d("Web View", url + "");
		
		WebView web_source = (WebView) findViewById(R.id.web_source);
		web_source.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
			
		});
		web_source.loadUrl(url);
	}
}
