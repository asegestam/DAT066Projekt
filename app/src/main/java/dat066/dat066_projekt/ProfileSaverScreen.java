package dat066.dat066_projekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ProfileSaverScreen extends AppCompatActivity{

    private static final String TAG = "ProfileSaverScreen";
    int age = 0;
    int weight = 0;
    String gender = "";
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInfo();
        //set contentview to activity_main
        if((gender != null && !gender.isEmpty()) && weight != 0 && age != 0) {
            Intent intent = new Intent(ProfileSaverScreen.this, MainActivity.class);
            startActivity(intent);
        }
        else{
            setContentView(R.layout.start_screen);
        }
    }

    public void init(View view) {
        if((getGender() != null && !getGender().isEmpty()) && getWeight() != 0 && getAge() != 0) {
            Log.d(TAG, "Weight: " + getWeight());
            Log.d(TAG, "Age: " + getAge());
            Log.d(TAG, "Gender: " + getGender());
            //ProfileSaver pf = new ProfileSaver(this, getWeight(), getAge(), getGender() );
            saveInfo();
            Intent intent = new Intent(ProfileSaverScreen.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Please fill all requested fields above", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"ERROR ALL FIELDS NOT FILLED");
        }
    }

    /**
     *  Gets input from screen and saves it with shared preferences
     */
    public void saveInfo(){
        SharedPreferences sharedPref = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("gender", getGender());
        editor.putInt("weight", getWeight());
        editor.putInt("age", getAge());
        editor.apply();
    }
    /**
     * Gets the user information from userInfo.xml and stores it in three variables
     */
    public void getInfo(){
        SharedPreferences sharedPref = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", "");
        weight = sharedPref.getInt("weight", 0);
        age = sharedPref.getInt("age", 0);
        System.out.println(age + ", " + weight + gender);
    }

    public int getWeight(){
        EditText weightET = (EditText) (findViewById(R.id.weight));
        String weightText = weightET.getText().toString();
        if(weightText.isEmpty()) return 0;
        weight = Integer.valueOf(weightText);
        return weight;
    }
    public int getAge(){
        EditText ageET = (EditText) (findViewById(R.id.age));
        String ageText = ageET.getText().toString();
        if(ageText.isEmpty()) return 0;
        age = Integer.valueOf(ageText);
        return age;
    }
    public String getGender(){
        RadioGroup radioGenderGroup = (RadioGroup) (findViewById(R.id.sex));
        if(radioGenderGroup.getCheckedRadioButtonId() != -1) {
            int selectedId = radioGenderGroup.getCheckedRadioButtonId();
            RadioButton radioGenderButton = (RadioButton) findViewById(selectedId);
            gender = radioGenderButton.getText().toString();
            return gender;
        }
        return null;
    }
}