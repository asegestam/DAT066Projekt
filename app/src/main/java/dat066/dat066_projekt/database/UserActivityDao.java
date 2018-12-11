package dat066.dat066_projekt.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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

    @Query("SELECT COUNT(id) FROM user_activity_table")
    LiveData<Integer> getTableSize();
}
