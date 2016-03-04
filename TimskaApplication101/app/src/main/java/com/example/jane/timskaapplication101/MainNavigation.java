package com.example.jane.timskaapplication101;

/**
 * Created by Jane on 7/22/2015.
 */

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.jane.timskaapplication101.extra.FirstFrag;
import com.example.jane.timskaapplication101.files.SecondFrag;
import com.example.jane.timskaapplication101.settings.SettingsFrag;

public class MainNavigation {
    public static final String KEY_TAB = "tab";
    public static final int TAB_FIRST = 0;
    public static final int TAB_SECOND = 1;
    public static final int TAB_SETTINGS = 2;



    public static void navigate(MainActivity a, BaseFragment.MsgType type, BaseFragment caller, Bundle data){
        switch(type){
            case SWITCH_SCREEN:
                int position = data.getInt(KEY_TAB);
                switchScreen(a, position);
                break;
        }


    }


    public static void switchScreen(MainActivity a, int position){ // TODO: Don't, if already on that screen
        BaseFragment f = null;

        switch(position){
            case TAB_FIRST:
                f = FirstFrag.newInstance(a, position);
                break;
            case TAB_SECOND:
                f = SecondFrag.newInstance(a, position);
                break;
            case TAB_SETTINGS:
                f = SettingsFrag.newInstance(a, position);
                break;
        }
        FragmentManager fragmentManager = a.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }


    public static String getSectionTitle(Context c, int number){
        return c.getResources().getStringArray(R.array.main_sections)[number]; // TODO: store in mem, faster
    }
    public static String getSectionTitle(Context c, Bundle data){
        return getSectionTitle(c, data.getInt(KEY_TAB));
    }


    public static abstract class SectionFrag extends BaseFragment{

        public SectionFrag(){
            super();
        }

        protected void setSection(int section){ // change to enum?
            getArguments().putInt(KEY_TAB, section);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            sendFragmentInteraction(MsgType.SECTION_ATTACHED, null);
        }

    }
}
