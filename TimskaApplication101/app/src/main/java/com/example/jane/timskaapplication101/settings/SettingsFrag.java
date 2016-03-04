package com.example.jane.timskaapplication101.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jane.timskaapplication101.MainNavigation;
import com.example.jane.timskaapplication101.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jane on 7/22/2015.
 */
public class SettingsFrag extends MainNavigation.SectionFrag {

    private List<SettingsItem> settingsitem;
    private RecyclerView rvs;

    Context context;


    public SettingsFrag(){
        super();
    }
    public static SettingsFrag newInstance(Context c, int section){
        SettingsFrag ret = new SettingsFrag();
        ret.setSection(section);
        return ret;
    }



    @Override
    public void afterCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        super.afterCreateView(inflater, container, savedInstanceState); // not mandatory, but good to call super
        setContentView(R.layout.settings_frag_layout);

        rvs = (RecyclerView) findViewById(R.id.rvs);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity().
        getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        rvs.setLayoutManager(llm);
        rvs.setHasFixedSize(true);

        initializeDate();
        initializeadapter();



    }

    private void initializeDate() {
        settingsitem = new ArrayList<SettingsItem>();
        settingsitem.add(new SettingsItem("WiFi only", false,1));
        settingsitem.add(new SettingsItem("Voice translator", true,1));
        settingsitem.add(new SettingsItem("Toast feedback", false,1));
        settingsitem.add(new SettingsItem("Exit", false,0));
    }

    private void initializeadapter() {
        RVSAdapter adapter = new RVSAdapter(getActivity().getApplicationContext(),
                settingsitem);
        adapter.SetOnItemClickListener(new RVSAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("JANE", "kliknato vo settings : " + position);
                if(position == 3) {
                    System.exit(0);
                }
            }
        });
        rvs.setAdapter(adapter);
    }


    @Override
    protected void onRetryClicked() {}
}

