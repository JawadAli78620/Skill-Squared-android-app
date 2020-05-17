package com.example.skillsquared2.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.skillsquared2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateServicesTitle extends AppCompatActivity {

    //firebase refrences
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String activityIntent = null;
    ProgressDialog pd;
    ArrayList<String> categoriesList;
    HashMap<String, ArrayList<String>> subCategoryList;
    HashMap<String, ArrayList<String>> subCategoryChildList;
    //Subcategory Lists
    ArrayList<String> graphics;
    ArrayList<String> marketing;
    ArrayList<String> animation;
    ArrayList<String> programming;
    ArrayList<String> business;
    ArrayList<String> lifestyle;
    ArrayList<String> sublist;
    String uid, email, name, selCategory = "", selSubCategory = "";
    String timeStamp = "";
    private EditText titleEt, descriptionEt;
    private AppCompatSpinner categorySpin, subCategorySpin, subCategoryChildSpin;
    private MaterialCardView nextBtnCv;
    private LinearLayout llSubCatLayoutCreateService, llSubCatChildLayoutCreateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_services_title);


        //get views by id's
        titleEt = findViewById(R.id.serviceTitle_et);
        descriptionEt = findViewById(R.id.etAdditionalInfo);
        categorySpin = findViewById(R.id.spinnerCategoryCreateService);
        subCategorySpin = findViewById(R.id.spinnerSubCategoryCreateService);
        subCategoryChildSpin = findViewById(R.id.spinnerSubCategoryChildCreateService);
        nextBtnCv = findViewById(R.id.submitCreateServiceOne);
        llSubCatLayoutCreateService = findViewById(R.id.llSubCatLayoutCreateService);
        llSubCatChildLayoutCreateService = findViewById(R.id.llSubCatChildLayoutCreateService);

        //initialize Category Arrays
        categoriesList = new ArrayList<>();
        subCategoryList = new HashMap<String, ArrayList<String>>();
        subCategoryChildList = new HashMap<String, ArrayList<String>>();
        sublist = new ArrayList<>();

        //Initialize subcategory arrays
        graphics = new ArrayList<>();
        marketing = new ArrayList<>();
        animation = new ArrayList<>();
        programming = new ArrayList<>();
        business = new ArrayList<>();
        lifestyle = new ArrayList<>();

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

        findViewById(R.id.ivBackCreateServiceOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        populateCategoryArrays();
        arrayAdapter(categoriesList, categorySpin);

        // Category Item Selection Listener
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                closeKeyboard();
                llSubCatLayoutCreateService.setVisibility(View.GONE);
                llSubCatChildLayoutCreateService.setVisibility(View.GONE);
                selCategory = categorySpin.getSelectedItem().toString();
                sublist = subCategoryList.get(selCategory);
                setSubList(sublist);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkUserStatus();

        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    email = "" + ds.child("email").getValue();
                    name = "" + ds.child("name").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nextBtnCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    closeKeyboard();
                    Toast.makeText(CreateServicesTitle.this, "Enter Service Title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    closeKeyboard();
                    Toast.makeText(CreateServicesTitle.this, "Enter Description..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selCategory.equals("Select Category") || selCategory.equals("")) {
                    closeKeyboard();
                    Toast.makeText(CreateServicesTitle.this, "Select Category..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selSubCategory.equals("Select Subcategory") || selSubCategory.equals("")) {
                    closeKeyboard();
                    Toast.makeText(CreateServicesTitle.this, "Select SubCategory..", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    closeKeyboard();
                    uploadData(title, description, selCategory, selSubCategory);

                    Intent intent = new Intent(CreateServicesTitle.this, CreateServicesPricing.class);
                    intent.putExtra("Uid", uid);
                    intent.putExtra("serviceid", timeStamp);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void closeKeyboard() {
        UIUtil.hideKeyboard(this);
    }

    @Override
    public void finish() {
        super.finish();
        // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setSubList(ArrayList<String> list) {
        ArrayList<String> subList = new ArrayList<>();
        subList.add("Select Subcategory");

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                subList.add(list.get(i));
            }
            llSubCatLayoutCreateService.setVisibility(View.VISIBLE);
            arrayAdapter(subList, subCategorySpin);

            subCategorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    closeKeyboard();
                    selSubCategory = subCategorySpin.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

    }

    public void populateCategoryArrays() {

        //Categories
        categoriesList.add("Select Category");
        categoriesList.add("Graphics and Design");
        categoriesList.add("Digital Marketing");
        categoriesList.add("Video and Animation");
        categoriesList.add("Programming");
        categoriesList.add("Business");
        categoriesList.add("Lifestyle");

        //graphics
        graphics.add("Logo Design");
        graphics.add("Game Design");
        graphics.add("Poster Design");
        graphics.add("illustration");
        graphics.add("Book Design");
        graphics.add("Flyer Design");

        //Marketing
        marketing.add("Social Media Advertising");
        marketing.add("SEO");
        marketing.add("Content Marketing");
        marketing.add("Podcast Marketing");
        marketing.add("Email Marketing");
        marketing.add("Web Analytics");

        animation.add("Video Editing");
        animation.add("Video Ads");
        animation.add("Animated GIFS");
        animation.add("Logo Animation");
        animation.add("Visual Effects");
        animation.add("Game Trailers");

        programming.add("Wordpress");
        programming.add("Game Development");
        programming.add("Web");
        programming.add("Mobile Apps");
        programming.add("Desktop Apps");
        programming.add("Databases");

        business.add("Virtual Assistant");
        business.add("Data Entry");
        business.add("Product Research");
        business.add("Management");
        business.add("Financial consultant");
        business.add("Presentations");

        lifestyle.add("Cooking Lessons");
        lifestyle.add("Fitness");
        lifestyle.add("Arts & crafts");
        lifestyle.add("Relationship Advice");
        lifestyle.add("Gaming");
        lifestyle.add("Travelling");

        //subCategories HashMap
        subCategoryList.put("Graphics and Design", new ArrayList<String>());
        subCategoryList.get("Graphics and Design").addAll(graphics);
        subCategoryList.put("Digital Marketing", new ArrayList<String>());
        subCategoryList.get("Digital Marketing").addAll(marketing);
        subCategoryList.put("Video and Animation", new ArrayList<String>());
        subCategoryList.get("Video and Animation").addAll(animation);
        subCategoryList.put("Programming", new ArrayList<String>());
        subCategoryList.get("Programming").addAll(programming);
        subCategoryList.put("Business", new ArrayList<String>());
        subCategoryList.get("Business").addAll(business);
        subCategoryList.put("Lifestyle", new ArrayList<String>());
        subCategoryList.get("Lifestyle").addAll(lifestyle);

    }

    private void uploadData(String title, String description, String category, String subCat) {
        pd.setTitle("Saving information...");
        pd.show();

        timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", email);
        hashMap.put("uid", uid);
        hashMap.put("title", title);
        hashMap.put("category", category);
        hashMap.put("subcategory", subCat);
        hashMap.put("description", description);
        hashMap.put("serviceId", timeStamp);

        DatabaseReference dbRefrence = FirebaseDatabase.getInstance().getReference("Services");
        //dbRefrence.child(uid).child(timeStamp).updateChildren(hashMap);
        dbRefrence.child(timeStamp).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateServicesTitle.this, "Saved Success fully.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateServicesTitle.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
    }

    private void checkUserStatus() {

        if (user != null) {
            email = user.getEmail();
            uid = user.getUid();
        } else {
            // user not signed in
            Toast.makeText(this, "User not Signed in..", Toast.LENGTH_SHORT).show();
        }
    }

    private void arrayAdapter(ArrayList<String> array, @NotNull AppCompatSpinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}

