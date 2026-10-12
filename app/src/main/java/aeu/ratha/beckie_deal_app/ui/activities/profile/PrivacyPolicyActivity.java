package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrivacyPolicyActivity extends BaseActivity {

    private static class PrivacySection {
        String titleEn;
        String titleKm;
        String bodyEn;
        String bodyKm;

        PrivacySection(String titleEn, String titleKm, String bodyEn) {
            this.titleEn = titleEn;
            this.titleKm = titleKm;
            this.bodyEn = bodyEn;
            this.bodyKm = "";
        }

        PrivacySection(String titleEn, String titleKm, String bodyEn, String bodyKm) {
            this.titleEn = titleEn;
            this.titleKm = titleKm;
            this.bodyEn = bodyEn;
            this.bodyKm = bodyKm;
        }
    }

    private String lang = "en";
    private Set<Integer> openSections = new HashSet<>();

    private List<PrivacySection> sections = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Initialize sections
        initializeSections();
        openSections.add(0); // First section opens by default

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.tabKhmer).setOnClickListener(v -> selectLang("km"));
        findViewById(R.id.tabEnglish).setOnClickListener(v -> selectLang("en"));

        selectLang("en");
        setupBottomNavigation();
    }

    private void initializeSections() {
        sections.add(new PrivacySection(
                "What information we collect",
                "ព័ត៌មាននៃប្រើប្រាស់ដែលយើងប្រមូល",
                "We collect the following information when you use Beckie Deal:\n\n• Account: Your name, email address, phone number, and password\n• Delivery: Your delivery address and contact information\n• Orders: Details of items you purchase, quantities, prices, and order status\n• Communications: Messages you send us through the contact form or customer support\n• Device: Your device type, operating system, IP address, and browser information\n• Usage: Pages you visit, products you view, time spent on the app, and search queries\n• Payment: Payment method details (processed securely by our payment partners)\n• Favorites: Your wishlist and bookmarked items"
        ));

        sections.add(new PrivacySection(
                "How we use your information",
                "របៀបដែលយើងប្រើប្រាស់ព័ត៌មាននៃអ្នក",
                "Your information is used for the following purposes:\n\n• Process orders: Fulfill your purchases and deliver items\n• Customer support: Answer your questions and resolve issues\n• Warranty service: Handle warranty claims and repairs\n• Communication: Send order updates, delivery notifications, and support responses\n• Product improvement: Understand how you use the app to improve features\n• Security: Detect fraud and unauthorized access\n• Legal compliance: Meet regulatory and tax requirements in Cambodia\n• Marketing: Send promotional offers (only if you opt-in)\n• Analytics: Understand user behavior and improve user experience"
        ));

        sections.add(new PrivacySection(
                "Who we share your data with",
                "ចំនួនប្រើប្រាស់ដែលយើងចែករំលែកទិន្នន័យ",
                "We share your information only with:\n\n• Delivery partners: To deliver your orders to the specified address\n• Payment processors: To securely process your payments\n• Customer support team: To help resolve your issues\n• Cloud service providers: To store and backup your data securely\n\nWe DO NOT sell, rent, or trade your personal information to third parties. We never share your data with marketing companies or advertisers without your explicit consent."
        ));

        sections.add(new PrivacySection(
                "Data security",
                "សុវត្ថិភាពទិន្នន័យ",
                "We implement multiple security measures to protect your information:\n\n• Encryption: All sensitive data is encrypted using AES-256-GCM encryption\n• HTTPS: All communications between your device and our servers use HTTPS\n• Secure storage: Passwords and tokens are stored securely in encrypted preferences\n• Access control: Only authorized personnel can access your data\n• Regular updates: We regularly update our security systems\n• Monitoring: We monitor for suspicious activities and breaches\n\nHowever, no security system is 100% secure. If you suspect a security breach, please contact us immediately at beckiedeal@gmail.com"
        ));

        sections.add(new PrivacySection(
                "Your rights and choices",
                "សិទ្ធិ និងជម្រើសរបស់អ្នក",
                "You have the following rights regarding your personal information:\n\n• Access: Request a copy of your personal data\n• Correction: Update or correct inaccurate information\n• Deletion: Request deletion of your data (subject to legal obligations)\n• Opt-out: Unsubscribe from marketing communications anytime\n• Portability: Request your data in a portable format\n• Withdraw consent: Withdraw consent for data processing\n\nTo exercise these rights, contact us at beckiedeal@gmail.com with your request. We will respond within 14 days."
        ));

        sections.add(new PrivacySection(
                "Cookies and tracking",
                "ប្រេងឆា និងការតាមដានលម្អិត",
                "The Beckie Deal app uses limited tracking:\n\n• Session management: We store a token to keep you logged in\n• Analytics: We collect usage statistics to improve the app\n• Device ID: We use a unique device identifier for analytics\n• Crash reports: We collect error logs to fix bugs\n\nYou can:\n• Clear app data in your phone settings to clear stored tokens\n• Disable analytics in the app settings (coming soon)\n• Contact us to opt-out of usage tracking\n\nWe do NOT use third-party trackers like Google Analytics or Facebook Pixel in the mobile app."
        ));

        sections.add(new PrivacySection(
                "Children's privacy",
                "ឯកជនភាពកុមារ",
                "Beckie Deal is not intended for children under 18. We do not knowingly collect personal information from children. If we learn that we have collected personal information from a child under 18 without parental consent, we will delete that information immediately. If you believe we have collected information from a child, please contact us at beckiedeal@gmail.com."
        ));

        sections.add(new PrivacySection(
                "Data retention",
                "ការរក្សាទុកទិន្នន័យ",
                "We retain your personal information for the following periods:\n\n• Account information: Until you request deletion or close your account\n• Order history: For 7 years (required by tax law in Cambodia)\n• Warranty records: For the duration of the warranty period\n• Communications: Until resolved, then for 1 year\n• Device/usage data: 90 days (then anonymized for analytics)\n• Payment information: Not stored (processed by secure payment partners)\n\nAfter these periods, information is securely deleted or anonymized."
        ));

        sections.add(new PrivacySection(
                "Third-party links",
                "តំណភ្ជាប់ទៅគេហទំព័ររបស់ភាគីទីបី",
                "The Beckie Deal app may contain links to third-party websites like:\n\n• Google Maps (for store location)\n• Payment provider websites\n• Brand websites (ASUS, Apple, Samsung, etc.)\n\nThis Privacy Policy does not apply to third-party websites. We are not responsible for their privacy practices. Please review their privacy policies before providing your information. When you tap on external links, you are leaving the Beckie Deal app and visiting a different website."
        ));

        sections.add(new PrivacySection(
                "Policy updates",
                "ការធ្វើបច្ចុប្បន្នភាពលក្ខណ្ឌ",
                "We may update this Privacy Policy periodically to reflect changes in our practices or legal requirements. We will notify you by:\n\n• Posting the updated policy in the app\n• Updating the \"Last Updated\" date\n• Sending an email notification for major changes\n• Displaying a prominent notice in the app\n\nYour continued use of the app after changes indicates your acceptance of the updated policy. We encourage you to review this policy regularly."
        ));

        sections.add(new PrivacySection(
                "Contact us",
                "ទំនាក់ទំនងយើងខ្ញុំ",
                "If you have questions about this Privacy Policy or our privacy practices, please contact us:\n\n• Email: beckiedeal@gmail.com\n• Phone: 077 999 776 / 081 999 776\n• Address: Phnom Penh, Cambodia\n• Form: Use the Contact Us form on the About Us page\n\nWe will respond to your inquiry within 14 days. If you are not satisfied with our response, you can lodge a complaint with the relevant data protection authority in Cambodia."
        ));
    }

    private void selectLang(String newLang) {
        lang = newLang;
        boolean km = lang.equals("km");

        findViewById(R.id.tabKhmerLine).setVisibility(km ? View.VISIBLE : View.GONE);
        findViewById(R.id.tabEnglishLine).setVisibility(km ? View.GONE : View.VISIBLE);
        ((TextView) findViewById(R.id.tabKhmerText)).setTextColor(
                ContextCompat.getColor(this, km ? R.color.text_primary : R.color.text_secondary)
        );
        ((TextView) findViewById(R.id.tabEnglishText)).setTextColor(
                ContextCompat.getColor(this, km ? R.color.text_secondary : R.color.text_primary)
        );

        renderSections();
    }

    private void renderSections() {
        LinearLayout container = findViewById(R.id.privacyContainer);
        container.removeAllViews();

        for (int index = 0; index < sections.size(); index++) {
            PrivacySection section = sections.get(index);

            View row = getLayoutInflater().inflate(R.layout.item_privacy_section, container, false);
            TextView body = row.findViewById(R.id.privacyBody);
            TextView icon = row.findViewById(R.id.privacyIcon);

            ((TextView) row.findViewById(R.id.privacyNumber)).setText(String.valueOf(index + 1));
            ((TextView) row.findViewById(R.id.privacyTitle)).setText(
                    lang.equals("km") ? section.titleKm : section.titleEn
            );

            String bodyText = lang.equals("km") && !section.bodyKm.isEmpty() ? section.bodyKm : section.bodyEn;
            body.setText(bodyText);

            final int sectionIndex = index;
            boolean open = openSections.contains(index);
            body.setVisibility(open ? View.VISIBLE : View.GONE);
            icon.setText(open ? "−" : "+");

            row.findViewById(R.id.privacyHeader).setOnClickListener(v -> {
                boolean nowOpen = body.getVisibility() != View.VISIBLE;
                body.setVisibility(nowOpen ? View.VISIBLE : View.GONE);
                icon.setText(nowOpen ? "−" : "+");
                if (nowOpen) {
                    openSections.add(sectionIndex);
                } else {
                    openSections.remove(sectionIndex);
                }
            });

            container.addView(row);
        }
    }

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
    }
}
