package dat066.dat066_projekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.firebase.database.collection.LLRBNode;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dat066.dat066_projekt.database.UserActivityEntity;

public class UserActivityList extends Fragment {
    String TAG = "UserActivityList";
    View v;
    int id;

    public UserActivityList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.useractivity_list_fragment, container, false);
        setHasOptionsMenu(true);
        return v;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
        final UserActivityAdapter adapter = new UserActivityAdapter(this.getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final UserActivityViewModel activityViewModel = ViewModelProviders.of(getActivity()).get(UserActivityViewModel.class);

        activityViewModel.getAllUserActivites().observe(this, new Observer<List<UserActivityEntity>>() {
            @Override
            public void onChanged(List<UserActivityEntity> userActivities) {
                Log.d(TAG, "onChanged: database changed and i observed ");
                adapter.setActivities(userActivities);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear_database) {
            ViewModelProviders.of(getActivity()).get(UserActivityViewModel.class).deleteAllActivities();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
