package com.example.jane.timskaapplication101;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jane.timskaapplication101.files.PDFItem;
import com.example.jane.timskaapplication101.files.SecondFrag;
import com.memetix.mst.translate.Translate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements BaseFragment.OnBaseFragmentInteractionListener ,
        MainNavFragment.NavigationDrawerCallbacks ,
        TextToSpeech.OnInitListener {

    private static List<PDFItem> pdfs;

    private int globalInitStatus;
    public static TextToSpeech tts;

    private boolean toastBool;

    public static List<PDFItem> getPdfs() {
        return pdfs;
    }


    public static boolean searchIsFinished;

    private MainNavFragment mNavFragment;

    private CharSequence mTitle;

    // public static boolean drawerisEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // drawerisEnabled = true;

        mNavFragment = (MainNavFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        pdfs = new ArrayList<PDFItem>();

        tts = new TextToSpeech(getApplicationContext(), this);

        //proverka na mrezi mreza wifi mobile data

        MyNetworkChecker.isNetworkAvailable(this);

        boolean isWifiAval = MyNetworkChecker.isWifiAvailable(this);
        Log.i("MREZA", "Wifi: " + isWifiAval);
        boolean isMobileData = MyNetworkChecker.isMobileDataAvailable(this);
        Log.i("MREZA", "MobileData: " + isMobileData);
        boolean isThreeGData = MyNetworkChecker.is3GConnected(this);
        Log.i("MREZA", "3G: " + isThreeGData);


        SharedPreferences example = this.
                getSharedPreferences(Prefs.PREFS, 0);

        toastBool = example.getBoolean(Prefs.ToastPrefs,
                false);

        /*
        Translate.setClientId("com_dzumerko_timskaapplication101");
        Translate.setClientSecret("odkojarabotaodkojarabotaodkojarabota");
    */
        String clientID = "Timska_rab_ClientID";
        Translate.setClientId(clientID);
        String clientSecret = "8ZWtTqK5QIHqCQfPwzbETQfY9LlWQ0PGbC0Oc0vP30k=";
        Translate.setClientSecret(clientSecret);

        // za multi-threading - do 5 maksimum

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new SearchForAllPDF().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new SearchForAllPDF().execute();
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavFragment.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mNavFragment.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }
*/
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        MainNavigation.switchScreen(this, position);
    }

    @Override
    public void onBaseFragmentInteraction(BaseFragment.MsgType type, BaseFragment caller, Bundle data) {
        switch(type){
            case SECTION_ATTACHED:
                mTitle = MainNavigation.getSectionTitle(this, caller.getArguments());
                break; // will be used in {@link #restoreActionBar()}
            default: // SWITCH_SCREEN, STACK_PUSH, STACK_POP handled in MainNavigation
                MainNavigation.navigate(this, type, caller, data);
                break;
        }
    }

    public void Search_Dir(File dir) {
        String pdfPattern = ".pdf";

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {
                PDFItem tmp = null;
                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    if(FileList[i].getName().endsWith(pdfPattern)) {
                        tmp = new PDFItem(FileList[i].getName(),
                                getFileSize(FileList[i].length()),
                                FileList[i].getParent(),
                                R.drawable.logo);
                    }
                    if(tmp != null) {
                        boolean boo = true;
                        for (PDFItem pdfItem : pdfs) {
                            if (pdfItem.getName().equals(tmp.getName())) {
                                boo = false;
                                break;
                            }
                        }
                        if (boo) {
                            pdfs.add(tmp);
                        }
                    }
                }
            }
        }
    }

    public static String getFileSize( long size ) {
        if(size < 1024) {
            return size + " b";
        }
        else {
            size /= 1024;
            if(size < 1024) {
                return size + " Kb";
            } else {
                size /= 1024;
                return size + " Mb";
            }
        }
    }


    // for starting voice translator
    @Override
    public void onInit(int status) {
        globalInitStatus = status;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectVoiceInit().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectVoiceInit().execute();
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    // Async Task Class
    class SearchForAllPDF extends AsyncTask<String, String, String> {

        @Override
         protected void onPreExecute() {
            super.onPreExecute();
        }

        // Search for all pdf files in your device
        @Override
        protected String doInBackground(String... f_url) {
            //make thread to sleep 7 sec. While testing only ...
            //SystemClock.sleep(7000);
            Search_Dir(Environment.getExternalStorageDirectory());

            return "Something";
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String file_url) {
            searchIsFinished = true;
            if(toastBool) {
                Toast.makeText(getApplicationContext(), "Searching finished",
                        Toast.LENGTH_SHORT).show();
            } else {
                tts.setLanguage(Locale.ENGLISH);
                tts.speak("Searching finished", TextToSpeech.QUEUE_FLUSH, null);
            }
            //SecondFrag.setVisibilityToView();
            if(SecondFrag.adapter != null)
            SecondFrag.adapter.notifyDataSetChanged();

        }
    }

    // Async Task Class
    class ConnectVoiceInit extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... f_url) {
            if (globalInitStatus == TextToSpeech.SUCCESS) {

                Log.i("JANE","SE POVIKAV");

                int result = tts.setLanguage(Locale.getDefault());

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {

                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }
            return "";
        }


        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String file_url) {

        }
    }


}
