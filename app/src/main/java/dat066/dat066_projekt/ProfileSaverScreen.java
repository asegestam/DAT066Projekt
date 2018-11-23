package dat066.dat066_projekt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class ProfileSaverScreen extends AppCompatActivity{

    private static final String TAG = "ProfileSaverScreen";
    String age = "";
    String weight = "";
    String gender = "";
    String userName = "";
    private Context mContext = this;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInfo();
        //set contentview to activity_main
        if((gender != null && !gender.isEmpty()) && (weight != null && !weight.isEmpty()) && (age != null && !age.isEmpty()) && (userName != null && !userName.isEmpty())) {
            Intent intent = new Intent(ProfileSaverScreen.this, MainActivity.class);
            startActivity(intent);

        }
        else{
            setContentView(R.layout.start_screen);
        }
        View v = findViewById(R.id.startscreen_container);
        usernameOnClickListener(v);
        weightOnClickListener(v);
        birthDayOnClickListener(v);
    }

    public void init(View view) {
        if(getGender() != null && getWeight() != null  && getAge() != null && getUserName() != null) {
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
        editor.putString("gender", getGenderFromLayout());
        editor.putString("weight", getWeightFromLayout());
        editor.putString("age", getAgeFromLayout());
        editor.putString("username", getUsernameFromLayout());

        editor.apply();
    }

    private void weightOnClickListener(View v){

        final TextView mDisplayWeight = (TextView) v.findViewById(R.id.initialWeight);
        mDisplayWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Weight");

                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.weight_popup, (ViewGroup) findViewById(R.id.startscreen_container), false);
                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.inputWeight);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String weight = input.getText().toString();
                        mDisplayWeight.setText(weight + " kg");
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void usernameOnClickListener(View v){

       final TextView mDisplayUsername = (TextView) v.findViewById(R.id.initialUsername);
        mDisplayUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Full Name");

                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.username_popup, (ViewGroup) findViewById(R.id.startscreen_container), false);
                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.inputUsername);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String username = input.getText().toString();
                        mDisplayUsername.setText(username);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void birthDayOnClickListener(View v){
        final TextView mDisplayDate = (TextView) v.findViewById(R.id.initialDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        mContext,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                //If you want the background to be transparent
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyyy:" + dayOfMonth + "/" + month + "/" + year);
                String date = "" + dayOfMonth + "/"  + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };
    }
    /**
     * Gets the user information from userInfo.xml and stores it in three variables
     */
    public void getInfo(){
        SharedPreferences sharedPref = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor Edit = sharedPref.edit();
        Edit.clear();
        Edit.apply();
        gender = sharedPref.getString("gender", "");
        weight = sharedPref.getString("weight", "");
        age = sharedPref.getString("age", "");
        userName = sharedPref.getString("username", "");
        Log.d(TAG, "getInfo: " + age + ", " + weight + gender);
    }

    public String getWeightFromLayout(){
            TextView weightET = (TextView) (findViewById(R.id.initialWeight));
            String[] weightText = weightET.getText().toString().split(" ");
            if (weightText.length == 0)
                return "";

            return weightText[0];
        }

    public String getAgeFromLayout(){
        TextView ageET = (TextView) (findViewById(R.id.initialDate));
        String ageText = ageET.getText().toString();
        if(ageText.isEmpty())
            return "";

        return ageText;
    }
    public String getGenderFromLayout(){
        RadioGroup radioGenderGroup = (RadioGroup) (findViewById(R.id.sex));
        if(radioGenderGroup.getCheckedRadioButtonId() != -1) {
            int selectedId = radioGenderGroup.getCheckedRadioButtonId();
            RadioButton radioGenderButton = (RadioButton) findViewById(selectedId);
            gender = radioGenderButton.getText().toString();
            return gender;
        }
        return null;
    }
    public String getUsernameFromLayout() {
        TextView nameET = (TextView) (findViewById(R.id.initialUsername));
        String nameText = nameET.getText().toString();
        if (nameText.isEmpty())
            return "";

        return nameText;
    }
    public String getGender(){
        return gender;
    }
    public String getWeight(){
        return weight;
    }
    public String getAge(){
       return age;
    }
    public String getUserName(){
        return userName;
    }

}