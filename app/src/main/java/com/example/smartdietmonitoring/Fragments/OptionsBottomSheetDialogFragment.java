package com.example.smartdietmonitoring.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartdietmonitoring.R;import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;



public class OptionsBottomSheetDialogFragment extends BottomSheetDialogFragment  {

    public static OptionsBottomSheetDialogFragment newInstance() {
        OptionsBottomSheetDialogFragment fragment = new OptionsBottomSheetDialogFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View view;
    private View.OnClickListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.bottomsheetdialog, container, false);
        ImageView ivEditProfile = view.findViewById(R.id.ivEditProfile);
        ImageView ivLogout = view.findViewById(R.id.ivLogout);
        ImageView ivTrackWeight = view.findViewById(R.id.ivTrackWeight);

        ivEditProfile.setOnClickListener(mListener);
        ivLogout.setOnClickListener(mListener);
        ivTrackWeight.setOnClickListener(mListener);



        return view;

    }


    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

}