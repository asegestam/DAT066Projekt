package dat066.dat066_projekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private View view;
    int age = 0;
    int weight = 0;
    String gender = "";
    private Context mContext = this.getActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button doneButton = (Button) view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(doneButtonClickListener);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().setTitle("Change Profile Inputs");
    }

    public void done() {
            Log.d(TAG, "Weight: " + getWeight());
            Log.d(TAG, "Age: " + getAge());
            Log.d(TAG, "Gender: " + getGender());
            saveInfo();
            getInfo();
            ((MainActivity)getActivity()).setFragment(R.id.activity_option);
    }

    /**
     *  Gets input from screen and saves it with shared preferences
     */
    public void saveInfo(){
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(getGender() != null && !getGender().isEmpty()) editor.putString("gender", getGender());
        if(getWeight() != 0) editor.putInt("weight", getWeight());
        if(getAge() != 0) editor.putInt("age", getAge());
        editor.apply();
    }
    /**
     * Gets the user information from userInfo.xml and stores it in three variables
     */
    public void getInfo(){
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", "");
        weight = sharedPref.getInt("weight", 0);
        age = sharedPref.getInt("age", 0);
        System.out.println(age + ", " + weight + gender);
    }

    public int getWeight(){
        EditText weightET = view.findViewById(R.id.weight);
        String weightText = weightET.getText().toString();
        if(weightText.isEmpty()) return 0;
        weight = Integer.valueOf(weightText);
        return weight;
    }
    public int getAge(){
        EditText ageET = view.findViewById(R.id.age);
        String ageText = ageET.getText().toString();
        if(ageText.isEmpty()) return 0;
        age = Integer.valueOf(ageText);
        return age;
    }
    public String getGender(){
        RadioGroup radioGenderGroup = view.findViewById(R.id.sex);
        if(radioGenderGroup.getCheckedRadioButtonId() != -1) {
            int selectedId = radioGenderGroup.getCheckedRadioButtonId();
            RadioButton radioGenderButton = view.findViewById(selectedId);
            gender = radioGenderButton.getText().toString();
            return gender;
        }
        return null;
    }

    /** Sets the onClickListeners to all relevant buttons */
    private View.OnClickListener doneButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            done();
        }
    };

}
