package com.websarva.wings.android.signinsample1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    //Firebaseのインスタンスを作る
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String email_p;


    EditText login_email, login_password;
    //パスワードは長くないとダメ


    private static final String TAG = "EmailPassword";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    // [END on_start_check_user]


    private void signInoriginal(String email,String password) {

        int index = email.indexOf(".com");
        String pre_com = email.substring(0, index);
        DatabaseReference reference = database.getReference(pre_com);
        reference.child("password").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.i("ccccccc","cccccc");
                    Toast.makeText(LoginActivity.this, "ログイン失敗",
                            Toast.LENGTH_SHORT).show();
                }

                    else {
                        Log.i("bbbbbbb",String.valueOf(task.getResult().getValue()));
                    Log.i("ddddddddd",password);

                        if (password.equals(String.valueOf(task.getResult().getValue()))){
                            //DatabaseReference references = database.getReference(pre_com+"/password");
                            //references.setValue(password);
                            Intent login_resume = new Intent(LoginActivity.this,Resume.class);
                            login_resume.putExtra("email",email);
                            login_resume.putExtra("password",password);
                            startActivity(login_resume);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                }
            }
    });
    }


    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }

    //ボタンを押したらログイン検証へ。空欄の時に実行しないように注意
    public void loginButton(View view) {
        if ((login_email.getText().toString().equals("") == false) && (login_password.getText().toString().equals("") == false)) {
            signInoriginal(login_email.getText().toString(), login_password.getText().toString());
            Log.i("text", login_email.getText().toString());
        }
    }

}