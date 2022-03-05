package fastcampus.aop.part2.chapter12.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fastcampus.aop.part2.chapter12.model.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE id == :id")
    fun getOneReview(id: Int?): Review

    //db에 이미 있다면 업데이트를 하는 전략
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review: Review)
}