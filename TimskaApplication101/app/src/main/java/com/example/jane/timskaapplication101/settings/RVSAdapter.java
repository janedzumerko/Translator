package com.example.jane.timskaapplication101.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jane.timskaapplication101.Prefs;
import com.example.jane.timskaapplication101.R;

import java.util.List;

/**
 * Created by Jane on 8/18/2015.
 */
public class RVSAdapter extends RecyclerView.Adapter<RVSAdapter.SettingsItemViewHolder> {
    List<SettingsItem> settingsItems;
    OnItemClickListener mItemClickListener;
    Context context;

    public RVSAdapter(Context context, List<SettingsItem> settingsItems) {
        this.settingsItems = settingsItems;
        this.context = context;
    }

    public class SettingsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView myTextView;
        private CheckBox myCheckBox;

        public SettingsItemViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.settextview);
            myCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_settings);;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }

    @Override
    public SettingsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.settingsitem_layout, parent, false);
        SettingsItemViewHolder setitvh = new SettingsItemViewHolder(v);
        return setitvh;
    }

    @Override
    public void onBindViewHolder(SettingsItemViewHolder holder, int position) {
        final int pos = position;


        holder.myTextView.setText(settingsItems.get(position).getName());

        SharedPreferences example = context.getSharedPreferences(Prefs.PREFS, 0);
        boolean sharedBool = example.getBoolean("userChecked"+pos,
                settingsItems.get(pos).isChecked());

        holder.myCheckBox.setChecked(sharedBool);



        if(settingsItems.get(position).getVisible() == 1) {
            holder.myCheckBox.setVisibility(View.VISIBLE);
        } else {
            holder.myCheckBox.setVisibility(View.GONE);
        }

        holder.myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("JANE", settingsItems.get(pos).getName()
                        + " " + isChecked);
                settingsItems.get(pos).setChecked(isChecked);

                SharedPreferences examplePrefs =
                        buttonView.getContext().getSharedPreferences(Prefs.PREFS, 0);
                SharedPreferences.Editor editor = examplePrefs.edit();
                editor.putBoolean("userChecked"+pos, isChecked );
                editor.commit();
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}