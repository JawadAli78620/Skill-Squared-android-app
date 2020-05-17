package com.example.skillsquared2.ui.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.skillsquared2.R;
import com.example.skillsquared2.buyer.PostJob;
import com.example.skillsquared2.buyer.ViewPostedJobs;

public class BuyerFragment extends Fragment {

    private BuyerViewModel buyerViewModel;

    private CardView postAjobCv, browseJobsCv, manageOrdersCv;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buyerViewModel = ViewModelProviders.of(this).get(BuyerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_buyer, container, false);

        postAjobCv = root.findViewById(R.id.post_a_job_card);
        browseJobsCv = root.findViewById(R.id.browse_jobs_card);
        manageOrdersCv = root.findViewById(R.id.manage_orders_Card);

        postAjobCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PostJob.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        browseJobsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ViewPostedJobs.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return root;
    }
}