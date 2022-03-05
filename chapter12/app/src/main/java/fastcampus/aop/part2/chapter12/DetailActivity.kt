package fastcampus.aop.part2.chapter12

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import fastcampus.aop.part2.chapter12.databinding.ActivityDetailBinding
import fastcampus.aop.part2.chapter12.model.Book
import fastcampus.aop.part2.chapter12.model.Review

class DetailActivity: AppCompatActivity() {

    private lateinit var bindings: ActivityDetailBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        bindings = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(bindings.root)
        db = getAppDatabase(this)
        //데이터베이스를 추가

        //Log.d("DetailActivity", "start activity")
        val model = intent.getParcelableExtra<Book>("bookModel")
        //Log.d("DetailActivity",model.toString())
        bindings.titleTextView.text = model?.title.orEmpty()
        bindings.descriptionTextView.text = model?.description.orEmpty()

        Glide.with(bindings.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(bindings.coverImageView)
//        Thread{
//            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
//            runOnUiThread {
//                bindings.reviewTextView.setText(review?.review.orEmpty())
//            }
//        }.start()
//        bindings.saveButton.setOnClickListener {
//            Thread{
//                db.reviewDao().saveReview(
//                    Review(model?.id?.toInt() ?: 0,
//                    bindings.reviewTextView.text.toString())
//                )
//            }.start()
//        }
    }
}