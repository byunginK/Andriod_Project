package fastcampus.aop.part2.chapter12

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fastcampus.aop.part2.chapter12.dao.HistoryDao
import fastcampus.aop.part2.chapter12.dao.ReviewDao
import fastcampus.aop.part2.chapter12.model.History
import fastcampus.aop.part2.chapter12.model.Review
//아래 리뷰dao를 추가할때는 version업데이트
@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

fun getAppDatabase(context: Context): AppDatabase{

    val migration_1_2= object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'REVIEW' ('ID' INTEGER,'review' TEXT,"+"PRIMARY KEY('id'))")
        }
    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    ).addMigrations(migration_1_2).build()
}