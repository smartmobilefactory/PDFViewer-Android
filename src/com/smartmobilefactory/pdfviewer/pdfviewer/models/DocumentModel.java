package com.smartmobilefactory.pdfviewer.pdfviewer.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

import android.view.View;

import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.CodecPageInfo;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.DecodeService;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.IViewerActivity;
import com.smartmobilefactory.pdfviewer.pdfviewer.decoding.Page;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageIndex;
import com.smartmobilefactory.pdfviewer.pdfviewer.page.PageType;
import com.smartmobilefactory.pdfviewer.pdfviewer.settings.BookSettings;
import com.smartmobilefactory.pdfviewer.pdfviewer.settings.SettingsManager;
import com.smartmobilefactory.pdfviewer.utils.LengthUtils;

public class DocumentModel extends CurrentPageModel {

    private static final Page[] EMPTY_PAGES = {};

    private DecodeService decodeService;

    private Page[] pages = EMPTY_PAGES;

    public DocumentModel(final DecodeService decodeService) {
        this.decodeService = decodeService;
    }

    public Page[] getPages() {
        return pages;
    }

    public Iterable<Page> getPages(final int start) {
        return new PageIterator(start, pages.length);
    }

    public Iterable<Page> getPages(final int start, final int end) {
        return new PageIterator(start, Math.min(end, pages.length));
    }

    public int getPageCount() {
        return decodeService.getPageCount();
    }

    public DecodeService getDecodeService() {
        return decodeService;
    }

    public void recycle() {
        decodeService.recycle();
        decodeService = null;
        if (LengthUtils.isNotEmpty(pages)) {
            for (final Page page : pages) {
                page.recycle();
            }
        }
        pages = EMPTY_PAGES;
    }

    public Page getPageObject(final int viewIndex) {
        return pages != null && 0 <= viewIndex && viewIndex < pages.length ? pages[viewIndex] : null;
    }

    /**
     * Gets the current page object.
     * 
     * @return the current page object
     */
    public Page getCurrentPageObject() {
        return getPageObject(this.currentIndex.viewIndex);
    }

    /**
     * Gets the next page object.
     * 
     * @return the next page object
     */
    public Page getNextPageObject() {
        return getPageObject(this.currentIndex.viewIndex + 1);
    }

    /**
     * Gets the prev page object.
     * 
     * @return the prev page object
     */
    public Page getPrevPageObject() {
        return getPageObject(this.currentIndex.viewIndex - 1);
    }

    /**
     * Gets the last page object.
     * 
     * @return the last page object
     */
    public Page getLastPageObject() {
        return getPageObject(pages.length - 1);
    }

    public void setCurrentPageByFirstVisible(final int firstVisiblePage) {
        final Page page = getPageObject(firstVisiblePage);
        if (page != null) {
            setCurrentPageIndex(page.index);
        }
    }

    public void initPages(final IViewerActivity base) {
        if (LengthUtils.isNotEmpty(pages)) {
            for (final Page page : pages) {
                page.recycle();
            }
        }
        pages = EMPTY_PAGES;

        final BookSettings bs = SettingsManager.getBookSettings();
        final boolean splitPages = bs.getSplitPages();
        final View view = base.getView();

        final CodecPageInfo defCpi = new CodecPageInfo();
        defCpi.setWidth(view.getWidth());
        defCpi.setHeight(view.getHeight());

        int viewIndex = 0;

        final long start = System.currentTimeMillis();
        try {
            final ArrayList<Page> list = new ArrayList<Page>();
            final CodecPageInfo[] infos = retrievePagesInfo(base, bs);

//            Log.d("Document Model", "Info Length: " + infos.length);
            
            for (int docIndex = 0; docIndex < infos.length; docIndex++) {
                if (!splitPages || infos[docIndex] == null
                        || (infos[docIndex].getWidth() < infos[docIndex].getHeight())) {
                    final Page page = new Page(base, new PageIndex(docIndex, viewIndex++), PageType.FULL_PAGE,
                            infos[docIndex] != null ? infos[docIndex] : defCpi);
                    list.add(page);
                } else {
                    final Page page1 = new Page(base, new PageIndex(docIndex, viewIndex++), PageType.LEFT_PAGE,
                            infos[docIndex]);
                    list.add(page1);
                    final Page page2 = new Page(base, new PageIndex(docIndex, viewIndex++), PageType.RIGHT_PAGE,
                            infos[docIndex]);
                    list.add(page2);
                }
            }
            pages = list.toArray(new Page[list.size()]);
        } finally {
            LCTX.d("Loading page info: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    private CodecPageInfo[] retrievePagesInfo(final IViewerActivity base, final BookSettings bs) {

        final String fileName = bs.getFileName();
        final File cacheDir = base.getContext().getFilesDir();

        final String md5 = md5(fileName);
        final File pagesFile = new File(cacheDir, md5 + ".cache");
        if (md5 != null) {
            if (pagesFile.exists()) {
                final CodecPageInfo[] infos = loadPagesInfo(pagesFile);
                if (infos != null) {
                    return infos;
                }
            }
        }

//        Log.d("DocumentModel", "Page Count: " + getDecodeService().getPageCount());
        
        final CodecPageInfo[] infos = new CodecPageInfo[getDecodeService().getPageCount()];
        for (int i = 0; i < infos.length; i++) {
            infos[i] = getDecodeService().getPageInfo(i);
        }

        if (md5 != null) {
            storePagesInfo(pagesFile, infos);
        }
        return infos;
    }

    private CodecPageInfo[] loadPagesInfo(final File pagesFile) {
        try {
            final DataInputStream in = new DataInputStream(new FileInputStream(pagesFile));
            try {
                final int pages = in.readInt();
//            	final int pages = getPageCount();
                final CodecPageInfo[] infos = new CodecPageInfo[pages];
                for (int i = 0; i < infos.length; i++) {
                    final CodecPageInfo cpi = new CodecPageInfo();
                    cpi.setWidth(in.readInt());
                    cpi.setHeight(in.readInt());
                    if (cpi.getWidth() != -1 && cpi.getHeight() != -1) {
                        infos[i] = cpi;
                    }
                }
                return infos;
            } catch (final EOFException ex) {
                LCTX.e("Loading pages cache failed: " + ex.getMessage());
            } catch (final IOException ex) {
                LCTX.e("Loading pages cache failed: " + ex.getMessage());
            } finally {
                try {
                    in.close();
                } catch (final IOException ex) {
                }
            }
        } catch (final FileNotFoundException ex) {
            LCTX.e("Loading pages cache failed: " + ex.getMessage());
        }
        return null;
    }

    private void storePagesInfo(final File pagesFile, final CodecPageInfo[] infos) {
        try {
            final DataOutputStream out = new DataOutputStream(new FileOutputStream(pagesFile));
            try {
                out.writeInt(infos.length);
                for (int i = 0; i < infos.length; i++) {
                    if (infos[i] != null) {
                        out.writeInt(infos[i].getWidth());
                        out.writeInt(infos[i].getHeight());
                    } else {
                        out.writeInt(-1);
                        out.writeInt(-1);
                    }
                }
            } catch (final IOException ex) {
                LCTX.e("Saving pages cache failed: " + ex.getMessage());
            } finally {
                try {
                    out.close();
                } catch (final IOException ex) {
                }
            }
        } catch (final IOException ex) {
            LCTX.e("Saving pages cache failed: " + ex.getMessage());
        }
    }

    private String md5(final String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            final byte[] a = digest.digest();
            final int len = a.length;
            final StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final class PageIterator implements Iterable<Page>, Iterator<Page> {

        private final int end;
        private int index;

        private PageIterator(final int start, final int end) {
            this.index = start;
            this.end = end;
        }

        @Override
        public boolean hasNext() {
            return 0 <= index && index < end;
        }

        @Override
        public Page next() {
            return hasNext() ? pages[index++] : null;
        }

        @Override
        public void remove() {
        }

        @Override
        public Iterator<Page> iterator() {
            return this;
        }
    }
}
