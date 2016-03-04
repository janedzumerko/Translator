package com.example.jane.timskaapplication101;

import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

/**
 * Created by Jane on 7/22/2015.
 */
public class NoFastClick{ // TODO: make all protected after flattening class structure
    private static final int FREQUENCY_LIMIT = 380; // 0.38s between clicks, sufficient to show a typical Dialog
    private static long clickBlockExpiresAt = 0; // time before which clicks are to be ignored
    private static boolean blockTouchEvents = false; // currently in the middle of an on*Click method

    public static boolean preClick(){
        if(blockTouchEvents || (System.currentTimeMillis() <= clickBlockExpiresAt))
            return false; // too early, ignore click
        blockTouchEvents = true;
        return true;
    }
    public static void postClick(){
        clickBlockExpiresAt = System.currentTimeMillis() + FREQUENCY_LIMIT;
        blockTouchEvents = false;
    }



    public static abstract class ViewOnClickListener implements View.OnClickListener{
        public final void onClick(View v) {
            if(!preClick()) return;
            doOnClick(v);
            postClick();
        }
        public abstract void doOnClick(View v); // true body of the onClick
    }
    public static abstract class ViewOnLongClickListener implements View.OnLongClickListener{
        public final boolean onLongClick(View v) {
            if(!preClick()) return false;
            boolean ret = doOnLongClick(v);
            postClick();
            return ret;
        }
        public abstract boolean doOnLongClick(View v); // true body of the onClick
    }

    public static abstract class AdapterViewOnItemClickListener implements AdapterView.OnItemClickListener{
        public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(!preClick()) return;
            doOnItemClick(parent, view, position, id);
            postClick();
        }
        public abstract void doOnItemClick(AdapterView<?> parent, View view, int position, long id); // true body of the onClick
    }

    public static abstract class ExpandableListViewOnChildClickListener implements ExpandableListView.OnChildClickListener{
        public boolean onChildClick (ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
            if(!preClick()) return false;
            boolean ret = doOnChildClick(parent, v, groupPosition, childPosition, id);
            postClick();
            return ret;
        }
        public abstract boolean doOnChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id); // true body of the onClick
    }
    // There's no ExpandableListView.OnChildLongClickListener, but this works the same.
    public static abstract class ExpandableListViewOnChildLongClickListener implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                if(!preClick()) return false;
                boolean ret = doOnChildLongClick((ExpandableListView)parent, view, groupPosition, childPosition, id);
                postClick();
                return ret;
            }

            return false;
        }
        public abstract boolean doOnChildLongClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id); // true body of the onClick
    }

    public static abstract class DialogInterfaceOnClickListener implements DialogInterface.OnClickListener{
        public final void onClick(DialogInterface dialog, int id) {
            if(!preClick()) return;
            doOnClick(dialog, id);
            postClick();
        }
        public abstract void doOnClick(DialogInterface dialog, int id); // true body of the onClick
    }

}