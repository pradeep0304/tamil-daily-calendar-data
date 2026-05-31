package com.example.data

import kotlinx.coroutines.flow.Flow

class PoruthamRepository(private val dao: PoruthamDao) {
    fun getAllReports(): Flow<List<PoruthamReportEntity>> = dao.getAllReports()
    
    suspend fun saveReport(groomName: String, brideName: String, score: Int) {
        dao.insertReport(
            PoruthamReportEntity(
                groomName = groomName,
                brideName = brideName,
                harmonyScore = score
            )
        )
    }
}
