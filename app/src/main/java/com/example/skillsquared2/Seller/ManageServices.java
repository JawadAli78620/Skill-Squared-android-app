package com.example.skillsquared2.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.skillsquared2.Adapters.ManageServiceAdapter;
import com.example.skillsquared2.R;
import com.example.skillsquared2.UserProfile;
import com.example.skillsquared2.models.postAJob.manageServices.ActiveGigs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ManageServices extends AppCompatActivity {

    String storagePath = "Users_Services_Banner_Imgs/";
    String uid, email, userName;
    String title, description, category, subcategory, price, deliveryTime, serviceId, serviceImage, requirements;
    ArrayList<ActiveGigs> serviceData;
    ProgressDialog pd;
    private RecyclerView manageServicesRv;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ImageView closeServiceIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        closeServiceIv = findViewById(R.id.ivCloseManageService);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);
        getServiceDatafromFirebase();
    }


    public void getServiceDatafromFirebase() {
        pd.setTitle("Please Wait...");
        pd.show();

        getCurrentUserInfo();

        DatabaseReference dbReferance = FirebaseDatabase.getInstance().getReference("Services");
        Query query = dbReferance.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                serviceData = new ArrayList<ActiveGigs>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ActiveGigs gigData = new ActiveGigs();

                    gigData.setServiceTitle("" + ds.child("title").getValue());
                    gigData.setServiceImage("" + ds.child("image").getValue());
                    gigData.setCategory("" + ds.child("category").getValue());
                    gigData.setSubCategory("" + ds.child("subcategory").getValue());
                    gigData.setDescription("" + ds.child("description").getValue());
                    gigData.setDeliveryTime("" + ds.child("delivery_Time").getValue());
                    gigData.setRequirements("" + ds.child("requirements").getValue());
                    gigData.setServiceId("" + ds.child("serviceId").getValue());
                    serviceData.add(gigData);

                    System.out.println("ttttttttttttttttttttttttttttt " + title + serviceData.size());
                }

                manageServicesRv = findViewById(R.id.recyclerViewManageServices);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                manageServicesRv.setLayoutManager(linearLayoutManager);
                ManageServiceAdapter serviceAdapter = new ManageServiceAdapter(serviceData, ManageServices.this);
                manageServicesRv.setAdapter(serviceAdapter);

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                Log.e("Firebase Error: ", String.valueOf(databaseError));
                Toast.makeText(ManageServices.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCurrentUserInfo() {
        uid = user.getUid();
        email = user.getEmail();

        Query query = databaseReference.orderByChild("email").equalTo(email);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    email = "" + ds.child("email").getValue();
                    userName = "" + ds.child("name").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
