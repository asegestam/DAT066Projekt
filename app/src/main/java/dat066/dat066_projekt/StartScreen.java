package dat066.dat066_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class StartScreen extends AppCompatActivity{

    private static final String TAG = "StartScreen";
    int age = 0;
    int weight = 0;
    String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set contentview to activity_main
        setContentView(R.layout.start_screen);
    }

    public void init(View view) {
        Log.d(TAG, "Weight: " + getWeight());
        Log.d(TAG, "Age: " + getAge());
        Log.d(TAG, "Gender: " + getGender());
        ProfileSaver pf = new ProfileSaver(this, getWeight(), getAge(), getGender() );
        pf.saveInfo();
        pf.getInfo();
        Intent intent = new Intent(StartScreen.this, MapsActivity.class);
        startActivity(intent);
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
