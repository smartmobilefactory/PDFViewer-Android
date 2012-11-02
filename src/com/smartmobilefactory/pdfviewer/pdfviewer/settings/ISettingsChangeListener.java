package com.smartmobilefactory.pdfviewer.pdfviewer.settings;

public interface ISettingsChangeListener {

    void onAppSettingsChanged(AppSettings oldSettings, AppSettings newSettings, AppSettings.Diff diff);

    void onBookSettingsChanged(BookSettings oldSettings, BookSettings newSettings, BookSettings.Diff diff, AppSettings.Diff appDiff);

}
