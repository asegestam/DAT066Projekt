package dat066.dat066_projekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dat066.dat066_projekt.database.UserActivityEntity;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder> {

    class UserActivityViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateView;
        private final TextView timeView;
        private final TextView distanceView;
        private final TextView speedView;
        private final TextView caloriesView;


        private UserActivityViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.txtDate);
            timeView = itemView.findViewById(R.id.txtTime);
            distanceView = itemView.findViewById(R.id.txtDistance);
            speedView = itemView.findViewById(R.id.txtSpeed);
            caloriesView = itemView.findViewById(R.id.txtCalories);
        }
    }

    private final LayoutInflater mInflater;
    private List<UserActivityEntity> mActivities = Collections.emptyList(); // Cached copy of userActivities

    UserActivityAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public UserActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new UserActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserActivityViewHolder holder, int position) {
        UserActivityEntity current = mActivities.get(position);
        ArrayList<Double> elevationArray = current.getElevation();
        double sum = elevationArray.size();
        holder.dateView.setText(current.getDate());
        holder.timeView.setText("" + current.getTime() + " s");
        holder.distanceView.setText("" + current.getDistance() + " m");
        holder.speedView.setText("" + current.getSpeed() + " m/s");
        holder.caloriesView.setText("" + current.getDistance() + " m");
    }

    void setActivities(List<UserActivityEntity> activities) {
        mActivities = activities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }
}
