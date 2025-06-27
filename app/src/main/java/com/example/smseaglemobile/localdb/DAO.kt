package com.example.smseaglemobile.localdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Entity(tableName = "api_config")
data class ApiConfigEntity(
    @PrimaryKey
    val id: Int = 1, // Zawsze będzie tylko jeden rekord
    val baseUrl: String,
    val apiToken: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface ApiConfigDao {

    @Query("SELECT * FROM api_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): ApiConfigEntity?

    @Query("SELECT * FROM api_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<ApiConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ApiConfigEntity)

    @Update
    suspend fun updateConfig(config: ApiConfigEntity)

    @Query("DELETE FROM api_config")
    suspend fun clearConfig()

    @Query("SELECT COUNT(*) > 0 FROM api_config WHERE id = 1")
    suspend fun hasConfig(): Boolean
}