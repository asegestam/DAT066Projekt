package dat066.dat066_projekt;

import android.text.format.Time;

public class CaloriesBurned {
    private double calories;
    private SpeedDistanceCalculator velocity;
    private ProfileSaverScreen profile;
    private String training;
    private double met;
    private double bmr;
    private long time = System.currentTimeMillis();

    public CaloriesBurned(SpeedDistanceCalculator velocity){
        this.velocity = velocity;
        profile = new ProfileSaverScreen();
        if(profile.getGender().equals("Male")){
           // bmr = (13.75 * profile.getWeight()) + /*(5 * profile.getHeight())*/ - (6.76 * profile.getAge()) + 66;
        }
        else {
           // bmr = (9.56 * profile.getWeight()) + /*(1.85 * profile.getHeight())*/ - (4.68 * profile.getAge()) + 665;
        }

    }


    public double CalculateCalories() {

        if(training.equals("Running")){
            met = 0.816 * velocity.getAverageSpeed() + 1.662;
            //calories = (bmr/24) * met * tid
        }
        else if(training.equals("Cycling")){

        }
        return calories;
    }

    public void setTraining(String s){
        training = s;
    }

}
