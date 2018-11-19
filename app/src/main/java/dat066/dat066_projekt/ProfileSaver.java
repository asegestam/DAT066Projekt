package dat066.dat066_projekt;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileSaver {

    private Context mContext;
    private String gender;
    private int weight;
    private int age;


    public ProfileSaver(Context context){
            this.mContext = context;
    }

    /**
     *  Gets input from screen and saves it with shared preferences
     */
    public void saveInfo(){
        SharedPreferences sharedPref = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("gender", "reee"/*input*/);
        editor.putInt("weight", 5/*input*/);
        editor.putInt("age", 3/*input*/);
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
