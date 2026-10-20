package aeu.ratha.beckie_deal_app;

import android.app.Application;

import aeu.ratha.beckie_deal_app.utils.preferences.ThemePreferences;

public class BeckieDealApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Apply saved dark/light mode preference as soon as the app process starts
        // This ensures that when MainActivity launches, it already has the correct theme
        ThemePreferences.applySavedTheme(this);
    }
}
