package fastcampus.aop.part2.chapter12.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "keyWord") val keyWord: String?
)
