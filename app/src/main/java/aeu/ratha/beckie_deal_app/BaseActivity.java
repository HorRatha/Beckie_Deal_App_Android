package aeu.ratha.beckie_deal_app;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Base activity that properly handles uiMode config changes.
 *
 * Because every activity declares android:configChanges="uiMode" in the
 * manifest, Android won't recreate them on night-mode switches. Instead it
 * calls onConfigurationChanged(). We respond by calling
 * getDelegate().applyDayNight() which re-applies the correct theme colours
 * in-place, then recreate() so layouts and drawables refresh correctly.
 *
 * recreate() is safe here because it's called exactly once per real user
 * toggle — it is NOT triggered by the config-change callback itself.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Re-apply day/night theme resources then recreate the activity so
        // all views, drawables and colours pick up the new theme.
        getDelegate().applyDayNight();
        recreate();
    }
}
