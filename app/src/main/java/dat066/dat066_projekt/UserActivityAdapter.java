package dat066.dat066_projekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dat066.dat066_projekt.database.UserActivityEntity;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder> {

    class UserActivityViewHolder extends RecyclerView.ViewHolder {
        private final TextView activityItemView;

        private UserActivityViewHolder(View itemView) {
            super(itemView);
            activityItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<UserActivityEntity> mActivities = Collections.emptyList(); // Cached copy of words

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
        holder.activityItemView.setText(current.getDate() + "\n" + current.getDistance() + " m\n" + current.getSpeed() + " m/s\n" + current.getTime() + " s");
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
