package com.smartmobilefactory.pdfviewer.pdfviewer;

import java.util.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.smartmobilefactory.pdfviewer.pdfviewer.animation.LinkSinglePageDocumentView;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.DecodeService;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.DecodeServiceBase;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageAlign;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageIndex;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageLink;
import com.smartmobilefactory.pdfviewer.pdfviewer.views.PdfOverlayView;

public class PdfViewerActivity extends BaseViewerActivity {

    private PdfOverlayView pdfOverlayView;
    private RelativeLayout layout;
    private SharedPreferences sharedPrefs;
    private int tocPage = 0;
    private int currentContentPage = 0;
    private int beginContentPage = 0;
    private int endContentPage = 0;
    private int beginSourcePage = 0;
    private int endSourcePage = 0;
	
    @Override
    protected DecodeService createDecodeService() {
        return new DecodeServiceBase(new PdfContext());
    }
    
    @Override
    public void createDocumentView() {
    	super.createDocumentView();
    	layout = new RelativeLayout(this);
    	sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	getFrameLayout().addView(getPdfOverlay());
    	getFrameLayout().addView(layout);
    	getDocumentController().setAlign(PageAlign.WIDTH);
    	getPdfOverlayModel().togglePdfOverlay();
//    	((LinkSinglePageDocumentView) getDocumentController()).setCurrentSite(sharedPrefs.getInt("page_number", 0));
    }
    
    private PdfOverlayView getPdfOverlay() {
    	if (pdfOverlayView == null) {
    		int overlayColor = getIntent().getIntExtra("color", PdfOverlayView.BLUE);
    		
    		switch (overlayColor) {
			case PdfOverlayView.BLUE:
	    		pdfOverlayView = new PdfOverlayView(this, PdfOverlayView.BLUE);
	    		initButtons(6, 7, 31, 38);
	    		
	    		firstOpenCheck(PdfOverlayView.BLUE);
	    		
				break;
			case PdfOverlayView.PURPLE:
				pdfOverlayView = new PdfOverlayView(this, PdfOverlayView.PURPLE);
				initButtons(7, 8, 29, 31);

				firstOpenCheck(PdfOverlayView.PURPLE);
				
				break;
			case PdfOverlayView.BLUE_BOOK_2:
				pdfOverlayView = new PdfOverlayView(this, PdfOverlayView.BLUE_BOOK_2);
				initButtons(3, 5, 15, 15);
				
				firstOpenCheck(PdfOverlayView.BLUE_BOOK_2);
				
				break;
			}
    		
    		pdfOverlayView.getNaviBackButton().setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					finish();
				}
			});
    		
    		getPdfOverlayModel().addEventListener(pdfOverlayView);
    	}
    	
    	return pdfOverlayView;
    }

	private void firstOpenCheck(int bookId) {
		
		if (sharedPrefs.getBoolean("book" + bookId, false)) {
			((LinkSinglePageDocumentView) getDocumentController()).setCurrentSite(tocPage);
		} else {
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putBoolean("book" + bookId, true);
			editor.commit();
		}
	}
    
	private void initButtons(final int pageOverview, final int pageContent, final int pageSourceBegin, final int pageSourceEnd) {
		
		tocPage = pageOverview - 1;
		beginContentPage = pageContent - 1;
		endContentPage = pageSourceBegin;
		currentContentPage = beginContentPage;
		beginSourcePage = pageSourceBegin - 1;
		endSourcePage = pageSourceEnd;
		
		pdfOverlayView.getOverviewButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				getDocumentController().goToPage(pageOverview - 1);
			}
		});
			
		pdfOverlayView.getContentButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				getDocumentController().goToPage(currentContentPage);
			}
		});
		
		pdfOverlayView.getSourceButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				getDocumentController().goToPage(pageSourceBegin - 1);		
			}
		});
	}
	
	@Override
	public void currentPageChanged(PageIndex oldIndex, PageIndex newIndex) {
		super.currentPageChanged(oldIndex, newIndex);

		if (getDocumentController() instanceof LinkSinglePageDocumentView) {
			LinkSinglePageDocumentView pageView = (LinkSinglePageDocumentView) getDocumentController();			
			List<PageLink> pageLinks = getDocumentModel().getDecodeService().getPageLinks(newIndex.viewIndex + 1);
			pageView.setPageLinks(pageLinks);
		}
		
		if ((newIndex.viewIndex + 1) > beginContentPage && (newIndex.viewIndex + 1) < endContentPage) {
			currentContentPage = newIndex.viewIndex;
			pdfOverlayView.getContentButton().setChecked(true);
		}
		
		if ((newIndex.viewIndex) == tocPage) {
			pdfOverlayView.getOverviewButton().setChecked(true);
		}
		
		if ((newIndex.viewIndex + 1) > beginSourcePage && (newIndex.viewIndex + 1) < endSourcePage + 1) {
			pdfOverlayView.getSourceButton().setChecked(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getPdfOverlayModel().togglePdfOverlay();
		return false;
	}
}
