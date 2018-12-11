package dat066.dat066_projekt;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dat066.dat066_projekt.database.UserActivityEntity;

public class UserActivityList extends Fragment {
    String TAG = "UserActivityList";
    View v;
    public UserActivityList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.useractivity_list_fragment, container, false);
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
        final UserActivityAdapter adapter = new UserActivityAdapter(this.getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        UserActivityViewModel activityViewModel = ViewModelProviders.of(getActivity()).get(UserActivityViewModel.class);

        activityViewModel.getAllUserActivites().observe(this, new Observer<List<UserActivityEntity>>() {
            @Override
            public void onChanged(List<UserActivityEntity> userActivities) {
                Log.d(TAG, "onChanged: database changed and i observed ");
                adapter.setActivities(userActivities);
            }
        });
        super.onViewCreated(view, savedInstanceState);

    }
}
