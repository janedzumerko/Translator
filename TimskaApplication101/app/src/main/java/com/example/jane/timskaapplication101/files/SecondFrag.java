package com.example.jane.timskaapplication101.files;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jane.timskaapplication101.MainNavigation;
import com.example.jane.timskaapplication101.R;
import com.example.jane.timskaapplication101.MainActivity;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.io.File;

/**
 * Created by Jane on 7/22/2015.
 */
public class SecondFrag extends MainNavigation.SectionFrag {


    public RecyclerView rv;
    public static RVAdapter adapter;
    public TextView emptyView;
    public CircularProgressView progresSearch;

    public static String [] spinnerLangauges;
    public static String momentFileLangauge;
    public static int voiceSelectedItem;
    public static Spinner FileLangauges;

    public DialogInterface.OnClickListener dialogClickListener;


    public SecondFrag(){
        super();
    }
    public static SecondFrag newInstance(Context c, int section){
        SecondFrag ret = new SecondFrag();
        ret.setSection(section);
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.second_frag_menu, menu);
    }

    @Override
    public void afterCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        super.afterCreateView(inflater, container, savedInstanceState); // not mandatory, but good to call super
        setContentView(R.layout.second_frag_layout);

        rv = (RecyclerView)findViewById(R.id.rv);
        emptyView = (TextView) findViewById(R.id.empty_view);
        progresSearch = (CircularProgressView) findViewById(R.id.prgres_bar_search);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);


        //spinner to choose a langauge
        FileLangauges = (Spinner) findViewById(R.id.file_choose_langauge_spinner);
        ArrayAdapter<CharSequence> adaptervoice =
                ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                        R.array.langauge_array , R.layout.spinner_custom_textview);
        adaptervoice.setDropDownViewResource(R.layout.spinner_custom_textview);
        FileLangauges.setAdapter(adaptervoice);
        voiceSelectedItem = 4;
        spinnerLangauges = new String[38];
        spinnerLangauges = getResources().getStringArray(R.array.langauge_array);
        momentFileLangauge = spinnerLangauges[voiceSelectedItem];
        FileLangauges.setSelection(adaptervoice.getPosition("English"));
        // spinner />



        /*
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Log.i("MART", "YES button clicked");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        Log.i("MART", "NO button clicked");
                        break;
                }
            }
        };
        */


        initializeAdapter();

        setVisibilityToView();

    }

    public void setVisibilityToView() {
        if(MainActivity.getPdfs().isEmpty()) {
            if(MainActivity.searchIsFinished) {
                rv.setVisibility(View.GONE);
                emptyView.setText(R.string.nofiles_in_device);
                emptyView.setVisibility(View.VISIBLE);
                progresSearch.setVisibility(View.GONE);
            } else {

                rv.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                progresSearch.setVisibility(View.VISIBLE);
            }
        } else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            progresSearch.setVisibility(View.GONE);
        }
    }

    public static String getLangaugeForFileTranslating() {
        voiceSelectedItem = FileLangauges.getSelectedItemPosition();
        momentFileLangauge = spinnerLangauges[voiceSelectedItem];

        return convertorToLangaugeCode(momentFileLangauge);
    }

    public static String convertorToLangaugeCode(String fullNameOfLangauge) {
        String langaugeCode;

        switch (fullNameOfLangauge) {

            case "Arabic" : langaugeCode = "ar";
                break;
            case "Czech" : langaugeCode = "cs";
                break;
            case "Danish" : langaugeCode = "da";
                break;
            case "German" : langaugeCode = "de";
                break;
            case "English" : langaugeCode = "en";
                break;
            case "Estonian" : langaugeCode = "et";
                break;
            case "Finnish" : langaugeCode = "fi";
                break;
            case "French" : langaugeCode = "fr";
                break;
            case "Dutch" : langaugeCode = "nl";
                break;
            case "Greek" : langaugeCode = "el";
                break;
            case "Hebrew" : langaugeCode = "he";
                break;
            case "Haitian Creole" : langaugeCode = "ht";
                break;
            case "Hungarian" : langaugeCode = "hu";
                break;
            case "Indonesian" : langaugeCode = "id";
                break;
            case "Italian" : langaugeCode = "it";
                break;
            case "Japanese" : langaugeCode = "ja";
                break;
            case "Korean" : langaugeCode = "ko";
                break;
            case "Lithuanian" : langaugeCode = "lt";
                break;
            case "Latvian" : langaugeCode = "lv";
                break;
            case "Norwegian" : langaugeCode = "no";
                break;
            case "Polish" : langaugeCode = "pl";
                break;
            case "Portuguese" : langaugeCode = "pt";
                break;
            case "Romanian" : langaugeCode = "ro";
                break;
            case "Spanish" : langaugeCode = "es";
                break;
            case "Russian" : langaugeCode = "ru";
                break;
            case "Slovak" : langaugeCode = "sk";
                break;
            case "Slovenian" : langaugeCode = "sl";
                break;
            case "Swedish" : langaugeCode = "sv";
                break;
            case "Thai" : langaugeCode = "th";
                break;
            case "Turkish" : langaugeCode = "tr";
                break;
            case "Ukrainian" : langaugeCode = "uk";
                break;
            case "Vietnamese" : langaugeCode = "vi";
                break;
            case "Simplified Chinese" : langaugeCode = "zh-CHS";
                break;
            case "Traditional Chinese" : langaugeCode = "zh-CHT";
                break;
            default: langaugeCode = "en";
                break;
        }

        return langaugeCode;
    }

    public void initializeAdapter() {

        adapter = new RVAdapter(getActivity().getApplicationContext(), MainActivity.getPdfs());
        rv.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                    int position = viewHolder.getAdapterPosition();
                    Log.i("MART", "Position: " + position);
                Log.i("MART", "Swipe code: " + swipeDir);
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity().getApplicationContext());
                builder.setMessage("Are you sure?").setPositiveButton(
                        "Yes", dialogClickListener).setNegativeButton(
                        "No",  dialogClickListener).show();
                */
                String filePath = RVAdapter.pdfItems.get(position).getPath() + "/"
                            + RVAdapter.pdfItems.get(position).getName();
                    Log.i("MART", "Path: " + filePath);
                    File tmpFile = new File(filePath);
                    tmpFile.delete();
                    RVAdapter.pdfItems.remove(position);
                    adapter.notifyDataSetChanged();
                    setVisibilityToView();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    public static RVAdapter getRVAdapter() {
        return adapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.new_refresh_button) {
            setVisibilityToView();
            adapter.notifyDataSetChanged();

        }
        if(item.getItemId() == R.id.new_choose_langauge_button) {
            FileLangauges.performClick();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRetryClicked() {}
}

