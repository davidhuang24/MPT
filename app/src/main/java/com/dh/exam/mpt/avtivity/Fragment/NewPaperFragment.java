package com.dh.exam.mpt.avtivity.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dh.exam.mpt.MPTApplication;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.avtivity.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class NewPaperFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private EditText et_paper_name;
    private EditText et_paper_author;
    private LinearLayout name_layout;
    private LinearLayout author_layout;
    private LinearLayout spinner_layout;
    private Button btn_new_paper;
    private AppCompatSpinner spinner;

    private MainActivity currentActivity;
    private String []paperKind;

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
        author_layout=(LinearLayout)view.findViewById(R.id.author_layout);
        spinner_layout=(LinearLayout)view.findViewById(R.id.spinner_layout);
        et_paper_name=(EditText)name_layout.findViewById(R.id.et_paper_name);
        et_paper_author=(EditText)author_layout.findViewById(R.id.et_paper_author);
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

                break;
                default:
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
        Toast.makeText(MPTApplication.getContext(),paperKind[position] , Toast.LENGTH_SHORT).show();
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
