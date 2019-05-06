package dat066.dat066_projekt.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {UserActivityEntity.class}, version = 2)
public abstract class UserActivityDatabase extends RoomDatabase {

    private static volatile UserActivityDatabase INSTANCE;

    public static UserActivityDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (UserActivityDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserActivityDatabase.class, "user_activity_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserActivityDao userActivityDao();

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

        }
    };

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
