package com.ua.hower.house;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

    public static int qr_content_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("Main Activity", "loaded");
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        //viewPager = (ViewPager) findViewById(R.id.pager);

    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    public void transfer_page() {

        setTitle("Displaying Items");
        Bundle bundle = new Bundle();
        bundle.putString("building_level", "1");

        SelectedPage selectedPage = new SelectedPage();
        selectedPage.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container, selectedPage)
                .commit();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {


        Log.e("in the navigation", "item selected page");

        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();


        switch (position) {

            case 0:
                setTitle("Hower House -Intro");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, IntroductionPage.newInstance())
                        .commit();
                break;
            case 1:
            case 2:
            case 3:
            case 4:

                setTitle("Displaying Items");
                Bundle bundle = new Bundle();
                bundle.putString("building_level", position + "");

                SelectedPage selectedPage = new SelectedPage();
                selectedPage.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, selectedPage)
                        .commit();
                break;


            case 5:

                setTitle("Contact Information");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactInfo.newInstance())
                        .commit();
                break;
            default:
                setTitle("Hower House -Intro");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, IntroductionPage.newInstance())
                        .commit();
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action ba

            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share_media) {

            Log.e("clicked", "share media");

/*
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");

            share.putExtra(Intent.EXTRA_SUBJECT, "Hower House");
            share.putExtra(Intent.EXTRA_TEXT, "http://howerhouse.org/events1/");
            startActivity(Intent.createChooser(share, "Promote Us if you"));


            */
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, "Hower House");
            share.putExtra(Intent.EXTRA_TEXT, "http://howerhouse.org/events1/");

            startActivity(Intent.createChooser(share, "Share link!"));
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

       // Log.e("onActivity result", "" + requestCode + "," + resultCode + "," + intent);
        super.onActivityResult(requestCode, resultCode, intent);


        SelectedPage selctedpage = new SelectedPage();
        selctedpage.SelectedPageInstance(this);
        selctedpage.onActivityResult(requestCode, resultCode, intent);

    }


}
