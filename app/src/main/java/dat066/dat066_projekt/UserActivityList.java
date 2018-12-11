package dat066.dat066_projekt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class UserActivityList extends ListFragment {
    View view;
    DatabaseReference data;
    ArrayAdapter<String> adapter;
    ArrayList<String> activities = new ArrayList<>();
    ListView listView;
    Bundle bundle;

    public UserActivityList(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.useractivity_list_fragment, container, false);
        bundle = this.getArguments();
        if(bundle != null) {
            String value = bundle.getString("date");
            activities.add(value);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_text, activities);
        listView = getListView();
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        data = FirebaseDatabase.getInstance().getReference();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getActivity(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public void onStart() {
        getActivity().setTitle("Your Saved Activities");
        super.onStart();

        data.child("Activity").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get Post object and use the values to update the UI
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    UserActivity user = ds.getValue(UserActivity.class);
                    if(user.dateTime != null) {
                        activities.add(user.dateTime.toString());
                        adapter.notifyDataSetChanged();
                    }
                }
            }



            @Override

            public void onCancelled(DatabaseError databaseError) {

                // Getting Post failed, log a message

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());


            }

        });

        // [END post_value_event_listener]



        // Keep copy of post listener so we can remove it when app stops

        //mPostListener = postListener;
    }

    public void updateListView(Bundle bundle){
        String value = bundle.getString("date");
        Log.e(TAG,"Value from bundle: "+value);
        activities.add(value);
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
}
