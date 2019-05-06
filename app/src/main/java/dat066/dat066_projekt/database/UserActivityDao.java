package dat066.dat066_projekt.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverter;

@Dao
public interface UserActivityDao {
    @Insert
    void insertActivity(UserActivityEntity userActivity);

    @Delete
    void deleteActivity(UserActivityEntity...activities);

    @Query("SELECT * FROM user_activity_table ORDER BY id ASC ")
    LiveData<List<UserActivityEntity>> getAllActivities();

    @Query("DELETE FROM user_activity_table")
    void deleteAllActivities();

    @Query("SELECT * FROM user_activity_table WHERE listId LIKE :ID")
    UserActivityEntity getActivity(int ID);

    @Query("SELECT COUNT(id) FROM user_activity_table")
    LiveData<Integer> getTableSize();

    @Query("DELETE FROM user_activity_table WHERE id LIKE :ID")
    void deleteActivity(int ID);

    @Query("UPDATE user_activity_table SET id=0")
    void resetID();

    @Query("UPDATE user_activity_table SET ListID = :listID-1 WHERE ListId > :listID")
    void updateActivities(int listID);
}
