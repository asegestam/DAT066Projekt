package dat066.dat066_projekt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView mDisplayUsername;
    private TextView mDisplayWeight;
    private TextView mDisplayHeight;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating a new ProfileFragment");
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPref = getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        usernameOnClickListener(v);
        weightOnClickListener(v);
        heightOnClickListener(v);
        birthDayOnClickListener(v);
        getSharedPreferences(v);
        genderOnClickListener(v);
        //Get sharedpreferences data
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void getSharedPreferences(View v) {
        String gender = sharedPref.getString("gender", "");
        String weight = sharedPref.getString("weight", "");
        String height = sharedPref.getString("height", "");
        String age = sharedPref.getString("age", "");
        String userName = sharedPref.getString("username", "");
        mDisplayDate.setText(age);
        mDisplayUsername.setText(userName);
        mDisplayWeight.setText(weight + " kg");
        mDisplayHeight.setText(height + " cm");
        if (gender.equals("Male")) {
            RadioButton b = v.findViewById(R.id.male);
            b.toggle();
        } else {
            RadioButton b = v.findViewById(R.id.female);
            b.toggle();
        }

    }

    private void weightOnClickListener(View v) {

        mDisplayWeight = (TextView) v.findViewById(R.id.tvWeight);
        mDisplayWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Weight");

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.weight_popup, (ViewGroup) getView(), false);
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
                        if (!weight.isEmpty()) {
                            editor.putString("weight", weight);
                            editor.apply();
                        }

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

    private void heightOnClickListener(View v) {

        mDisplayHeight = (TextView) v.findViewById(R.id.tvHeight);
        mDisplayHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Height");

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.height_popup, (ViewGroup) getView(), false);
                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.inputHeight);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String height = input.getText().toString();
                        mDisplayHeight.setText(height + " cm");
                        if (!height.isEmpty()) {
                            editor.putString("height", height);
                            editor.apply();
                        }

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

    private void usernameOnClickListener(View v) {

        mDisplayUsername = (TextView) v.findViewById(R.id.tvUsername);
        mDisplayUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Full Name");

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.username_popup, (ViewGroup) getView(), false);
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
                        if (!username.isEmpty()) {
                            editor.putString("username", username);
                            editor.apply();
                        }
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

    private void birthDayOnClickListener(View v) {
        mDisplayDate = (TextView) v.findViewById(R.id.tvDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
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
                // month is returned 0-11
                month = month + 1;
                String date;
                Log.d(TAG, "onDateSet: yyyy/mm/dd:" + year + "/" + month + "/" + dayOfMonth);
                if(month < 10 && dayOfMonth > 10){
                    date = "" + year + "/"  + String.format("%02d", month) + "/" + dayOfMonth;
                }
                else if(month > 10 && dayOfMonth < 10){
                    date = "" + year + "/"  + month + "/" + String.format("%02d", dayOfMonth);
                }
                else if(month < 10 && dayOfMonth < 10){
                    date = "" + year + "/"  + String.format("%02d", month) + "/" + String.format("%02d", dayOfMonth);
                }
                else {
                    date = "" + year + "/"  + month + "/" + dayOfMonth;
                }
                mDisplayDate.setText(date);
                editor.putString("age", date);
                editor.apply();
            }
        };
    }

    private void genderOnClickListener(View v) {
        final RadioGroup radioGenderGroup = (RadioGroup) (v.findViewById(R.id.sex));

               radioGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                String gender;

                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    switch(checkedId)
                    {
                        case R.id.male:
                            gender = "Male";
                            editor.putString("gender", gender);
                            editor.apply();
                            break;
                        case R.id.female:
                            gender = "Female";
                            editor.putString("gender", gender);
                            editor.apply();
                            break;
                    }
                }
            });
    }
}
