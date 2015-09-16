package com.ua.hower.house;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.gc.materialdesign.views.ProgressBarIndeterminateDeterminate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;


public class MainScreen extends Activity {

    static String dest_file_path = "data/data/com.ua.hower.house/db/";
    static String dest_file_media_path = "data/data/com.ua.hower.house/media/";
    final static String root_directory = "http://www.headfreeze.net/howerhouse/1/";
    public static int TMP_DELTA_DATE_FLAG;
    public static boolean DOWNLOAD_DATA_FLAG = false;
    public static Set<Integer> set_list = new HashSet<Integer>();
    public static int UPDATE_DATE_FLAG = 0;
    DatabaseOpenHelper dbActivity1;
    public static int to_be_downloaded_content_size = 0;
    public static int current_download_content_size = 0;
    public static final String DATA_FILE_EXTENSION = ".data.csv";
    public static final String MEDIA_FILE_EXTENSION = ".items.csv";
    public static int BUFFER_LENGTH = 0;
    public static TextView user_showcase;


    ProgressBarDeterminate mProgress;
    private int mProgressStatus = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        user_showcase = (TextView) findViewById(R.id.textview);
        mProgress = (ProgressBarDeterminate) findViewById(R.id.progressDeterminate);

        SharedPreferences sharedPreferences = getSharedPreferences("PP4_DUMP", Context.MODE_PRIVATE);
        TMP_DELTA_DATE_FLAG = sharedPreferences.getInt("Date", 0);
        UPDATE_DATE_FLAG = sharedPreferences.getInt("Date", 0);


        boolean FOLDER_FLAG = sharedPreferences.getBoolean("Folder", false);

        //TMP_DELTA_DATE_FLAG = 0;
        //UPDATE_DATE_FLAG = 0;
        // Log.e("TMP_DELTA_DATE_FLAG :"+TMP_DELTA_DATE_FLAG ,"UPDATE_DATE_FLAG :"+UPDATE_DATE_FLAG);


       /*folders are created for if doesn't exist*/
        if (!FOLDER_FLAG) {

            File f = new File("/data/data/" + getPackageName(), File.separator + "/db");
            f.mkdirs();
            f.setReadable(true);

            f = new File("/data/data/" + getPackageName(), File.separator + "/media");
            f.mkdirs();
            f.setReadable(true);

            FOLDER_FLAG = true;
            SharedPreferences shrdpref = getSharedPreferences("PP4_DUMP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shrdpref.edit();
            editor.putBoolean("Folder", FOLDER_FLAG);
            editor.commit();
        }

        new ExtractData().execute();
        dbActivity1 = new DatabaseOpenHelper(getBaseContext());
    }


    private class DownloadContents extends AsyncTask<String, String, String> {


        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            mProgress.setProgress((int) ((((current_download_content_size + Integer.parseInt(progress[0])) * 100) / to_be_downloaded_content_size)));

        }


        @Override
        protected String doInBackground(String... params) {

            try {

                download_files(root_directory + UPDATE_DATE_FLAG + DATA_FILE_EXTENSION, dest_file_path + UPDATE_DATE_FLAG + DATA_FILE_EXTENSION);
                download_files(root_directory + UPDATE_DATE_FLAG + MEDIA_FILE_EXTENSION, dest_file_path + UPDATE_DATE_FLAG + MEDIA_FILE_EXTENSION);
                Iterator iterator1 = set_list.iterator();

                // check values
                while (iterator1.hasNext()) {

                    int iterator_element = (int) iterator1.next();
                    download_files(root_directory + iterator_element + ".zip", dest_file_media_path + iterator_element + ".zip");
                }
            } catch (Exception e) {
                Log.e("Exception in download", "" + e.toString());
            }


            return null;

        }


        private void download_files(String source, String destination) {
            HttpURLConnection urlConnection = null;
            int count;
            try {
                URL url = new URL(source);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                FileOutputStream fileOutput = new FileOutputStream(new File(destination));
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[(int) BUFFER_LENGTH];
                int bufferLength = 0;

                while ((count = inputStream.read(buffer)) != -1) {
                    bufferLength += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + bufferLength);

                    fileOutput.write(buffer, 0, count);
                }
                fileOutput.flush();
                fileOutput.close();
                inputStream.close();

            } catch (FileNotFoundException e) {
                Log.e("file not found", "Exception in downloading files");
                Log.e("FileNotFoundException ", "" + e.toString());

            } catch (IOException e) {
                //e.printStackTrace();
                Log.e("IOException ", "" + e.toString());
                Log.e("IO Exception", "Exception in downloading files");
            } catch (Exception e) {
                Log.e("Exception", " in files");
                Log.e("Exception in ", "" + e.toString());
                e.printStackTrace();
            } finally {

                current_download_content_size +=(int)urlConnection.getContentLength();
                Log.e("download_content_size", "" + (int)urlConnection.getContentLength());
                urlConnection.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {


            user_showcase.setText("Processing Data ..");
            Iterator iterator_list = set_list.iterator();

            // check values
            while (iterator_list.hasNext()) {

                int iterator_element = (int) iterator_list.next();
                unzip(dest_file_media_path + iterator_element + ".zip", dest_file_media_path + iterator_element);
            }
            Log.e("Database Load started", " donwload Loop");
            new updateDB().execute();
        }


        public void unzip(String source, String destination) {

            InputStream file_copy_in = null;
            OutputStream fil_copy_out = null;

            try {


                ZipFile zipFile = new ZipFile(source);
                zipFile.extractAll(dest_file_media_path);
                new File(source).delete();

                byte[] buffer = new byte[Byte.MAX_VALUE];
                File directory = new File(destination);
                File[] fList = directory.listFiles();

                if(directory.length()!=0) {


                    Log.e("destination file:","is not empty");
                    for (File file : fList) {
                        if (file.isFile()) {

                            file_copy_in = new FileInputStream(file.getAbsolutePath());
                            fil_copy_out = new FileOutputStream(dest_file_media_path + file.getName());

                            int read;
                            while ((read = file_copy_in.read(buffer)) != -1) {
                                fil_copy_out.write(buffer, 0, read);
                            }
                            file_copy_in.close();
                            file_copy_in = null;

                            // write the output file
                            fil_copy_out.flush();
                            fil_copy_out.close();
                            fil_copy_out = null;
                            new File(file.getAbsolutePath()).delete();
                        }
                    }
                }

                // delete the original file
                new File(destination).delete();

            } catch (ZipException e) {
                Log.e("ZipException", e.toString());
                Log.e("source file:",source);
                Log.e("dest file :",destination);
            } catch (IOException e) {
                Log.e("IOException in unzip", e.toString());
                Log.e("source file:",source);
                Log.e("dest file :",destination);
            }
        }


    }

    private class ExtractData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {

                Document doc = Jsoup.connect(root_directory).get();
                Elements fileElements = doc.select("ul").select("li");
                for (Element iterator : fileElements) {
                    String iterator_value = iterator.text().replaceAll("/|.zip|.csv", "").trim();

                    if (iterator.text().endsWith(DATA_FILE_EXTENSION) && Integer.parseInt(iterator.text().trim().replace(DATA_FILE_EXTENSION, "")) > TMP_DELTA_DATE_FLAG) {

                        DOWNLOAD_DATA_FLAG = true;
                        UPDATE_DATE_FLAG = Integer.parseInt(iterator.text().replace(DATA_FILE_EXTENSION, ""));

                        estimate_download_size(root_directory + "/" + UPDATE_DATE_FLAG + DATA_FILE_EXTENSION);
                        estimate_download_size(root_directory + "/" + UPDATE_DATE_FLAG + MEDIA_FILE_EXTENSION);


                    } else if (!(iterator_value.equalsIgnoreCase("Parent Directory") || iterator.text().endsWith(DATA_FILE_EXTENSION) || iterator.text().endsWith(MEDIA_FILE_EXTENSION))) {

                        if (Integer.parseInt(iterator_value) >= TMP_DELTA_DATE_FLAG) {
                            set_list.add(Integer.parseInt(iterator_value));
                            estimate_download_size(root_directory + "/" + Integer.parseInt(iterator_value) + ".zip");
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception in Extracting", "website");
                Log.e("Exception :", "" + e.toString());

            } finally {

                return null;
            }
        }

        private void estimate_download_size(String url) {
            HttpURLConnection conn = null;

            try {
                URL url_input = new URL(url);
                conn = (HttpURLConnection) url_input.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                conn.connect();
                to_be_downloaded_content_size += conn.getContentLength();
                if (conn.getContentLength() >= BUFFER_LENGTH) {
                    BUFFER_LENGTH = conn.getContentLength();
                }
            } catch (IOException e) {
                Log.e("IOException ", "" + e.toString());
                Log.e("IO Exception", "Exception in finding file size");
            } finally {
                conn.disconnect();
            }
        }


        @Override
        protected void onPostExecute(Void result) {

            if (DOWNLOAD_DATA_FLAG) {
                //mProgress.setIndeterminate(false);


                new DownloadContents().execute();
            } else {
                //mProgress.hide();
                Intent gotoMainScreen = new Intent(MainScreen.this, MainActivity.class);
                startActivity(gotoMainScreen);
                Log.e("going to next page", "HHMainPage");
                finish();

            }

        }
    }


    public class updateDB extends AsyncTask<Void, Void, Void> {

        String selectQuery = null;
        SQLiteDatabase db1 = dbActivity1.getWritableDatabase();

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(Void aVoid) {


            db1.close();
            SharedPreferences shrdpref = getSharedPreferences("PP4_DUMP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shrdpref.edit();
            editor.putInt("Date", UPDATE_DATE_FLAG);
            editor.commit();
            Log.e("Date flag is updated:", UPDATE_DATE_FLAG + "");

            // mProgress.hide();

            Intent gotoMainScreen = new Intent(MainScreen.this, MainActivity.class);
            startActivity(gotoMainScreen);
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {


            /* Deleting and Inserting the data after download*/


            db1.execSQL("delete from HH_TABLE");
            Log.e("deletion  ", "HH_TABLE done");


            db1.execSQL("delete from HH_ITEMS");
            Log.e("deletion  ", "HH_ITEMS done");

            insert_data_into_table("/data/data/" + getPackageName() + "/db/" + UPDATE_DATE_FLAG + DATA_FILE_EXTENSION, "HH_TABLE");
            Log.e("Insertion  ", " HH_tbale done");

            insert_data_into_table("/data/data/" + getPackageName() + "/db/" + UPDATE_DATE_FLAG + MEDIA_FILE_EXTENSION, "HH_ITEMS");
            Log.e("Insertion  ", " HH_tbale done");

            return null;
        }


        private void insert_data_into_table(String file_path, String table_name) {

            String temp = "";
            try {
                // SQLiteDatabase sqllitedb =dbActivity.getWritableDatabase();

                InputStream inputStream = new FileInputStream(file_path);
                Reader reader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(reader);

                // storing the value of length of the first line of maze
                String curr_line = br.readLine();

                String column_data[] = curr_line.split("\\|");
                while ((curr_line = br.readLine()) != null) {
                    String row_data[] = curr_line.split("\\|");
                    ContentValues values = new ContentValues();
                    for (int i = 0; i < column_data.length; i++) {
                        temp = i + " value :" + column_data[i] + "," + row_data[i];
                        values.put(column_data[i], row_data[i]);
                        // Log.e( i+" record :" ,column_data[i]+","+row_data[i]);
                    }
                    long id = db1.insert(table_name, null, values);
                    if (id < 0) {
                        Toast.makeText(getBaseContext(), "messed upsomewhere in " + table_name, Toast.LENGTH_LONG).show();
                        Log.e("id", "messed up somewhere" + table_name);
                    }
                }
            } catch (Exception e) {

                Log.e("Error in insert", "" + e.toString());
                Log.e("record :", temp + "");
                //Toast.makeText(getBaseContext(), " terrible exception", Toast.LENGTH_LONG).show();
            }
        }

    }


}

