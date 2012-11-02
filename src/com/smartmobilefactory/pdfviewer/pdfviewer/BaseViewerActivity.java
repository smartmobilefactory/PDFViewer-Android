package com.smartmobilefactory.pdfviewer.pdfviewer;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmobilefactory.pdfviewer.R;
import com.smartmobilefactory.pdfviewer.log.LogContext;
import com.smartmobilefactory.pdfviewer.pdfviewer.animation.LinkSinglePageDocumentView;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.DecodeService;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.IDocumentViewController;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.IViewerActivity;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.CurrentPageListener;
import com.smartmobilefactory.pdfviewer.pdfviewer.event.DecodingProgressListener;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.DecodingProgressModel;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.DocumentModel;
import com.smartmobilefactory.pdfviewer.pdfviewer.models.ZoomModel;
import com.smartmobilefactory.pdfviewer.pdfviewer.multitouch.MultiTouchZoom;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageIndex;
import com.smartmobilefactory.pdfviewer.pdfviewer.settings.AppSettings;
import com.smartmobilefactory.pdfviewer.pdfviewer.settings.BookSettings;
import com.smartmobilefactory.pdfviewer.pdfviewer.settings.ISettingsChangeListener;
import com.smartmobilefactory.pdfviewer.pdfviewer.settings.SettingsManager;
import com.smartmobilefactory.pdfviewer.pdfviewer.views.PdfOverlayModel;

public abstract class BaseViewerActivity extends Activity implements IViewerActivity, DecodingProgressListener,
        CurrentPageListener, ISettingsChangeListener {

    public static final LogContext LCTX = LogContext.ROOT.lctx("Core");

    public static final DisplayMetrics DM = new DisplayMetrics();

    private IDocumentViewController documentController;
    private Toast pageNumberToast;

    private ZoomModel zoomModel;
//    private PageViewZoomControls zoomControls;
    private PdfOverlayModel pdfOverlayModel;
    
    private FrameLayout frameLayout;

    private DecodingProgressModel progressModel;

    private MultiTouchZoom multiTouchZoom;

    private DocumentModel documentModel;
    private String currentFilename;
    

    /**
     * Instantiates a new base viewer activity.
     */
    public BaseViewerActivity() {
        super();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindowManager().getDefaultDisplay().getMetrics(DM);

        SettingsManager.addListener(this);

        frameLayout = createMainContainer();

        initActivity();

        initView("");
    }

    @Override
    protected void onDestroy() {
        if (documentModel != null) {
            documentModel.recycle();
            documentModel = null;
        }
        SettingsManager.removeListener(this);
        super.onDestroy();
    }

    private void initActivity() {
        SettingsManager.applyAppSettingsChanges(null, SettingsManager.getAppSettings());
    }

    private void initView(final String password) {
        final DecodeService decodeService = createDecodeService();

        final Uri uri = getIntent().getData();
        try {
        	final String fileName = uri.getPath();
        	
            SettingsManager.init(fileName);

            decodeService.open(fileName, password);

        } catch (final Exception e) {
            LCTX.e(e.getMessage(), e);
            final String msg = e.getMessage();

            if ("PDF needs a password!".equals(msg)) {
                askPassword();
            } else {
                showErrorDlg(msg);
            }
            return;
        }

        documentModel = new DocumentModel(decodeService);

        documentModel.addEventListener(this);

        zoomModel = new ZoomModel();
        pdfOverlayModel = new PdfOverlayModel();

        initMultiTouchZoomIfAvailable();

        progressModel = new DecodingProgressModel();
        progressModel.addEventListener(this);

        SettingsManager.applyBookSettingsChanges(null, SettingsManager.getBookSettings(), null);

        setContentView(frameLayout);
        setProgressBarIndeterminateVisibility(false);
    }

    public PdfOverlayModel getPdfOverlayModel() {
    	return pdfOverlayModel;
    }
    
    private void askPassword() {
        setContentView(R.layout.password);
        final Button ok = (Button) findViewById(R.id.pass_ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final EditText te = (EditText) findViewById(R.id.pass_req);
                initView(te.getText().toString());
            }
        });
        final Button cancel = (Button) findViewById(R.id.pass_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                closeActivity();
            }
        });
    }

    private void showErrorDlg(final String msg) {
        setContentView(R.layout.error);
        final TextView errortext = (TextView) findViewById(R.id.error_text);
        if (msg != null && msg.length() > 0) {
            errortext.setText(msg);
        } else {
            errortext.setText("Unexpected error occured!");
        }
        final Button cancel = (Button) findViewById(R.id.error_close);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                closeActivity();
            }
        });
    }

    private void initMultiTouchZoomIfAvailable() {
        try {
            multiTouchZoom = ((MultiTouchZoom) Class.forName("com.smartmobilefactory.pdfviewer.pdfviewer.multitouch.MultiTouchZoomImpl")
                    .getConstructor(ZoomModel.class).newInstance(zoomModel));
        } catch (final Exception e) {
            System.out.println("Multi touch zoom is not available: " + e);
        }
    }

    @Override
    public void createDocumentView() {
        if (documentController != null) {
            frameLayout.removeView(documentController.getView());
            zoomModel.removeEventListener(documentController);
        }

//        final BookSettings bs = SettingsManager.getBookSettings();

//        if (bs.getSinglePage()) {
//            documentController = new SinglePageDocumentView(this);
        	documentController = new LinkSinglePageDocumentView(this); 
//        } else {
//            documentController = new ContiniousDocumentView(this);
//        }

        zoomModel.addEventListener(documentController);
        documentController.getView().setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        documentController.showDocument();

//        frameLayout.removeView(getZoomControls());
        frameLayout.addView(documentController.getView());
//        frameLayout.addView(getZoomControls());
    }

    @Override
    public void decodingProgressChanged(final int currentlyDecoding) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    setProgressBarIndeterminateVisibility(true);
                    getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
                            currentlyDecoding == 0 ? 10000 : currentlyDecoding);
                } catch (final Throwable e) {
                }
            }
        });
    }

    @Override
    public void currentPageChanged(final PageIndex oldIndex, final PageIndex newIndex) {
        final int pageCount = documentModel.getPageCount();
        String prefix = "";

        if (pageCount > 0) {
            final String pageText = (newIndex.viewIndex + 1) + "/" + pageCount;
//            if (SettingsManager.getAppSettings().getPageInTitle()) {
//                prefix = "(" + pageText + ") ";
//            } else {
                if (pageNumberToast != null) {
                    pageNumberToast.setText(pageText);
                } else {
                    pageNumberToast = Toast.makeText(this, pageText, 300);
                }
                pageNumberToast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
                pageNumberToast.show();
//            }
        }

        getWindow().setTitle(prefix + currentFilename);
        SettingsManager.currentPageChanged(oldIndex, newIndex);
    }

    private void setWindowTitle() {
        currentFilename = getIntent().getData().getLastPathSegment();

        cleanupTitle();

        getWindow().setTitle(currentFilename);
    }

    /**
     * Cleanup title. Remove from title file extension and (...), [...]
     */
    private void cleanupTitle() {
        try {
            currentFilename = currentFilename.substring(0, currentFilename.lastIndexOf('.'));
            currentFilename = currentFilename.replaceAll("\\(.*\\)|\\[.*\\]", "");
        } catch (final IndexOutOfBoundsException e) {

        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setWindowTitle();
        if (documentModel != null) {
            final BookSettings bs = SettingsManager.getBookSettings();
            if (bs != null) {
                currentPageChanged(PageIndex.NULL, bs.getCurrentPage());
            }
        }
    }

//    private PageViewZoomControls getZoomControls() {
//        if (zoomControls == null) {
//            zoomControls = new PageViewZoomControls(this, zoomModel);
//            zoomControls.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
//            zoomModel.addEventListener(zoomControls);
//        }
//        return zoomControls;
//    }

    private FrameLayout createMainContainer() {
        return new FrameLayout(this);
    }

    protected abstract DecodeService createDecodeService();

    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onOptionsMenuClosed(final Menu menu) {
        if (SettingsManager.getAppSettings().getFullScreen()) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * Gets the zoom model.
     *
     * @return the zoom model
     */
    @Override
    public ZoomModel getZoomModel() {
        return zoomModel;
    }

    /**
     * Gets the multi touch zoom.
     *
     * @return the multi touch zoom
     */
    @Override
    public MultiTouchZoom getMultiTouchZoom() {
        return multiTouchZoom;
    }

    @Override
    public DecodeService getDecodeService() {
        return documentModel.getDecodeService();
    }

    /**
     * Gets the decoding progress model.
     *
     * @return the decoding progress model
     */
    @Override
    public DecodingProgressModel getDecodingProgressModel() {
        return progressModel;
    }

    @Override
    public DocumentModel getDocumentModel() {
        return documentModel;
    }

    @Override
    public IDocumentViewController getDocumentController() {
        return documentController;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public View getView() {
        return documentController.getView();
    }

    public FrameLayout getFrameLayout() {
    	return frameLayout;
    }
    
    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            closeActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void closeActivity() {
        SettingsManager.clearCurrentBookSettings();
        finish();
    }

    @Override
    public void onAppSettingsChanged(final AppSettings oldSettings, final AppSettings newSettings,
            final AppSettings.Diff diff) {
        if (diff.isRotationChanged()) {
            setRequestedOrientation(newSettings.getRotation().getOrientation());
        }

        if (diff.isFullScreenChanged()) {
            final Window window = getWindow();
            if (newSettings.getFullScreen()) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        if (diff.isShowTitleChanged() && diff.isFirstTime()) {
            final Window window = getWindow();
            try {
                if (!newSettings.getShowTitle()) {
                    window.requestFeature(Window.FEATURE_NO_TITLE);
                } else {
                    // Android 3.0+ you need both progress!!!
                    window.requestFeature(Window.FEATURE_PROGRESS);
                    window.requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                    setProgressBarIndeterminate(true);
                }
            } catch (final Throwable th) {
                LCTX.e("Error on requestFeature call: " + th.getMessage());
            }
        }
        final IDocumentViewController dc = getDocumentController();
        if (dc != null) {
            if (diff.isKeepScreenOnChanged()) {
                dc.getView().setKeepScreenOn(newSettings.isKeepScreenOn());
            }
        }

    }

    @Override
    public void onBookSettingsChanged(final BookSettings oldSettings, final BookSettings newSettings,
            final BookSettings.Diff diff, final AppSettings.Diff appDiff) {

        boolean redrawn = false;
        if (diff.isSinglePageChanged() || diff.isSplitPagesChanged()) {
            redrawn = true;
            createDocumentView();
        }

        if (diff.isZoomChanged() && diff.isFirstTime()) {
            redrawn = true;
            getZoomModel().setZoom(newSettings.getZoom());
        }

        final IDocumentViewController dc = getDocumentController();
        if (dc != null) {

            if (diff.isPageAlignChanged()) {
                dc.setAlign(newSettings.getPageAlign());
            }

            if (diff.isAnimationTypeChanged()) {
                dc.updateAnimationType();
            }

            if (!redrawn && appDiff != null) {
                if (appDiff.isMaxImageSizeChanged() || appDiff.isPagesInMemoryChanged() || appDiff.isDecodeModeChanged()) {
                    dc.updateMemorySettings();
                }
            }
        }

        final DocumentModel dm = getDocumentModel();
        if (dm != null) {
            currentPageChanged(PageIndex.NULL, dm.getCurrentIndex());
        }
    }

}
