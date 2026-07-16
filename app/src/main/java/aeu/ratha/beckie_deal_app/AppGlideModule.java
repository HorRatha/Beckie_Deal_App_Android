package aeu.ratha.beckie_deal_app;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

/**
 * App-wide Glide configuration.
 *
 * Applies lighter decode defaults to EVERY image load in the app (product
 * cards, banners, cart, categories, order thumbnails, etc.) without having to
 * change each call site.
 *
 *  - PREFER_RGB_565 halves the memory & CPU cost of decoding vs ARGB_8888,
 *    which keeps heavy image decoding from blocking the main thread and
 *    starving UI animations (this was causing the "skipped frames" jank that
 *    made the navigation drawer stutter / snap shut).
 *  - dontAnimate() removes per-load crossfade work.
 */
@GlideModule
public final class AppGlideModule extends com.bumptech.glide.module.AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .dontAnimate()
        );
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
