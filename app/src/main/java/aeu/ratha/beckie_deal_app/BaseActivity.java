package aeu.ratha.beckie_deal_app;

import android.content.res.Configuration;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Base activity that properly handles uiMode config changes.
 *
 * Because every activity declares android:configChanges="uiMode" in the
 * manifest, Android won't recreate them on night-mode switches. Instead it
 * calls onConfigurationChanged(). We respond by calling recreate() exactly
 * once using a flag guard to prevent infinite recreation loops.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private boolean isRecreating = false;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isRecreating) {
            isRecreating = true;
            getDelegate().applyDayNight();
            recreate();
        }
    }
}
