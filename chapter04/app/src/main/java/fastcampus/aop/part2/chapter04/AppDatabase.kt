package fastcampus.aop.part2.chapter04

import androidx.room.Database
import androidx.room.RoomDatabase
import fastcampus.aop.part2.chapter04.dao.HistoryDao
import fastcampus.aop.part2.chapter04.model.History

//버전을 입력해야 마이그레이션을 위해서 작성
@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}