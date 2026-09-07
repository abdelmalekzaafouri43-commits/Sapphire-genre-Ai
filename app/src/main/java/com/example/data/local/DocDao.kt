package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter
    fun fromDocType(value: DocType): String = value.name

    @TypeConverter
    fun toDocType(value: String): DocType = try {
        DocType.valueOf(value)
    } catch (e: Exception) {
        DocType.WORKSHEET
    }
}

@Dao
interface DocDao {
    @Query("SELECT * FROM generated_docs ORDER BY id DESC")
    fun getAllDocs(): Flow<List<GeneratedDoc>>

    @Query("SELECT * FROM generated_docs WHERE docType = :docType ORDER BY id DESC")
    fun getDocsByType(docType: DocType): Flow<List<GeneratedDoc>>

    @Query("SELECT * FROM generated_docs WHERE id = :id")
    suspend fun getDocById(id: Long): GeneratedDoc?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoc(doc: GeneratedDoc): Long

    @Query("UPDATE generated_docs SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM generated_docs WHERE id = :id")
    suspend fun deleteDoc(id: Long)

    @Query("SELECT COUNT(*) FROM generated_docs")
    fun getDocCount(): Flow<Int>
}
