package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "translation_history")
data class TranslationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val tone: String,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<TranslationRecord>>

    @Query("SELECT * FROM translation_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<TranslationRecord>>

    @Query("SELECT * FROM translation_history WHERE originalText LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<TranslationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: TranslationRecord): Long

    @Update
    suspend fun updateRecord(record: TranslationRecord)

    @Query("DELETE FROM translation_history WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM translation_history")
    suspend fun clearAllHistory()
}

@Database(entities = [TranslationRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "translation_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class TranslationRepository(private val dao: TranslationDao) {
    val allHistory: Flow<List<TranslationRecord>> = dao.getAllHistory()
    val allFavorites: Flow<List<TranslationRecord>> = dao.getFavorites()

    fun search(query: String): Flow<List<TranslationRecord>> = dao.searchHistory(query)

    suspend fun insert(record: TranslationRecord): Long = dao.insertRecord(record)

    suspend fun update(record: TranslationRecord) = dao.updateRecord(record)

    suspend fun delete(id: Long) = dao.deleteRecordById(id)

    suspend fun clearAll() = dao.clearAllHistory()
}
