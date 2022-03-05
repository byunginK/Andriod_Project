package fastcampus.aop.part2.chapter12.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    //@SerializedName = 실제 데이터의 명칭과 동일하게 맵핑
    @SerializedName("itemId") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String,

): Parcelable //직렬화를 가능하도록 설정
