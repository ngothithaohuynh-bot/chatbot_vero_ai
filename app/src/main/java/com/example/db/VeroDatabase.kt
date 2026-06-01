package com.example.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- ENTITIES ---

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user" or "vero"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_documents")
data class StudyDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val subject: String,
    val isCustom: Boolean = false,
    val citationSource: String = "" // Associated metadata or citation details
)

// --- DAO ---

@Dao
interface VeroDao {
    // Chat queries
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()

    // Document queries
    @Query("SELECT * FROM study_documents ORDER BY isCustom ASC, id ASC")
    fun getAllDocuments(): Flow<List<StudyDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: StudyDocumentEntity)

    @Query("DELETE FROM study_documents WHERE id = :id")
    suspend fun deleteDocument(id: Int)
}

// --- DATABASE ---

@Database(entities = [ChatMessageEntity::class, StudyDocumentEntity::class], version = 1, exportSchema = false)
abstract class VeroDatabase : RoomDatabase() {
    abstract fun veroDao(): VeroDao

    companion object {
        @Volatile
        private var INSTANCE: VeroDatabase? = null

        fun getDatabase(context: Context): VeroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VeroDatabase::class.java,
                    "vero_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
