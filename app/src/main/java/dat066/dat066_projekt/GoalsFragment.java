package dat066.dat066_projekt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class GoalsFragment extends Fragment {

    LayoutInflater layoutInflater;
    LinearLayout mparent;
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_goals, container, false);

        Button createGoalButton = (Button) v.findViewById(R.id.createGoal_button);
        createGoalButton.setOnClickListener(createGoalButtonClickListener);


        return v;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);


            mparent = v.findViewById(R.layout.fragment_goals);



            getActivity().setTitle("Goals-Fragment");
    }

    private View.OnClickListener createGoalButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {


            View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
            listView1.addHeaderView(header);

            View myView = layoutInflater.inflate(R.layout.goals_example, null, false);


            mparent.addView(myView);


        }
    };
    public void createGoal(String activity, String type, int goal, int time){

    }
}


