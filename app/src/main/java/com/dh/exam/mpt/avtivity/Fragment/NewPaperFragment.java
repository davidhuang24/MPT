package com.dh.exam.mpt.avtivity.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.avtivity.MainActivity;
import com.dh.exam.mpt.avtivity.NewQuestionActivity;
import com.dh.exam.mpt.entity.MPTUser;
import com.dh.exam.mpt.entity.Paper;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class NewPaperFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private EditText et_paper_name;
    private LinearLayout name_layout;
    private LinearLayout author_layout;
    private LinearLayout spinner_layout;
    private Button btn_new_paper;
    private AppCompatSpinner spinner;

    private MainActivity currentActivity;
    private String []paperKind;
    private int spinnerItemId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_paper, container, false);
        init(view);
        return view;
    }

    public void init(View view){

        currentActivity=(MainActivity)getActivity();

        name_layout=(LinearLayout)view.findViewById(R.id.name_layout);
        spinner_layout=(LinearLayout)view.findViewById(R.id.spinner_layout);
        et_paper_name=(EditText)name_layout.findViewById(R.id.et_paper_name);
        btn_new_paper=(Button) view.findViewById(R.id.btn_new_paper);
        btn_new_paper.setOnClickListener(this);

        paperKind=currentActivity.getResources().getStringArray(R.array.paper_kind);
        spinner=(AppCompatSpinner) spinner_layout.findViewById(R.id.kind_spinner);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_new_paper:
                newPaper();
                break;
                default:
        }
    }

    /**
     * 新建试卷，插入一条记录到Paper表
     */
    public void newPaper(){
        final String title = et_paper_name.getText().toString().trim();

        if(!TextUtils.isEmpty(title)){
            MPTUser currentUser= BmobUser.getCurrentUser(MPTUser.class);
            Paper paper=new Paper();
            paper.setLove(false);
            paper.setPaperName(title);
            paper.setPaperAuthor(currentUser);//添加一对多关系
            paper.setPaperKind(paperKind[spinnerItemId]);
            paper.setQuestionCount(0);
            paper.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    String objectId=s;
                    if(e==null){
                        NewQuestionActivity.activityStart(currentActivity,NewQuestionActivity.class,objectId,null,null);
                    }else{
                        Toast.makeText(currentActivity,
                                getString(R.string.new_paper_fail)+e.getErrorCode()+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else {
            Toast.makeText(currentActivity, getString(R.string.paper_title_null), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Spinner Item选中时的操作
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       spinnerItemId=position;
    }
    /**
     * Spinner Item未选中时的操作
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
