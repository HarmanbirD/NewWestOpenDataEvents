package opendata.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CulturalEvent.class}, version = 1)
public abstract class CultureEventsDatabase
    extends RoomDatabase
{
    public abstract CulturalEventDOA culturalEventDOA();
}