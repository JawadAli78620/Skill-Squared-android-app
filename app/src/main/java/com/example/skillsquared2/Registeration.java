package com.example.skillsquared2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.HashMap;

public class Registeration extends AppCompatActivity {

    ProgressDialog pd;
    private EditText userNameId;
    private EditText emailId;
    private EditText pswId;
    private EditText confirmPswId;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        userNameId = findViewById(R.id.reg_username);
        emailId = findViewById(R.id.email_field);
        pswId = findViewById(R.id.reg_password_field);
        confirmPswId = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        pd = new ProgressDialog(Registeration.this);
        pd.setTitle("Please Wait...");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                registerButtonPressed();
            }
        });
    }

    public void closeKeyboard() {
        UIUtil.hideKeyboard(this);
    }

    public void registerButtonPressed() {

        final String userName = userNameId.getText().toString();
        String email = emailId.getText().toString();
        String psw = pswId.getText().toString();
        String confPswrd = confirmPswId.getText().toString();

        if (userName.isEmpty()) {
            userNameId.setError("Please Enter your name");
            userNameId.requestFocus();
        } else if (email.isEmpty()) {
            emailId.setError("Please Enter your Email");
            emailId.requestFocus();
        } else if (psw.isEmpty()) {
            pswId.setError("Please Enter a password");
            pswId.requestFocus();
        } else if (confPswrd.isEmpty()) {
            confirmPswId.setError("Please Enter password to confirm");
            confirmPswId.requestFocus();
        } else if (!userName.isEmpty() && !email.isEmpty() && !psw.isEmpty() && !confPswrd.isEmpty()) {
            if (psw.length() < 6) {
                pswId.setError("password must be at least 6 characters long");
                pswId.requestFocus();
            } else if (!psw.equals(confPswrd)) {
                confirmPswId.setError("please enter matching password");
                confirmPswId.requestFocus();
            } else {

                pd.show();
                firebaseAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Registeration.this, "Registered Successfully.", Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String uEmail = user.getEmail();
                                    String uId = user.getUid();

                                    //when user Registered store user info in realtime Database too
                                    HashMap<Object, String> userHashMap = new HashMap<>();
                                    userHashMap.put("email", uEmail);
                                    userHashMap.put("uid", uId);
                                    userHashMap.put("image", "");
                                    userHashMap.put("name", userName);
                                    userHashMap.put("location", "");
                                    userHashMap.put("description", "");
                                    userHashMap.put("language", "");
                                    userHashMap.put("skills", "");
                                    userHashMap.put("links", "");

                                    //path to store user data named "Users"
                                    DatabaseReference reference = firebaseDatabase.getReference("Users");
                                    // put data into database
                                    reference.child(uId).setValue(userHashMap);

                                    pd.dismiss();
                                    startActivity(new Intent(Registeration.this, Login.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    pd.dismiss();
                                    Toast.makeText(Registeration.this, "Registeration Failed, Try Again..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

    }
}
