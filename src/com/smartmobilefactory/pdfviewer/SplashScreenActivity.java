package com.smartmobilefactory.pdfviewer;

import com.smartmobilefactory.pdfviewer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

	protected boolean mActive = true;
	protected final int SPLASH_TIME = 5000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		Thread splashThread = new Thread() {
			
			@Override
			public void run() {
				try {
					int waited = 0;
					
					while (mActive && (waited < SPLASH_TIME)) {
						sleep(100);
						
						if (mActive) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
				} finally {
					finish();
					startActivity(new Intent(SplashScreenActivity.this, PDFViewerMainActivity.class));
//					stop();
				}
			}
		};
		
		splashThread.start();
	}
}
