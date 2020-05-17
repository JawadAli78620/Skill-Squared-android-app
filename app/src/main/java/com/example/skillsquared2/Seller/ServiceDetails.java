package com.example.skillsquared2.Seller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.skillsquared2.R;
import com.squareup.picasso.Picasso;

public class ServiceDetails extends AppCompatActivity {

    ImageView backArrow, serviceIv;
    TextView titleTv, descriptionTv, priceTv, deliveryTimeTv;
    RatingBar ratingBar;

    String title, description, rating, price, deliveryTime, serviceId, serviceImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        backArrow = findViewById(R.id.ivBackServiceDetail);
        serviceIv = findViewById(R.id.ivServiceDetail);
        titleTv = findViewById(R.id.tvServiceDetailTitle);
        descriptionTv = findViewById(R.id.tvServiceDetailDescription);
        priceTv = findViewById(R.id.tvPriceServiceDetail);
        deliveryTimeTv = findViewById(R.id.tvDeliveryTimeServiceDetail);
        ratingBar = findViewById(R.id.rbRatingServiceDetail);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setServiceDetails();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void getStringExtras() {

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        price = getIntent().getStringExtra("price");
        deliveryTime = getIntent().getStringExtra("deliveryTime");
        serviceId = getIntent().getStringExtra("serviceId");
        serviceImage = getIntent().getStringExtra("mainImage");
        rating = getIntent().getStringExtra("rating");
    }

    public void setServiceDetails() {

        getStringExtras();
        Picasso.with(this)
                .load(serviceImage)
                .placeholder(R.drawable.noimg)
                .into(serviceIv);

        titleTv.setText(title);
        descriptionTv.setText(description);
        priceTv.setText(price);
        deliveryTimeTv.setText(deliveryTime);
    }
}
