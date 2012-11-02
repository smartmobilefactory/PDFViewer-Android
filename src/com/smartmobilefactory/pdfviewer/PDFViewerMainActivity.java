package com.smartmobilefactory.pdfviewer;

import com.smartmobilefactory.pdfviewer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PDFViewerMainActivity extends Activity {

	private static final String PDFVIEWER_PASSWORD = "pdf";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		EditText username_edit = (EditText) findViewById(R.id.edit_user);
		username_edit.setEnabled(false);
		
		final EditText userpass_edit = (EditText) findViewById(R.id.edit_pass);
		userpass_edit.setHint("Passwort: pdf");
		userpass_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				userpass_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}); 
		
		ImageButton btn_impress = (ImageButton) findViewById(R.id.btn_impressum);
		btn_impress.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(PDFViewerMainActivity.this, PDFViewerAboutActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton btn_login = (ImageButton) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (userpass_edit.getText().toString().toLowerCase().equals(PDFVIEWER_PASSWORD)) {
					Intent intent = new Intent(PDFViewerMainActivity.this, BookSelectorActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(PDFViewerMainActivity.this, "Das Passwort ist falsch", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
