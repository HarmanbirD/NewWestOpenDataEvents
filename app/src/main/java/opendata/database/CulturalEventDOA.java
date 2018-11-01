package opendata.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CulturalEventDOA
{
    @Query("SELECT COUNT(*) FROM culturalEvents")
    int count();

    @Query("SELECT * FROM culturalEvents")
    List<CulturalEvent> getAll();

    @Query("SELECT name FROM culturalEvents")
    List<String> getAllNames();

    @Query("SELECT address FROM culturalEvents WHERE name LIKE :curName")
    String getAddress(String curName);

    @Insert
    void insertAll(CulturalEvent... culturalEvents);

    @Delete
    void delete(CulturalEvent culturalEvent);
}