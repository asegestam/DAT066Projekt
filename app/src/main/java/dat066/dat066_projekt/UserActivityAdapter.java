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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.GraphViewXML;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import dat066.dat066_projekt.database.UserActivityEntity;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder> {

    private int id;

    public static class UserActivityViewHolder extends RecyclerView.ViewHolder {
        private final TextView activityItemView;

        private UserActivityViewHolder(View itemView) {
            super(itemView);
            activityItemView = itemView.findViewById(R.id.textView);
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
        UserActivityEntity current = mActivities.get(position);
        id = current.getDate().hashCode();
        holder.activityItemView.setId(current.getDate().hashCode());
        System.out.println("onBindViewHolder ID: "+current.getDate().hashCode());
        holder.activityItemView.setText(current.getDate() + "\n" + current.getDistance() + " m\n" + current.getSpeed() + " m/s\n" + current.getTime() + " s");
        holder.activityItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("View ID: " + v.getId());
                UserActivityEntity entity = mActivities.get(position);
                System.out.println("Entity ID: " + entity.getListId());
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("array", entity.getElevationArray());
                //v = setPlotData(entity.getElevationArray(), v);
                Navigation.findNavController(v).navigate(R.id.action_useractivity_fragment_to_stats_fragment, bundle);
                /*PopupWindow popupWindow = new PopupWindow(v, 750, 1250,true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.CYAN));
                v.setBackgroundColor(Color.WHITE);
                popupWindow.setAnimationStyle(R.style.AnimationPopup);
                popupWindow.showAtLocation(holder.activityItemView, Gravity.CENTER,0,0);*/
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


