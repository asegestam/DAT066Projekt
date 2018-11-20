package dat066.dat066_projekt;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileSaver {

    private Context mContext;
    private String gender;
    private int weight;
    private int age;
    private StartScreen startScreen;


    public ProfileSaver(Context context, int weight, int age, String gender){
            this.mContext = context;
            startScreen = new StartScreen();
            this.weight = weight;
            this.age = age;
            this.gender = gender;
    }

    /**
     *  Gets input from screen and saves it with shared preferences
     */
    public void saveInfo(){
        SharedPreferences sharedPref = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("gender", gender);
        editor.putInt("weight", weight);
        editor.putInt("age", age);
        editor.commit();
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
    public int getAge() {
        return age;
    }
    public int getWeight() {
        return weight;
    }
    public String getGender(){
        return gender;
    }
}
