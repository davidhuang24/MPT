package com.dh.exam.mpt.avtivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.dh.exam.mpt.R;

public class NewQuestionActivity extends BaseActivity {

    private String paperObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        init();
    }

    public void init(){
        paperObjectId=getIntent().getStringExtra("param1");
        Toast.makeText(this, paperObjectId, Toast.LENGTH_SHORT).show();
    }
}
