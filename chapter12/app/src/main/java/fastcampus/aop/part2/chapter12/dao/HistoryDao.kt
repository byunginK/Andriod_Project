package fastcampus.aop.part2.chapter12.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fastcampus.aop.part2.chapter12.model.History

@Dao
interface HistoryDao {
    @Query("SELECT*FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history WHERE keyWord == :keyWord")
    fun delete(keyWord: String)
}