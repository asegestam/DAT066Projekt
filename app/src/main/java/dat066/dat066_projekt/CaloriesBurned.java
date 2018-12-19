package dat066.dat066_projekt;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.time.LocalDate;
import java.time.Period;

import static android.content.ContentValues.TAG;

public class CaloriesBurned {
    private double calories;
    private String gender;
    private String age;
    private String weight;
    private String height;
    private String training;
    private double met;
    private double bsa;
    private double bmr;
    private long time = System.currentTimeMillis();

    public CaloriesBurned(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", "");
        weight = sharedPref.getString("weight", "");
        age = sharedPref.getString("age", "");
        height = sharedPref.getString("height", "");
        if(gender.equals("Male")){
            bmr = (13.7 * Double.parseDouble(weight)) + (5 * Double.parseDouble(height)) - (6.8 * calculateAge()) + 66;
        }
        else {
            bmr = (9.56 * Double.parseDouble(weight)) + (1.85 * Double.parseDouble(height)) - (4.68 * calculateAge()) + 665;
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


    public double CalculateCalories(String training, long time, double speed, double alpha) {
        double v = 16.67 * speed; // m/min
        double a = alpha; // gradient
        if(training == null) {
            training = "Running";
        }
        if(training.equals("Running")){
            //met = (1.411 * Math.pow(speed, 0.8435));
            //calories = (bmr/24) * met * time/3600000;
            double horizontal = (v * 0.2);
            double vertical = (a * v * 0.9);
            calories = ((horizontal + vertical) * Double.parseDouble(weight)/1000) * 5.05 * time/60000;
        }
        else if(training.equals("Cycling")){
            //met = 0.6923 * (speed * 3.6) + -6.727;
            //calories = ((bmr/24) * met * time/3600000)/4.184;

            bsa = Math.sqrt((Double.parseDouble(height) * Double.parseDouble(weight)/3600));
            double horizontal = 131 - (9.8 * speed) + 0.92 * Math.pow(speed, 2);
            double vertical = (271.5 * 0.0 * speed)/(0.236*bsa);
            calories = (horizontal + vertical) * time/3600000 * 0.86 * bsa;
        }
        else if(training.equals("Walking")){
            double horizontal = (v * 0.1);
            double vertical = (a * v * 1.8);
            calories = ((horizontal + vertical) * Double.parseDouble(weight)/1000) * 5.05 * time/60000;
        }
        return calories;

    }

    public void setTraining(String s){
        training = s;
    }

}
