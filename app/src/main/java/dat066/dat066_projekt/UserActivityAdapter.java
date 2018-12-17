package dat066.dat066_projekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import androidx.recyclerview.widget.RecyclerView;
import dat066.dat066_projekt.database.UserActivityEntity;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder> {

    class UserActivityViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateView;
        private final TextView timeView;
        private final TextView distanceView;
        private final TextView paceView;
        private final TextView speedView;
        private final TextView caloriesView;


        private UserActivityViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.txtDate);
            timeView = itemView.findViewById(R.id.txtTime);
            distanceView = itemView.findViewById(R.id.txtDistance);
            paceView = itemView.findViewById(R.id.txtPace);
            speedView = itemView.findViewById(R.id.txtTopSpeed);
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
        Date date = new Date(current.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formatted = formatter.format(date);
        holder.dateView.setText(current.getDate());
        holder.timeView.setText(formatted);
        if(current.getDistance() > 1000 ) {
            holder.distanceView.setText("Distance " + current.getDistance()/1000 + " km");
        }else {
            holder.distanceView.setText("Distance " + current.getDistance() + " m");
        }
        holder.paceView.setText("Pace " + current.getPace() + " min/km");
        holder.speedView.setText("Top Speed " + current.getSpeed() + " m/s");
        holder.caloriesView.setText("" + current.getCalories() + " calories burned");
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
