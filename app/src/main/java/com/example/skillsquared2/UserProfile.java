package com.example.skillsquared2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceGroup;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import static android.app.PendingIntent.getActivity;
import static com.google.firebase.storage.StorageTaskScheduler.getInstance;

public class UserProfile extends AppCompatActivity {

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    //path to store images
    String storagePath = "Users_profile_Imgs/";
    String cameraPermissions[];
    String storagePermissions[];
    //Image uri
    Uri image_uri;
    private ImageView profileIv;
    String language = "", skills = "", links = "", description = "";
    private TextView profNameTv, locationTv, addLinksTv, addLanguageTv, addSkillsTv, addDescTv;
    private TextView linksListTv, langListTv, skillsListTv, descriptionTv;
    private FloatingActionButton editProfFab;
    private EditText descriptionET;
    private MaterialCardView saveBtnCv;
    private RelativeLayout savBtnRL;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    //Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // initialize elements
        init();

        // get details of currently signed in user from database
        getCurrentUserDetails();

        editProfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    private void init() {
        profileIv = findViewById(R.id.create_profile_img);
        profNameTv = findViewById(R.id.create_profile_name);
        addLinksTv = findViewById(R.id.create_profile_addLinks);
        locationTv = findViewById(R.id.create_profile_location);
        addLanguageTv = findViewById(R.id.create_profile_addLang);
        addSkillsTv = findViewById(R.id.create_profile_addSkill);
        editProfFab = findViewById(R.id.edit_profile_fab);
        langListTv = findViewById(R.id.create_profile_lang_list);
        linksListTv = findViewById(R.id.create_profile_links_list);
        skillsListTv = findViewById(R.id.create_profile_skills_list);
        descriptionTv = findViewById(R.id.create_profile_descriptionTV);
        descriptionET = findViewById(R.id.create_profile_descriptionField);
        addDescTv = findViewById(R.id.create_profile_addDesc);
        saveBtnCv = findViewById(R.id.create_profile_saveBtnCV);
        savBtnRL = findViewById(R.id.create_profile_bottomButtonRL);

        //get firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        dbReference = database.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        //init Progress dialogue
        progressDialog = new ProgressDialog(UserProfile.this);

        //init Permissions Arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void getCurrentUserDetails() {

        // get details of currently signed in user from database
        Query query = dbReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get Data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String pImage = "" + ds.child("image").getValue();
                    language = "" + ds.child("language").getValue();
                    String location = "" + ds.child("location").getValue();
                    skills = "" + ds.child("skills").getValue();
                    links = "" + ds.child("links").getValue();
                    description = "" + ds.child("description").getValue();

                    //set Data to UI
                    profNameTv.setText(name);
                    linksListTv.setText(links);
                    langListTv.setText(language);
                    locationTv.setText(location);
                    skillsListTv.setText(skills);
                    descriptionTv.setText(description);


                    try {
                        //if image received, set
                        Picasso.with(UserProfile.this)
                                .load(pImage)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(profileIv);
                        //Picasso.get().load(pImage).into(profileIv);
                    } catch (Exception e) {
                        //default image
                        // Picasso.get().load(R.drawable.ic_launcher_background).into(profileIv);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // This method called when user Allow or Deny from permission request dialogue

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //picking from camera

                if (grantResults.length > 0) {
                    boolean cameraAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    boolean writeStorageAccepted = (grantResults[1] == PackageManager.PERMISSION_GRANTED);

                    if (cameraAccepted && writeStorageAccepted) {
                        //permission enabled
                        pickFromCamera();
                    } else {
                        //Permission denied
                        Toast.makeText(this, "please enable Camera & storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                //picking from gallery

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
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromCamera() {
        //Picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put Image uri
        image_uri = UserProfile.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start Camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
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

                image_uri = data.getData();
                uploadProfilePicture(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                //image_uri = data.getData();
                uploadProfilePicture(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePicture(Uri uri) {

        progressDialog.show();

        //path and Image name to be stored in firebase storage
        String filePathAndName = storagePath + "" + user.getUid();
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
                            dbReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //uri in users database added successfully
                                            progressDialog.dismiss();
                                            Toast.makeText(UserProfile.this, "Image Updated..", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error adding uri in database
                                            progressDialog.dismiss();
                                            Toast.makeText(UserProfile.this, "Error updating Image..", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfile.this, "Some error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditProfileDialog() {

        String[] options = {"Edit profile Image", "Edit User Name", "Change Location", "Edit Other Details"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //profile Image
                    Toast.makeText(UserProfile.this, "Edit Profile selected", Toast.LENGTH_SHORT).show();
                    progressDialog.setMessage("Updating Profile Image");
                    showChooseImageFromDialogue();
                } else if (which == 1) {
                    //Profile Name
                    progressDialog.setMessage("Updating Username");

                    showEditTextDialogue("name");
                } else if (which == 2) {
                    //Location
                    progressDialog.setMessage("Updating Location");
                    showEditTextDialogue("location");
                } else if (which == 3) {
                    //Other Details
                    showEditOptionsInUI();
                }
            }
        });
        builder.create().show();

        //ExampleDialog exampleDialog = new ExampleDialog();
        //exampleDialog.show(getSupportFragmentManager(), "example Dialog");
    }

    @SuppressLint("RestrictedApi")
    private void showEditOptionsInUI() {
        addLanguageTv.setVisibility(View.VISIBLE);
        addSkillsTv.setVisibility(View.VISIBLE);
        addLinksTv.setVisibility(View.VISIBLE);
        addDescTv.setVisibility(View.VISIBLE);
        savBtnRL.setVisibility(View.VISIBLE);
        editProfFab.setVisibility(View.GONE);

        editOtherDetails();
    }

    private void editOtherDetails() {

        // Edit Languages click listener
        addLanguageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialogue("language");
            }
        });

        // Edit Skills click listener
        addSkillsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialogue("skills");
            }
        });

        // Edit Links click Listener

        addLinksTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialogue("links");
            }
        });

        //Edit Description
        addDescTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionTv.setVisibility(View.GONE);
                descriptionET.setVisibility(View.VISIBLE);
                descriptionET.setText(description);
            }
        });

        saveBtnCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String value = descriptionET.getText().toString().trim();
                progressDialog.setTitle("Saving Information...");
                progressDialog.show();
                HashMap<String, Object> result = new HashMap<>();
                result.put("description", value);

                dbReference.child(user.getUid()).updateChildren(result)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onSuccess(Void aVoid) {

                                descriptionTv.setText(value);
                                descriptionET.setVisibility(View.GONE);
                                descriptionTv.setVisibility(View.VISIBLE);

                                editProfFab.setVisibility(View.VISIBLE);
                                savBtnRL.setVisibility(View.GONE);
                                addLanguageTv.setVisibility(View.GONE);
                                addSkillsTv.setVisibility(View.GONE);
                                addLinksTv.setVisibility(View.GONE);
                                addDescTv.setVisibility(View.GONE);

                                progressDialog.dismiss();
                                Toast.makeText(UserProfile.this, "Saved Successfully..", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }


    @SuppressLint("ResourceType")
    private void showEditTextDialogue(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Update " + key);

        //set Layout of dailogue
        LinearLayout layout = new LinearLayout(UserProfile.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText editText = new EditText(UserProfile.this);
        editText.setHint("Enter " + key);
        layout.addView(editText);
        layout.setPadding(10, 10, 10, 10);

        //add in dailogue
        builder.setView(layout);

        //add buttons in dailogue
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String value = editText.getText().toString().trim();

                if (!TextUtils.isEmpty(value)) {
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();

                    if (key.equals("language")) {
                        language += "\n" + value;
                        value = language;
                    }
                    if (key.equals("skills")) {
                        skills += "\n" + value;
                        value = skills;
                    }
                    if (key.equals("links")) {
                        links += "\n" + value;
                        value = links;
                    }
                    result.put(key, value);

                    dbReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserProfile.this, "Updated Successfully..", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(UserProfile.this, "please Enter " + key, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void showChooseImageFromDialogue() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Choose Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //Camera Image
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //Gallery
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

}
