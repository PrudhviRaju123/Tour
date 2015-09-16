package com.ua.hower.house;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class SelectedPage extends Fragment {

    private static final String TAG_CLICK_QR_SCAN = "QR_SCAN_CLICK";
    private static final String TAG_CLICK_AUDIO = "AUDIO_CLICK";


    private Runnable QRresult;
    FloatingActionsMenu floatingActionsMenu;
    private static boolean speakBtn_flag = true;
    static ViewPager viewPager;
    static PagerAdapter adapter;
    double[] image_order;
    String[] image_caption;
    String[] image_description;
    String[] image_data;
    int Building_level;
    static Context context;
    public static View rootView;



    // TODO: Rename and change types and number of parameters
    public static SelectedPage newInstance() {

        SelectedPage fragment = new SelectedPage();
        return fragment;
    }


    public  void SelectedPageInstance (Context context) {
        this.context =context;
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);




    }


    public SelectedPage() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context=getActivity();

        rootView = inflater.inflate(R.layout.activity_selected_page, container,
                false);
        Building_level = Integer.parseInt(getArguments().getString("building_level").trim());

        Log.e("buildding level :", Building_level + "");
        floatingActionsMenu = (FloatingActionsMenu) rootView.findViewById(R.id.multiple_actions);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);

        FloatingActionButton Scan_btn = new FloatingActionButton(getActivity());
        //Scan_btn.setTitle("Scan QR code");

        Scan_btn.setColorNormalResId(R.color.myPrimaryColor);
        Scan_btn.setColorPressedResId(R.color.ButtonClickColor);

        Scan_btn.setIcon(R.drawable.ic_action_camera);

        Scan_btn.setClickable(true);
        Scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    IntentIntegrator integrator = new IntentIntegrator(getActivity());
                    integrator.addExtra("SCAN_WIDTH", 1000);
                    integrator.addExtra("SCAN_HEIGHT", 800);
                    integrator.addExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
                    integrator.addExtra("PROMPT_MESSAGE", "Scan QR Code for Item");
                    integrator.initiateScan();
                    Log.e("scanning ", "result");

                    Log.e("Scan result Activity", ((MainActivity) getActivity()).qr_content_value + "");

                } catch (Exception e) {
                    Log.e("Exception in Scan", e.toString());
                }

            }
        });
        ((FloatingActionsMenu) rootView.findViewById(R.id.multiple_actions)).addButton(Scan_btn);

        final FloatingActionButton speakBtn = new FloatingActionButton(getActivity());
        //speakBtn.setTitle("Listen to Audio");
        speakBtn.setColorNormalResId(R.color.myPrimaryColor);
        speakBtn.setColorPressedResId(R.color.ButtonClickColor);
        speakBtn.setIcon(R.drawable.ic_action_volume_on);
        speakBtn.setClickable(true);

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageSpoken messageSpoken = new MessageSpoken(getActivity());
                if (speakBtn_flag) {
                    messageSpoken.MessageForSpeak(image_caption[viewPager.getCurrentItem()], image_description[viewPager.getCurrentItem()]);
                    speakBtn_flag = false;
                    // Toast.makeText(getActivity(), "page item"+image_description[viewPager.getCurrentItem()], Toast.LENGTH_SHORT).show();
                } else {
                    messageSpoken.onDestroy();

                    speakBtn_flag = true;
                }

            }
        });
        ((FloatingActionsMenu) rootView.findViewById(R.id.multiple_actions)).addButton(speakBtn);

        DBQuery(1, Building_level);


        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();

            try {

               // Log.e("in the contents  ", "" + contents);
                final int qr_content_value = Integer.parseInt(contents);

                //Log.e("contents value", "" + contents);
                if (contents != null) {
                    QRresult = new Runnable() {
                        @Override
                        public void run() {

                            //flag value 1 set to determine it is a QR Scan result
                            Log.e("calling", "DBQuery");
                            DBQuery(2, qr_content_value);
                        }
                    };

                    QRresult.run();
                }

            } catch (NumberFormatException e) {

                Log.e("Wrong product scanned", "" + e.toString());
                alert_message("Scanner Error", "you scanned the items which are not in  Hower House display");
            } catch (Exception e) {
                Log.e("Excception raised", "" + e.toString());
            }

        } else {

            Toast.makeText(getActivity(), "Is not a valid image or ", Toast.LENGTH_SHORT).show();
            //showDialog(R.string.result_failed, getString(R.string.result_failed_why));
        }
    }


    public void DBQuery(int flag, double value) {
        try {
            SQLiteDatabase database = null;
            DatabaseOpenHelper db1;
            Cursor cursor = null;
            String selectQuery = null;
            //Log.e("dbQuery", "called");

            String x = null;
            int temp_val = (int) value;

            if (flag == 2) {
                selectQuery = null;
                selectQuery = "SELECT  TOUR_STOP, TOUR_TITLE ,OBJECTNAME,TOUR_CONTENT,IMAGEPATH " +
                        "FROM HH_TABLE WHERE CAST(TOUR_STOP AS INTEGER)=" + temp_val + " ORDER BY TOUR_STOP ASC";
                //Log.e("selectQuery", ""+selectQuery);
                 db1 = new DatabaseOpenHelper(context);
            } else {

                selectQuery = null;

                //Log.e("temp value ", "" + temp_val);
                 db1 = new DatabaseOpenHelper(getActivity());
                selectQuery = "SELECT  TOUR_STOP, TOUR_TITLE ,OBJECTNAME,TOUR_CONTENT,IMAGEPATH " +
                        "FROM HH_TABLE WHERE  LOCATION like '" + temp_val + "%%'" + " ORDER BY TOUR_STOP ASC";
              //  Log.e("selectQuery", ""+selectQuery);
            }

           // Log.e("After sqliteDatabase", "called");


                database = db1.getReadableDatabase();
                cursor = database.rawQuery(selectQuery, null);

            Log.e("records retrieved:", cursor.getCount() + "");
            if (cursor != null && cursor.getCount() > 0) {
                Log.e("in the ", "cursor");
                image_order = new double[cursor.getCount()];
                image_caption = new String[cursor.getCount()];
                image_description = new String[cursor.getCount()];
                image_data = new String[cursor.getCount()];

                cursor.moveToFirst();
                Log.e("cursor", "started");
                for (int i = 0; i < cursor.getCount(); i++) {
                   // Log.e("In the :", "cursor loop");
                    image_caption[i] = cursor.getString(cursor.getColumnIndex("OBJECTNAME"));
                    image_description[i] = cursor.getString(cursor.getColumnIndex("TOUR_CONTENT"));
                    image_data[i] = "/data/data/com.ua.hower.house/media/" + cursor.getString(cursor.getColumnIndex("IMAGEPATH"));
                    image_order[i] = cursor.getDouble(cursor.getColumnIndex("TOUR_STOP"));
                    //Log.e("image_caption[" + i + "]", image_caption[i] + "");
                    //Log.e("image_description[" + i + "]", image_description[i] + "");
                    //Log.e("image_data[" + i + "]", image_data[i] + "");
                    //Log.e("image_order[" + i + "]", image_order[i] + "");
                    cursor.moveToNext();
                }


            } else {
                alert_message("No items found", "Please scan the items only on display");
            }
            cursor.close();

            //database.setTransactionSuccessful();
            //database.endTransaction();

            Log.e("Cursor", "closed");
            database.close();
            Log.e("database", "closed");

            Log.e("context",""+context.getApplicationContext());

            adapter = new ViewPagerAdapter(context.getApplicationContext(), image_data, image_caption,
                    image_description, image_order);
            Log.e("adapter","set");
            // Binds the Adapter to the ViewPager
            viewPager.setAdapter(adapter);
            Log.e("viewpager","set");
            //viewPager.getCurrentItem();
            //viewPager.invalidate();
            Toast.makeText(getActivity(), image_data.length + " items found. Swipe right to view next items", Toast.LENGTH_LONG).show();


        } catch (Exception e) {

            Log.e("Exception in sql data", e.toString());
        }

    }


    public void alert_message(String title, String message) {
        // TODO Auto-generated method stub
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
        alertdialog.setTitle(title);
        alertdialog.setMessage(message);
        alertdialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alertdialog.show();
    }


}
