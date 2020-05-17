package com.example.skillsquared2.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.skillsquared2.R;
import com.example.skillsquared2.UserProfile;
import com.example.skillsquared2.ui.seller.SellerFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.HashMap;

public class CreateServicesPricing extends AppCompatActivity {


    //Storage Permissions Constants
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    //Firebase instances
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storagePermissions[];
    ProgressDialog pd;
    String uid, price, deliveryTime, requirements = "", serviceId = "";
    Uri bannerUri;
    String storagePath = "Users_Services_Banner_Imgs/";
    private EditText priceEt, deliveryEt, bannerEt, requirementsEt;
    private MaterialCardView publishBtn;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_services_pricing);

        priceEt = findViewById(R.id.etPriceCreateService);
        deliveryEt = findViewById(R.id.etDeliveryTimeCreateService);
        bannerEt = findViewById(R.id.etServiceBannerCreateService);
        requirementsEt = findViewById(R.id.etRequirementsCreateService);
        publishBtn = findViewById(R.id.publishCreateService);
        backArrow = findViewById(R.id.ivBackCreateService);

        //Firebase Get instances
        databaseReference = FirebaseDatabase.getInstance().getReference("Services");
        storageReference = FirebaseStorage.getInstance().getReference();

        //Init permissions Arrays
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);
        uid = getIntent().getStringExtra("Uid");
        serviceId = getIntent().getStringExtra("serviceid");

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bannerEt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                closeKeyboard();
                chooseFile();
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();
                price = priceEt.getText().toString();
                deliveryTime = deliveryEt.getText().toString();
                requirements = requirementsEt.getText().toString();

                if (TextUtils.isEmpty(price)) {
                    closeKeyboard();
                    Toast.makeText(CreateServicesPricing.this, "Enter Service Price...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(deliveryTime)) {
                    closeKeyboard();
                    Toast.makeText(CreateServicesPricing.this, "Please Set Delivery Time..", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    uploadData(price, deliveryTime, requirements);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void uploadData(String price, String delTime, String requirment) {
        pd.setTitle("Publishing Your Service...");
        pd.show();

        //for service-id, publish time
        //String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("price", price);
        hashMap.put("delivery_Time", delTime);
        hashMap.put("requirements", requirment);

        databaseReference.child(serviceId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateServicesPricing.this, "Published Successfully.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateServicesPricing.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
    }

    public void closeKeyboard() {
        UIUtil.hideKeyboard(this);
    }

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(CreateServicesPricing.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // This method called when user Allow or Deny from permission request dialogue

        if (grantResults.length > 0) {
            boolean writeStorageAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);

            if (writeStorageAccepted) {
                //permission enabled
                pickFromGallery();
            } else {
                //Permission denied
                Toast.makeText(this, "please enable Camera & storage Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromGallery() {
        //pick from Gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // This method will be called after picking image from camera or Gallery
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                bannerUri = data.getData();
                uploadBannerPicture(bannerUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadBannerPicture(Uri uri) {
        pd.setTitle("Uploading Image...");
        pd.show();

        //path and Image name to be stored in firebase storage
        String filePathAndName = storagePath + "" + uid;
        StorageReference storageReference2 = storageReference.child(filePathAndName);
        storageReference2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and uri is received
                        if (uriTask.isSuccessful()) {
                            //image Uploaded
                            //add/update uri in user's database
                            HashMap<String, Object> results = new HashMap<>();
                            results.put("image", downloadUri.toString());
                            databaseReference.child(serviceId).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //uri in users database added successfully
                                            pd.dismiss();
                                            Toast.makeText(CreateServicesPricing.this, "Image Uploaded Successfully..", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error adding uri in database
                                            pd.dismiss();
                                            Toast.makeText(CreateServicesPricing.this, "Error Uploading Image..", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        } else {
                            pd.dismiss();
                            Toast.makeText(CreateServicesPricing.this, "Some error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CreateServicesPricing.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void chooseFile() {

        if (!checkStoragePermission()) {
            requestStoragePermission();
        } else {
            pickFromGallery();
        }
    }


}
