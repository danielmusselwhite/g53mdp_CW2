package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.ViewSavedRunActivity;
import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SavedRunsRecyclerViewAdapter extends RecyclerView.Adapter<SavedRunsRecyclerViewAdapter.ViewHolder>{

    private List<SavedRun> data;
    private Context context;
    private LayoutInflater layoutInflater;

    public SavedRunsRecyclerViewAdapter(Context context){
        this.data = new ArrayList<>();
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_saved_runs_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(){
        notifyDataSetChanged();
    }

    public void setData(List<SavedRun> newData) {
        if (data != null) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    //region "ViewHolder class"
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameView;
        TextView dateView;
        TextView sportView;
        TextView distanceView;
        TextView timeView;

        ViewHolder(View itemView) {
            super(itemView);
            //getting all text views
            nameView = itemView.findViewById(R.id.recyclerTextViewRunTitle);
            dateView = itemView.findViewById(R.id.recyclerTextViewDate);
            sportView = itemView.findViewById(R.id.viewRunTextViewSportValue);
            distanceView = itemView.findViewById(R.id.viewRunTextViewDistanceValue);
            timeView = itemView.findViewById(R.id.viewRunTextViewTimeValue);

            // this can be clicked (allow user to view more info on the run and annotate the data)
            itemView.setOnClickListener(this);
        }

        void bind(final SavedRun savedRun) {
            // updating the text views
            nameView.setText(savedRun.getName());

            //region "formatting the date"
            DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm");
            String dateString = dateFormat.format(savedRun.getDate());
            //endregion

            dateView.setText(dateString);
            String sport = context.getResources().getStringArray(R.array.sports_array)[savedRun.getSportIndex()];
            sportView.setText(sport);
            distanceView.setText(savedRun.getDistance()+" km");
            timeView.setText(MyUtilities.formatTimeNicely(savedRun.getTime()));
        }


        @Override
        public void onClick(View view) {
            Log.d("g53mdp","Clicked "+data.get(getAdapterPosition()).getName()+", the savedRun in position "+getAdapterPosition());

            Bundle bundle = new Bundle();
            bundle.putLong("SavedRunID",data.get(getAdapterPosition()).get_id());
            Intent intent = new Intent(context, ViewSavedRunActivity.class);
            intent.putExtras(bundle);

            ((Activity) context).startActivity(intent);
        }
    }
    //endregion
}
