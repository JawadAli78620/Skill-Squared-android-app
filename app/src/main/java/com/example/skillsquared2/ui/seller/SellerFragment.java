package com.example.skillsquared2.ui.seller;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skillsquared2.R;
import com.example.skillsquared2.Seller.CreateServicesTitle;
import com.example.skillsquared2.Seller.ManageServices;

public class SellerFragment extends Fragment {

    private SellerViewModel sellerViewModel;

    //get UI elements
    private CardView createServiceCv,
            manageServicesCv,
            buyerRequestCv,
            earningsCv,
            analyticsCv,
            ordersCv;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sellerViewModel = ViewModelProviders.of(this).get(SellerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_seller, container, false);

        //initialize seller components
        createServiceCv = root.findViewById(R.id.create_services_cV);
        manageServicesCv = root.findViewById(R.id.manage_services_cV);
        buyerRequestCv = root.findViewById(R.id.buyerRequests_cV);
        earningsCv = root.findViewById(R.id.earnings_cV);
        analyticsCv = root.findViewById(R.id.analytics_cV);
        ordersCv = root.findViewById(R.id.orders_cV);

        /*
            called when clicked on create Services card
         */
        createServiceCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), CreateServicesTitle.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //ApplicationHandler.intent(CreateServicesTitle.class);
            }
        });

        /*
            called when clicked on manage Services card
         */
        manageServicesCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "create Services Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), ManageServices.class));
            }
        });
        return root;
    }
}
