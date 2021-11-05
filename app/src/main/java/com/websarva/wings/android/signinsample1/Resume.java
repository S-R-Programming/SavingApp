package com.websarva.wings.android.signinsample1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Resume extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private TextView sum_text,price,what;
    private String email,pre_com,password;
    private RecyclerView resume_scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("resume","resume_on");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        resume_scroll = findViewById(R.id.list);
        LinearLayoutManager layout = new LinearLayoutManager(Resume.this);
        resume_scroll.setLayoutManager(layout);
        RecyclerView.ItemDecoration decorator = new DividerItemDecoration(Resume.this,DividerItemDecoration.HORIZONTAL);
        resume_scroll.addItemDecoration(decorator);
        sum_text = findViewById(R.id.sum);
        price = findViewById(R.id.price);
        what = findViewById(R.id.what);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        int index = email.indexOf(".com");
        pre_com = email.substring(0, index);//Firebaseでは「.」がダメなので、「.com」を消す
        DatabaseReference reference_email = database.getReference(pre_com+ "/email");
        reference_email.setValue(email);
        DatabaseReference reference_password = database.getReference(pre_com+ "/password");
        reference_password.setValue(password);
        setup();
    }
    /**
     * Firebaseのデータベースからデータ(detailとsum)を取得する。下にリストとして表示するため
     */
    private void setup() {
        //emailの下の月の下のdetailを指定
        setSum();
        DatabaseReference reference = database.getReference(pre_com+"/detail");
        Query query = reference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override//setupとしてだけでなく、データの更新もしてくれる
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> resumeList = new ArrayList<>();
                //StringBuilder sb = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//detailの中身を全てget
                    String key = snapshot.getKey();
                    resumeList.add(key+"円");
                  //  sb.append(key+"円");
                    //sb.append("\n");
                }
                Collections.reverse(resumeList);
                //Log.i("ttttttttttt",sb.toString());
                //resume_scroll.setText(sb.toString());
                // アダプタオブジェクトを生成。
                RecyclerListAdapter adapter = new RecyclerListAdapter(resumeList);
                // RecyclerViewにアダプタオブジェクトを設定。
                resume_scroll.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private String getMonth() {

        CharSequence charSequence = android.text.format.DateFormat.format("yyyy-MM", Calendar.getInstance());
        return charSequence.toString();
    }

    private String getDay() {

        CharSequence charSequence = android.text.format.DateFormat.format("MM月dd日", Calendar.getInstance());
        return charSequence.toString();
    }

    private String getTime() {

        CharSequence charSequence = android.text.format.DateFormat.format("HH:mm", Calendar.getInstance());
        return charSequence.toString();
    }

    public void setSum(){

        //1番初めにをsumテキストを設定
        DatabaseReference reference_sum = database.getReference(pre_com);
        reference_sum.child("sum").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.i("ccccccc","cccccc");
                }
                else  if(task.getResult().getValue()==null){
                    //nullの時は1番初めなので0円を
                    sum_text.setText("0円");

                    Log.i("iiiiiiii","iiiii");
                }

                else {
                    //すでにsumがある場合、それをセット
                    Log.i("bbbbbbb",String.valueOf(task.getResult().getValue()));
                    String sum_t = String.valueOf(task.getResult().getValue());
                    sum_text.setText(sum_t+"円");
                }
            }
        });

    }

    public void add(View view) {//登録ボタンを押したとき
        if ((what.getText().toString().equals("") == false) && (price.getText().toString().equals("") == false)) {
            String day = getDay();
            String detail = day + " " + getTime() + " " + what.getText().toString() + " " + price.getText().toString();
            //emailの下のmonthの下のdetailに入力内容を登録
            DatabaseReference reference = database.getReference(pre_com + "/detail/" + detail);
            reference.setValue(detail);
            //合計金額をテキストに設定
            DatabaseReference reference_sum = database.getReference(pre_com);
            reference_sum.child("sum").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.i("ccccccc", "cccccc");
                    } else if (task.getResult().getValue() == null) {
                        //nullの時は1番初めなので現在の金額を
                        sum_text.setText(price.getText().toString() + "円");
                        DatabaseReference references = database.getReference(pre_com + "/sum");
                        references.setValue(price.getText().toString());

                        Log.i("iiiiiiii", "iiiii");
                    } else {
                        Log.i("bbbbbbb", String.valueOf(task.getResult().getValue()));
                        String sum_t = String.valueOf(task.getResult().getValue());
                        int sum_t_num = Integer.parseInt(sum_t);
                        sum_t_num = sum_t_num + Integer.parseInt(price.getText().toString());
                        sum_text.setText(Integer.toString(sum_t_num) + "円");
                        DatabaseReference references = database.getReference(pre_com + "/sum");
                        references.setValue(sum_t_num);
                    }
                }
            });
        }
    }


    private class RecyclerListViewHolder extends RecyclerView.ViewHolder {

        public TextView text_content;

        public RecyclerListViewHolder(View itemView) {

            super(itemView);

            text_content = itemView.findViewById(R.id.text_content);
        }
    }


    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {

        private List<String> _listData;


        public RecyclerListAdapter(List<String> listData) {
            _listData = listData;
        }

        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(Resume.this);
            View view = inflater.inflate(R.layout.row, parent, false);
            view.setOnClickListener(new ItemClickListener());
            RecyclerListViewHolder holder = new RecyclerListViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder, int position) {
            String content = _listData.get(position);
            holder.text_content.setText(content);
        }

        @Override
        public int getItemCount() {
            return _listData.size();
        }
    }


    private class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {//リストをクリックしたら、その内容をツイート

            String strTweet = "";
            String strHashTag = "#節約履歴";

            TextView tweet_content =  view.findViewById(R.id.text_content);
            String msg = tweet_content.getText().toString()+"節約！";
            try {
                strTweet = "http://twitter.com/intent/tweet?text="
                        + URLEncoder.encode(msg, "UTF-8")
                        + "+"
                        + URLEncoder.encode(strHashTag, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strTweet));
            startActivity(intent);
            //Toast.makeText(Resume.this, msg, Toast.LENGTH_SHORT).show();


        }
    }

    //目安の画面へ
    public void toTarget(View view){
        Intent intent_target = new Intent(Resume.this,TargetActivity.class);
        startActivity(intent_target);

    }


}