package com.websarva.wings.android.signinsample1;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void gotoCreate(View view){//アカウント作成の画面へ
        Intent gotoCreate  = new Intent(MainActivity.this,CreateAccount.class);
        startActivity(gotoCreate);
    }

    public void gotoLogin(View view){//ログインの画面へ
        Intent gotoLogin  = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(gotoLogin);
    }



}