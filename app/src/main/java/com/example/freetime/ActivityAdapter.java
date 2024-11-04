/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Esta clase es un adaptador que gestiona la visualización de actividades en una lista. Enlaza los datos de cada actividad con su vista correspondiente.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Se usa en conjunto con un RecyclerView para mostrar actividades en una lista.
 */


package com.example.freetime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.freetime.entities.Activity;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private List<Activity> activityList;

    public ActivityAdapter(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public void setActivityList(List<Activity> activities) {
        this.activityList = activities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activityList.get(position);
        holder.bind(activity);
    }

    @Override
    public int getItemCount() {
        return activityList != null ? activityList.size() : 0;
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView activityName;
        private TextView activityTime;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.tvActivityName);
            activityTime = itemView.findViewById(R.id.tvActivityTime);
        }

        public void bind(Activity activity) {
            activityName.setText(activity.name);
            activityTime.setText(activity.startTime + " - " + activity.endTime);
        }
    }
}
