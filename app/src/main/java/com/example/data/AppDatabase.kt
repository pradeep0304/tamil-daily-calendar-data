package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "porutham_reports")
data class PoruthamReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groomName: String,
    val brideName: String,
    val dateSaved: Long = System.currentTimeMillis(),
    val harmonyScore: Int
)

@Dao
interface PoruthamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: PoruthamReportEntity)

    @Query("SELECT * FROM porutham_reports ORDER BY dateSaved DESC")
    fun getAllReports(): Flow<List<PoruthamReportEntity>>
}

@Database(entities = [PoruthamReportEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun poruthamDao(): PoruthamDao
}
