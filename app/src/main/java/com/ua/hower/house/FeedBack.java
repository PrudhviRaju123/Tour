package com.ua.hower.house;

import android.support.v4.app.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;


public class FeedBack extends Fragment  {


    FloatingActionButton fab;

    public static FeedBack newInstance() {
        FeedBack fragment = new FeedBack();
        return fragment;
    }

    public FeedBack() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_feed_back, container,
                false);


        fab=(FloatingActionButton)rootView.findViewById(R.id.multiple_actions);
        fab.setIcon(R.drawable.ic_send_white_48dp);
        fab.setColorNormalResId(R.color.myPrimaryColor);
        fab.setClickable(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setColorPressedResId(R.color.ButtonClickColor);
                fab.setIcon(R.drawable.ic_done_white_48dp);
                fab.setColorNormalResId(R.color.myPrimaryColor);


            }
        });

        return rootView;
    }

}
