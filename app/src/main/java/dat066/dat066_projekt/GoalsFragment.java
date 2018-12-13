package dat066.dat066_projekt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class GoalsFragment extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Goals> lstGoal = new ArrayList<>();
    RecyclerViewAdapter recyclerAdapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_goals, container, false);

        sharedPref = getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Button createGoalButton = (Button) v.findViewById(R.id.createGoal_button);
        createGoalButton.setOnClickListener(createGoalButtonClickListener);

        myrecyclerview = (RecyclerView) v.findViewById(R.id.goals_recyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter  = new RecyclerViewAdapter(getContext(),lstGoal);
        myrecyclerview.setAdapter(recyclerAdapter);
        return v;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);





            getActivity().setTitle("Goals-Fragment");
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstGoal.add(new Goals("fortfort","Type: springer","10", "100", "10"));
        lstGoal.add(new Goals("fast","Type: running","30", "120", "20"));



    }

    private View.OnClickListener createGoalButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("New Goal");


            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.goals_popup, (ViewGroup) getView(), false);
            // Set up the input

            final EditText inputTitle = (EditText) viewInflated.findViewById(R.id.inputTitle);
            final EditText inputType = (EditText) viewInflated.findViewById(R.id.inputType);
            final EditText inputEndGoal = (EditText) viewInflated.findViewById(R.id.inputEndGoal);
            final EditText inputTime = (EditText) viewInflated.findViewById(R.id.inputTime);

            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(viewInflated);
            System.out.println("hej6");

            // Set up the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    //String weight = inputTitle.getText().toString();

                    lstGoal.add(new Goals(inputTitle.getText().toString(),"Type: " + inputType.getText().toString(),inputEndGoal.getText().toString()
                            + " km", inputTime.getText().toString() + " Days", "0 km"));


                    recyclerAdapter.notifyItemInserted(lstGoal.size() - 1);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();





            //lstGoal.add(new Goals("fast","Type: running","30", "120"));

        }
    };
}


