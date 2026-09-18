package aeu.ratha.beckie_deal_app.ui.fragments.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.checkout.CheckoutActivity;

public class CartReviewFragment extends Fragment {
    private RecyclerView cartItemsRV;
    private TextView subtotalText, totalText;
    private Button continueBtn;
    private CheckoutActivity checkoutActivity;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart_review, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        checkoutActivity = (CheckoutActivity) getActivity();
        
        cartItemsRV = view.findViewById(R.id.cartItemsRV);
        subtotalText = view.findViewById(R.id.subtotalText);
        totalText = view.findViewById(R.id.totalText);
        continueBtn = view.findViewById(R.id.continueBtn);
        
        setupCartItems();
        
        continueBtn.setOnClickListener(v -> {
            if (checkoutActivity != null) {
                checkoutActivity.nextStep();
            }
        });
    }
    
    private void setupCartItems() {
        // TODO: Implement cart items display
        // For now, show placeholder
        subtotalText.setText("Subtotal: $3,168.97");
        totalText.setText("Total: $3,168.97");
    }
}
