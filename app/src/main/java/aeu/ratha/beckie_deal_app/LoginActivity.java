package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent();
        setContentView(R.layout.activity_login);

        setupTopBar();
        setupForm();
        setupEntranceAnimation();
    }

    /** Fades the header wave in, then staggers every form row up from below. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.loginHeaderWave), 0);

        View formSection = findViewById(R.id.loginFormSection);
        if (formSection instanceof android.view.ViewGroup) {
            UiAnimations.staggerChildrenIn((android.view.ViewGroup) formSection, 150L);
        }
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        UiAnimations.pressBounce(backButton);
    }

    private void setupForm() {
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        UiAnimations.pressBounce(loginButton, registerLink, forgotPassword);

        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (registerLink != null) {
            registerLink.setOnClickListener(v -> {
                startActivity(new Intent(this, RegisterActivity.class));
            });
        }

        if (forgotPassword != null) {
            forgotPassword.setOnClickListener(v -> 
                Toast.makeText(this, "Redirecting to password reset...", Toast.LENGTH_SHORT).show());
        }
    }
}
