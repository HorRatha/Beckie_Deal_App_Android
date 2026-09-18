package aeu.ratha.beckie_deal_app.ui.fragments.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.checkout.CheckoutActivity;

public class ShippingAddressFragment extends Fragment {
    private EditText fullNameInput, emailInput, addressInput, cityInput, stateInput, zipInput;
    private Button backBtn, continueBtn;
    private CheckoutActivity checkoutActivity;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipping_address, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        checkoutActivity = (CheckoutActivity) getActivity();
        
        fullNameInput = view.findViewById(R.id.fullNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        addressInput = view.findViewById(R.id.addressInput);
        cityInput = view.findViewById(R.id.cityInput);
        stateInput = view.findViewById(R.id.stateInput);
        zipInput = view.findViewById(R.id.zipInput);
        backBtn = view.findViewById(R.id.backBtn);
        continueBtn = view.findViewById(R.id.continueBtn);
        
        backBtn.setOnClickListener(v -> {
            if (checkoutActivity != null) {
                checkoutActivity.previousStep();
            }
        });
        
        continueBtn.setOnClickListener(v -> {
            if (validateAddress()) {
                if (checkoutActivity != null) {
                    checkoutActivity.nextStep();
                }
            }
        });
    }
    
    private boolean validateAddress() {
        // TODO: Add proper validation
        return true;
    }
}
