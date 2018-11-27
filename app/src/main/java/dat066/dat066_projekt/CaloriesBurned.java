package dat066.dat066_projekt;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.PeriodicSync;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.util.Log;

import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.Period;

import static android.support.constraint.Constraints.TAG;

public class CaloriesBurned {
    private double calories;
    private String gender;
    private String age;
    private String weight;
    private String height;
    private String training;
    private double met;
    private double bmr;
    private long time = System.currentTimeMillis();

    public CaloriesBurned(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", "");
        weight = sharedPref.getString("weight", "");
        age = sharedPref.getString("age", "");
        height = sharedPref.getString("height", "");
        if(gender.equals("Male")){
           bmr = (13.75 * Double.parseDouble(weight)) + (5 * Double.parseDouble(height)) - (6.76 * calculateAge() + 66);
        }
        else {
           bmr = (9.56 * Double.parseDouble(weight)) + (1.85 * Double.parseDouble(height)) - (4.68 * Double.parseDouble(age)) + 665;
        }
        System.out.println(bmr);
    }

    @TargetApi(26)
    public int calculateAge(){

        LocalDate birthday = LocalDate.parse(age.replaceAll("/", "-"));
        LocalDate today = LocalDate.now();
        Period p = Period.between(birthday, today);
        return p.getYears();
    }


    public double CalculateCalories(double speed, int time) {

        if(training.equals("Running")){
            met = 0.816 * speed + 1.662;
            calories = (bmr/24) * met * time;
        }
        else if(training.equals("Cycling")){

        }
        return calories;
    }

    public void setTraining(String s){
        training = s;
    }

}
