package com.dh.exam.mpt.avtivity.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.dh.exam.mpt.R;


public class NewPaperFragment extends Fragment implements View.OnClickListener{

    private EditText et_paper_name;
    private EditText et_paper_author;
    private LinearLayout name_layout;
    private LinearLayout author_layout;
    private LinearLayout spinner_layout;
    private Button btn_new_paper;
    private AppCompatSpinner spinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_paper, container, false);
        init(view);
        return view;
    }

    public void init(View view){
        name_layout=(LinearLayout)view.findViewById(R.id.name_layout);
        author_layout=(LinearLayout)view.findViewById(R.id.author_layout);
        spinner_layout=(LinearLayout)view.findViewById(R.id.spinner_layout);
        et_paper_name=(EditText)name_layout.findViewById(R.id.et_paper_name);
        et_paper_author=(EditText)author_layout.findViewById(R.id.et_paper_author);
        spinner=(AppCompatSpinner) spinner_layout.findViewById(R.id.spinner);
        btn_new_paper=(Button) view.findViewById(R.id.btn_new_paper);
        btn_new_paper.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_new_paper:

                break;
                default:
        }
    }
}
