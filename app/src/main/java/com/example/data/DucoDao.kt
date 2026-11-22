package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DucoDao {
  @Query("SELECT * FROM ducos ORDER BY createdAtMillis ASC")
  fun observeAllDucos(): Flow<List<DucoEntity>>

  @Query("SELECT * FROM ducos ORDER BY createdAtMillis ASC")
  suspend fun getAllDucosOnce(): List<DucoEntity>

  @Query("SELECT * FROM ducos WHERE id = :id LIMIT 1")
  suspend fun getDucoById(id: Long): DucoEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDuco(duco: DucoEntity): Long

  @Update
  suspend fun updateDuco(duco: DucoEntity)

  @Query("DELETE FROM ducos WHERE id = :id")
  suspend fun deleteDucoById(id: Long)

  @Query("UPDATE ducos SET count = count + 1 WHERE id = :id")
  suspend fun incrementCount(id: Long)

  @Query("UPDATE ducos SET count = 0 WHERE id = :id")
  suspend fun resetCount(id: Long)
}
