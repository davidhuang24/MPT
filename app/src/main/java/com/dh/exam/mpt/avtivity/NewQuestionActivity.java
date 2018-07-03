package com.dh.exam.mpt.avtivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.dh.exam.mpt.R;


public class NewQuestionActivity extends BaseActivity implements View.OnClickListener{

    private String paperObjectId;
    private EditText et_title;
    private EditText et_option_a;
    private EditText et_option_b;
    private EditText et_option_c;
    private EditText et_option_d;
    private EditText et_analysis;
    private Button btn_pre;
    private Button btn_next;
    private Button btn_commit;
    private AppCompatCheckBox cb_a;
    private AppCompatCheckBox cb_b;
    private AppCompatCheckBox cb_c;
    private AppCompatCheckBox cb_d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        init();
    }

    public void init(){
        paperObjectId=getIntent().getStringExtra("param1");
        Toast.makeText(this, paperObjectId, Toast.LENGTH_SHORT).show();
        et_title=(EditText) findViewById(R.id.et_question_title);
        et_option_a=(EditText) findViewById(R.id.et_option_a);
        et_option_b=(EditText) findViewById(R.id.et_option_b);
        et_option_c=(EditText) findViewById(R.id.et_option_c);
        et_option_d=(EditText) findViewById(R.id.et_option_d);
        et_analysis=(EditText) findViewById(R.id.et_question_analysis);
        btn_pre=(Button) findViewById(R.id.btn_pre_que);
        btn_next=(Button) findViewById(R.id.btn_next_que);
        btn_commit=(Button) findViewById(R.id.btn_commit);
        cb_a=(AppCompatCheckBox) findViewById(R.id.cb_option_a);
        cb_b=(AppCompatCheckBox) findViewById(R.id.cb_option_b);
        cb_c=(AppCompatCheckBox) findViewById(R.id.cb_option_c);
        cb_d=(AppCompatCheckBox) findViewById(R.id.cb_option_d);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_commit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_pre_que:
                preQuestion();
                break;
            case R.id.btn_next_que:
                nextQuestion();
                break;
            case R.id.btn_commit:
                commitQuestions();
                break;
                default:
        }
    }

    public void preQuestion(){

    }

    public void nextQuestion(){

    }

    public void commitQuestions(){

    }
}
