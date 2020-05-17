package com.example.skillsquared2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    ProgressDialog pd;
    private CardView loginButton;
    private EditText passwordId;
    private EditText userNameId;
    private TextView signUpTv;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_cardView);
        passwordId = findViewById(R.id.password_field);
        userNameId = findViewById(R.id.username_field);
        signUpTv = findViewById(R.id.register_cardView);
        //get Database instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        pd = new ProgressDialog(Login.this);
        pd.setTitle("Please wait...");

        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                startActivity(new Intent(Login.this, Registeration.class));
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                loginPressed();
            }
        });
    }

    public void closeKeyboard() {
        UIUtil.hideKeyboard(this);
    }

    public void loginPressed() {

        String userName = userNameId.getText().toString();
        String password = passwordId.getText().toString();

        if (userName.isEmpty()) {
            userNameId.setError("please Enter Username");
            userNameId.requestFocus();
        } else if (password.isEmpty()) {
            passwordId.setError("please Enter Password");
            passwordId.requestFocus();
        } else if (!userName.isEmpty() && !password.isEmpty()) {
            pd.show();
            firebaseAuth.signInWithEmailAndPassword(userName, password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String uEmail = user.getEmail();
                                    String uId = user.getUid();

                                    //when user Registered store user info in realtime Database too
                                    HashMap<Object, String> userHashMap = new HashMap<>();
                                    userHashMap.put("email", uEmail);
                                    userHashMap.put("uid", uId);
                                    userHashMap.put("image", "");
                                    userHashMap.put("name", "");
                                    userHashMap.put("location", "");
                                    userHashMap.put("language", "");
                                    userHashMap.put("skills", "");

                                    //path to store user data named "Users"
                                    DatabaseReference reference = firebaseDatabase.getReference("Users");
                                    // put data into database
                                    reference.child(uId).setValue(userHashMap);
                                }
                                pd.dismiss();
                                startActivity(new Intent(Login.this, MainActivity.class));
                            } else {
                                pd.dismiss();
                                Toast.makeText(Login.this, "Sign in failed, Please Sign up first.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

}
