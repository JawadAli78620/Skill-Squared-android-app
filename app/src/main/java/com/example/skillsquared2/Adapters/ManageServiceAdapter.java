package com.example.skillsquared2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillsquared2.R;
import com.example.skillsquared2.GigDetails;
import com.example.skillsquared2.Seller.ServiceDetails;
import com.example.skillsquared2.models.postAJob.manageServices.ActiveGigs;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ManageServiceAdapter extends RecyclerView.Adapter<ManageServiceAdapter.ManageServiceViewHolder> {

    ArrayList<ActiveGigs> serviceList;
    Activity activity;


    public ManageServiceAdapter(ArrayList<ActiveGigs> list, Activity activity) {
        this.serviceList = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ManageServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_service_item, parent, false);
        return new ManageServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageServiceViewHolder holder, int position) {

        final ActiveGigs item = serviceList.get(position);

        Picasso.with(activity)
                .load(item.getServiceImage())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.manageServiceIv);

        holder.serviceTitleTv.setText(String.valueOf(item.getServiceTitle()));
        holder.clicksTv.setText(String.valueOf(item.getTotalClicks()));
        holder.ordersTv.setText(String.valueOf(item.getTotalOrders()));

        holder.serviceCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ServiceDetails.class);
                intent.putExtra("serviceId", item.getServiceId());
                intent.putExtra("mainImage", item.getServiceImage());
                intent.putExtra("title", item.getServiceTitle());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("deliveryTime", item.getDeliveryTime());
                intent.putExtra("rating", item.getRating());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    public class ManageServiceViewHolder extends RecyclerView.ViewHolder {

        ImageView manageServiceIv;
        TextView serviceTitleTv, clicksTv, ordersTv;
        MaterialCardView serviceCv;

        public ManageServiceViewHolder(View itemView) {
            super(itemView);
            //Toast.makeText(mContext, "MY View Holder",Toast.LENGTH_LONG).show();
            manageServiceIv = itemView.findViewById(R.id.ivMainImageManageService);
            serviceTitleTv = itemView.findViewById(R.id.tvHeadingBoldManageService);
            clicksTv = itemView.findViewById(R.id.tvClicksManageService);
            ordersTv = itemView.findViewById(R.id.tvOrderManageService);
            serviceCv = itemView.findViewById(R.id.cardViewService);
        }
    }
}
