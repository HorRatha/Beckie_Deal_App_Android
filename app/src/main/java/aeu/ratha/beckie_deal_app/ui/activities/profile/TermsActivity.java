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

public class TermsActivity extends BaseActivity {

    private static class TermsSection {
        String titleEn;
        String titleKm;
        String bodyEn;
        String bodyKm;

        TermsSection(String titleEn, String titleKm, String bodyEn) {
            this.titleEn = titleEn;
            this.titleKm = titleKm;
            this.bodyEn = bodyEn;
            this.bodyKm = "";
        }

        TermsSection(String titleEn, String titleKm, String bodyEn, String bodyKm) {
            this.titleEn = titleEn;
            this.titleKm = titleKm;
            this.bodyEn = bodyEn;
            this.bodyKm = bodyKm;
        }
    }

    private String lang = "en";
    private Set<Integer> openSections = new HashSet<>();

    private List<TermsSection> sections = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

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
        sections.add(new TermsSection(
                "Acceptance of terms",
                "ការទទួលយកលក្ខខណ្ឌ",
                "By using the Beckie Deal Cambodia app or placing an order, you agree to these Terms of Service. You must be 18 or older to place an order. If you do not agree, please do not use the app."
        ));

        sections.add(new TermsSection(
                "Accounts and security",
                "គណនី និងសុវត្ថិភាព",
                "Keep your login details private and give correct information. You are responsible for all activity under your account. We may suspend accounts that use false information or abuse our services."
        ));

        sections.add(new TermsSection(
                "Products and stock",
                "ផលិតផល និងស្តុក",
                "Product photos, specifications and weights are for reference, and colors or packaging may differ slightly. \"Available In Stock\" is not a reservation until we confirm your order."
        ));

        sections.add(new TermsSection(
                "Prices and payment",
                "តម្លៃ និងការទូទាត់",
                "Prices are shown in USD and may change without notice. The price at order confirmation applies. Payment methods: Cash on Delivery, Bank Transfer, Mobile Money. VAT is not included in the invoice, and the invoice cannot be used for tax declaration."
        ));

        sections.add(new TermsSection(
                "Orders and cancellation",
                "ការបញ្ជាទិញ និងការបោះបង់",
                "An order is accepted when we confirm it by phone, message or in the app. You can cancel before it is prepared for delivery by contacting our sales team. We may cancel an order if the item is out of stock or the price was shown by mistake."
        ));

        sections.add(new TermsSection(
                "Delivery or pickup",
                "ការដឹកជញ្ជូន ឬការទទួលផ្ទាល់",
                "Delivery areas: Phnom Penh and surrounding provinces. Estimated time: 2-3 business days. Delivery fee: Free for orders over $50, otherwise $5. Please check the item in front of the delivery person and report any problem right away."
        ));

        sections.add(new TermsSection(
                "Returns and refunds",
                "ការប្តូរ និងការសងប្រាក់វិញ",
                "Goods are sold with no refund and return. Exception: if you receive a wrong or defective item, contact us within 3 days with your invoice and a photo or unboxing video. We will repair, replace or refund the item."
        ));

        sections.add(new TermsSection(
                "Warranty",
                "ការធានា",
                "Warranty covers parts for the specified period. There is no warranty on adapters, or on items that are lost, broken, damaged, burned, dropped, water-damaged or missing the Warranty Seal. ASUS/MSI Cambodia products follow the company's terms. Apple devices with an iCloud lock void the warranty return. Gaming laptops (GPU) have no warranty on the screen and adapter. Warranty repair can take 2 to 6 weeks."
        ));

        sections.add(new TermsSection(
                "Free and bonus items",
                "ទំនិញថែមជូន",
                "Extra free items are a gift. They are not under warranty and cannot be exchanged for cash or other items."
        ));

        sections.add(new TermsSection(
                "Privacy and personal data",
                "ឯកជនភាព និងទិន្នន័យផ្ទាល់ខ្លួន",
                "We collect your name, phone number, email, delivery address and the messages you send us, to process orders and provide support and warranty service. We do not sell your data. See our Privacy Policy for details."
        ));

        sections.add(new TermsSection(
                "Limitation of liability",
                "ការកំណត់ការទទួលខុសត្រូវ",
                "As far as the law allows, our liability is limited to the price of the product you bought. We are not responsible for delays or losses caused by couriers, payment providers, internet problems or events outside our control."
        ));

        sections.add(new TermsSection(
                "Governing law and disputes",
                "ច្បាប់ដែលអនុវត្ត និងការដោះស្រាយវិវាទ",
                "These terms follow the laws of the Kingdom of Cambodia. We will try to solve any dispute by talking first. If it cannot be solved, the courts of Phnom Penh will handle it."
        ));

        sections.add(new TermsSection(
                "Contact",
                "ទំនាក់ទំនង",
                "Sales: 077 999 776 / 081 999 776. You can also use the Contact Us form on the About Us page. Address: Phnom Penh, Cambodia."
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
        LinearLayout container = findViewById(R.id.termsContainer);
        container.removeAllViews();

        for (int index = 0; index < sections.size(); index++) {
            TermsSection section = sections.get(index);

            View row = getLayoutInflater().inflate(R.layout.item_terms_section, container, false);
            TextView body = row.findViewById(R.id.termBody);
            TextView icon = row.findViewById(R.id.termIcon);

            ((TextView) row.findViewById(R.id.termNumber)).setText(String.valueOf(index + 1));
            ((TextView) row.findViewById(R.id.termTitle)).setText(
                    lang.equals("km") ? section.titleKm : section.titleEn
            );

            String bodyText = lang.equals("km") && !section.bodyKm.isEmpty() ? section.bodyKm : section.bodyEn;
            body.setText(bodyText);

            final int sectionIndex = index;
            boolean open = openSections.contains(index);
            body.setVisibility(open ? View.VISIBLE : View.GONE);
            icon.setText(open ? "−" : "+");

            row.findViewById(R.id.termHeader).setOnClickListener(v -> {
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
