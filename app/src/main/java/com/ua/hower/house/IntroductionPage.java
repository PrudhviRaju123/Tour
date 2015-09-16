package com.ua.hower.house;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.getbase.floatingactionbutton.FloatingActionButton;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class IntroductionPage extends Fragment {


    CircularSeekBar circularSeekbar;
    CircleImageView circleImageView;
    static Boolean CIRCLE_IMAGE_CLICKED = false;
    MediaPlayer mediaPlayer;
    double media_start_time, media_end_time;
    Handler media_update_handler = new Handler();
    FloatingActionButton fab;
    LinkedHashMap Image_directories = new LinkedHashMap();
    Iterator iterator;
    int temp_val;


    public static IntroductionPage newInstance() {
        IntroductionPage fragment = new IntroductionPage();
        return fragment;
    }

    public IntroductionPage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Image_directories.clear();
        Image_directories.put(0, "introcap_01.png");
        Image_directories.put(3666, "introcap_02.png");
        Image_directories.put(7333, "introcap_03.png");
        Image_directories.put(11000, "introcap_04.png");
        Image_directories.put(14534, "introcap_05.png");
        Image_directories.put(23198, "introcap_06.png");
        Image_directories.put(30449, "introcap_07.png");
        Image_directories.put(32000, "introcap_08.png");
        Image_directories.put(37211, "introcap_09.png");
        Image_directories.put(40157, "introcap_10.png");
        Image_directories.put(42944, "introcap_11.png");
        Image_directories.put(45743, "introcap_12.png");
        Image_directories.put(47862, "introcap_11.png");
        Image_directories.put(49691, "introcap_12.png");
        Image_directories.put(53683, "introcap_13.png");
        Image_directories.put(57640, "introcap_14.png");
        Image_directories.put(64501, "introcap_15.png");

        iterator = Image_directories.keySet().iterator();
        temp_val = 0;


        Log.e("fragment", "IntroductionPage");
        View rootView = inflater.inflate(R.layout.activity_introduction, container,
                false);


        RelativeLayout rltvlayout = (RelativeLayout) rootView.findViewById(R.id.rltivelt);
        rltvlayout.setBackground(getResources().getDrawable(R.drawable.hower_house));
        rltvlayout.getBackground().setAlpha(50);
        rltvlayout.getBackground().setDither(true);


        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.intro_audio);
        media_start_time = mediaPlayer.getCurrentPosition();
        media_end_time = mediaPlayer.getDuration();


        circleImageView = (CircleImageView) rootView.findViewById(R.id.circularimage);
        circleImageView.setClickable(false);

        Bitmap bitmap = BitmapFactory.decodeFile("/data/data/" + getActivity().getPackageName() + "/media/" + Image_directories.get(0));
        circleImageView.setImageBitmap(bitmap);


//      circleImageView.setImageResource(R.drawable.shahrukhkhan);
        circularSeekbar = (CircularSeekBar) rootView.findViewById(R.id.circularbar);
        circularSeekbar.setMax((int) mediaPlayer.getDuration());
        Log.e("max duration", "" + (int) mediaPlayer.getDuration());
        circularSeekbar.setProgress(0);

        /*
        circularSeekbar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mediaPlayer.pause();
                    Log.e("progress value", progress + "");
                    mediaPlayer.seekTo(progress);
                    circularSeekBar.setProgress(progress);
                    mediaPlayer.start();

                }


            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });


        */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Toast.makeText(getActivity(), "Displaying the Items", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).transfer_page();


            }
        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.multiple_actions);
        fab.setColorNormalResId(R.color.myPrimaryColor);
        fab.setClickable(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*set the audio to play*/
                if (!CIRCLE_IMAGE_CLICKED) {
                    mediaPlayer.start();
                    fab.setIcon(R.drawable.ic_pause_circle_outline_white_48dp);
                    fab.setColorNormalResId(R.color.myPrimaryColor);
                    media_update_handler.postDelayed(media_update_runnable, 100);
                    CIRCLE_IMAGE_CLICKED = true;

                } else {
                    /*set the audio to stop */
                    CIRCLE_IMAGE_CLICKED = false;
                    fab.setIcon(R.drawable.ic_play_circle_outline_white_48dp);
                    mediaPlayer.pause();

                }


            }
        });
        return rootView;
    }


    private Runnable media_update_runnable = new Runnable() {

        @Override
        public void run() {
            // Log.e("runnable loop ", "yes");
            circularSeekbar.setProgress((int) mediaPlayer.getCurrentPosition());
            circularSeekbar.invalidate();

            if (temp_val < (int) mediaPlayer.getCurrentPosition()) {

                if (iterator.hasNext()) {

                    temp_val = (Integer) iterator.next();

                    Bitmap bitmap = BitmapFactory.decodeFile("/data/data/" + getActivity().getPackageName() + "/media/" + Image_directories.get(temp_val));
                    circleImageView.setImageBitmap(bitmap);
                }

            }
            media_update_handler.postDelayed(this, 100);

        }
    };


}


