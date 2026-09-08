package aeu.ratha.beckie_deal_app.ui.components;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.graphics.Color;

import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.os.Bundle;

import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.view.LayoutInflater;

import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.view.ViewGroup;

import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.widget.LinearLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.widget.SeekBar;

import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

/**
 * A reusable filter bottom sheet with:
 *  - Sort options (Default / Price Low→High / Price High→Low / Top Rated)
 *  - Release Year chips (All + 2010-2026)
 *  - Max price slider ($0–$3,000)
 *
 * Attach a {@link FilterListener} to receive the chosen filter state.
 */
public class FilterBottomSheet extends BottomSheetDialogFragment {

    public static final String SORT_DEFAULT    = "Default";
    public static final String SORT_PRICE_LOW  = "Price: Low → High";
    public static final String SORT_PRICE_HIGH = "Price: High → Low";
    public static final String SORT_TOP_RATED  = "Top Rated";

    public interface FilterListener {
        void onFiltersApplied(String sortBy, String year, int maxPrice);
    }

    private FilterListener listener;

    // Current state
    private String currentSort     = SORT_DEFAULT;
    private String currentYear     = "All";
    private int    currentMaxPrice = 3000;

    // Views
    private TextView sortDefault, sortPriceLow, sortPriceHigh, sortTopRated;
    private LinearLayout yearContainer;
    private SeekBar priceSeekBar;
    private TextView priceRangeLabel;

    public static FilterBottomSheet newInstance(String sort, String year, int maxPrice) {
        FilterBottomSheet sheet = new FilterBottomSheet();
        Bundle args = new Bundle();
        args.putString("sort", sort);
        args.putString("year", year);
        args.putInt("maxPrice", maxPrice);
        sheet.setArguments(args);
        return sheet;
    }

    public void setFilterListener(FilterListener l) {
        this.listener = l;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentSort     = getArguments().getString("sort",     SORT_DEFAULT);
            currentYear     = getArguments().getString("year",     "All");
            currentMaxPrice = getArguments().getInt("maxPrice",    3000);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        sortDefault   = root.findViewById(R.id.sortDefault);
        sortPriceLow  = root.findViewById(R.id.sortPriceLow);
        sortPriceHigh = root.findViewById(R.id.sortPriceHigh);
        sortTopRated  = root.findViewById(R.id.sortTopRated);
        yearContainer = root.findViewById(R.id.yearContainer);
        priceSeekBar  = root.findViewById(R.id.priceSeekBar);
        priceRangeLabel = root.findViewById(R.id.priceRangeLabel);

        setupSortChips();
        setupYearChips();
        setupPriceSlider();

        root.findViewById(R.id.resetButton).setOnClickListener(v -> {
            currentSort = SORT_DEFAULT;
            currentYear = "All";
            currentMaxPrice = 3000;
            setupSortChips();
            setupYearChips();
            priceSeekBar.setProgress(3000);
            priceRangeLabel.setText("$0 – $3,000");
        });

        MaterialButton applyBtn = root.findViewById(R.id.applyButton);
        applyBtn.setOnClickListener(v -> {
            if (listener != null)
                listener.onFiltersApplied(currentSort, currentYear, currentMaxPrice);
            dismiss();
        });

        return root;
    }

    // ── Sort ──────────────────────────────────────────────────────────────────

    private void setupSortChips() {
        refreshSortChip(sortDefault,   SORT_DEFAULT);
        refreshSortChip(sortPriceLow,  SORT_PRICE_LOW);
        refreshSortChip(sortPriceHigh, SORT_PRICE_HIGH);
        refreshSortChip(sortTopRated,  SORT_TOP_RATED);

        sortDefault.setOnClickListener(v   -> selectSort(SORT_DEFAULT));
        sortPriceLow.setOnClickListener(v  -> selectSort(SORT_PRICE_LOW));
        sortPriceHigh.setOnClickListener(v -> selectSort(SORT_PRICE_HIGH));
        sortTopRated.setOnClickListener(v  -> selectSort(SORT_TOP_RATED));
    }

    private void selectSort(String sort) {
        currentSort = sort;
        refreshSortChip(sortDefault,   SORT_DEFAULT);
        refreshSortChip(sortPriceLow,  SORT_PRICE_LOW);
        refreshSortChip(sortPriceHigh, SORT_PRICE_HIGH);
        refreshSortChip(sortTopRated,  SORT_TOP_RATED);
    }

    private void refreshSortChip(TextView chip, String label) {
        boolean sel = label.equals(currentSort);
        chip.setBackground(requireContext().getDrawable(
                sel ? R.drawable.chip_selected : R.drawable.chip_unselected));
        chip.setTextColor(sel ? Color.WHITE : Color.parseColor("#555555"));
    }

    // ── Year ──────────────────────────────────────────────────────────────────

    // Newest year displayed first so users don't have to scroll to the end
    private static final String[] YEARS = {
        "All","2026","2025","2024","2023","2022","2021","2020",
        "2019","2018","2017","2016","2015","2014","2013","2012","2011","2010"
    };

    private void setupYearChips() {
        if (yearContainer == null) return;
        yearContainer.removeAllViews();

        for (String year : YEARS) {
            TextView chip = new TextView(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4, 4, 4, 4);
            chip.setLayoutParams(lp);
            chip.setPadding(dp(14), dp(8), dp(14), dp(8));
            chip.setText(year);
            ScreenUtils.applyTextSize(chip, R.dimen.text_size_body);
            chip.setClickable(true);
            chip.setFocusable(true);

            boolean sel = year.equals(currentYear);
            chip.setBackground(requireContext().getDrawable(
                    sel ? R.drawable.chip_selected : R.drawable.chip_unselected));
            chip.setTextColor(sel ? Color.WHITE : Color.parseColor("#555555"));

            chip.setOnClickListener(v -> {
                currentYear = year;
                setupYearChips();
            });
            yearContainer.addView(chip);
        }
    }

    // ── Price slider ──────────────────────────────────────────────────────────

    private void setupPriceSlider() {
        priceSeekBar.setMax(3000);
        priceSeekBar.setProgress(currentMaxPrice);
        priceRangeLabel.setText("$0 – $" + formatPrice(currentMaxPrice));

        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                // Round to nearest $50
                currentMaxPrice = (progress / 50) * 50;
                if (currentMaxPrice == 0) currentMaxPrice = 50;
                priceRangeLabel.setText("$0 – $" + formatPrice(currentMaxPrice));
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String formatPrice(int price) {
        if (price >= 1000) return String.format("%,d", price);
        return String.valueOf(price);
    }

    private int dp(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
