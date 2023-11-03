package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Visit;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Activity data;

    public MyAdapter(Activity data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_tip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.activityName.setText(data.getActivityName());
        holder.activityLocation.setText(data.getLocationName());
        holder.activityDescription.setText(data.getDescription());
        holder.activityTime.setText(data.getStartTime() + " - " + data.getEndTime());

        if(data.getActivityVisits().size() > 0){

            Visit activityVisit = data.getActivityVisits().get(0);

            holder.activityVisitStatus.setText(activityVisit.getVisitingTime().toString());

        } else {
            holder.activityVisitStatus.setText("No Record");
        }

        holder.closeActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, you can define what happens when the close button is clicked.
                // For instance, if you want to hide the layout:
                v.getRootView().findViewById(R.id.rvView).setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (data == null) ? 0 : 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView activityImage;
        public TextView activityName;
        public TextView activityLocation;
        public TextView activityTime;
        public TextView activityDescription;
        public ImageButton closeActivityButton;
        public TextView activityVisitStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityImage = itemView.findViewById(R.id.activity_image);
            activityName = itemView.findViewById(R.id.activity_name);
            activityLocation = itemView.findViewById(R.id.activity_location);
            activityTime = itemView.findViewById(R.id.activity_time);
            activityDescription = itemView.findViewById(R.id.activity_description);
            closeActivityButton = itemView.findViewById(R.id.closeButton);
            activityVisitStatus = itemView.findViewById(R.id.activity_check_in_status);

        }
    }
}