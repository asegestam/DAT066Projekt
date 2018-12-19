package dat066.dat066_projekt;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import androidx.annotation.DrawableRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import dat066.dat066_projekt.database.UserActivityEntity;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder> {

    class UserActivityViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView dateView;
        private final TextView timeView;
        private final TextView distanceView;
        private final TextView paceView;
        private final TextView speedView;
        private final TextView caloriesView;


        private UserActivityViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
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
    public void onBindViewHolder(final UserActivityViewHolder holder, final int position) {
        final UserActivityEntity current = mActivities.get(position);
        holder.cardView.setId(current.getDate().hashCode());
        ArrayList<Double> elevationArray = current.getElevationArray();
        Date date = new Date(current.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String formatted = formatter.format(date);
        holder.dateView.setText(current.getDate());
        holder.timeView.setText(formatted);
        if(current.getDistance() > 1000 ) {
            holder.distanceView.setText("Distance " + current.getDistance()/1000 + " km");
        }else {
            holder.distanceView.setText("Distance " + (int)current.getDistance() + " m");
        }
        holder.paceView.setText("Pace " + current.getPace() + " min/km");
        holder.speedView.setText("Top Speed " + current.getSpeed() + " m/s");
        holder.caloriesView.setText("" + (int)current.getCalories() + " calories burned");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("View ID: " + v.getId());
                UserActivityEntity entity = mActivities.get(position);
                System.out.println("Entity ID: " + entity.getListId());
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("elevationArray", entity.getElevationArray());
                bundle.putParcelableArrayList("speedArray", entity.getSpeedArray());
                bundle.putDouble("distance",entity.getDistance());
                bundle.putDouble("topSpeed", entity.getSpeed());
                bundle.putDouble("averagePace",entity.getPace());
                bundle.putString("date",entity.getDate());
                bundle.putString("time",formatted);
                Navigation.findNavController(v).navigate(R.id.action_useractivity_fragment_to_stats_fragment, bundle);
            }
        });
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
