package fastcampus.aop.part2.chapter14.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.chapter14.DBKey.Companion.CHILD_CHAT
import fastcampus.aop.part2.chapter14.DBKey.Companion.DB_ARTICLES
import fastcampus.aop.part2.chapter14.DBKey.Companion.DB_USERS
import fastcampus.aop.part2.chapter14.R
import fastcampus.aop.part2.chapter14.chatlist.ChatListItem
import fastcampus.aop.part2.chapter14.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null
    private lateinit var articleAdapter: ArticleAdapter
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private val articleList = mutableListOf<ArticleModel>()

    //db에서 받은 child가 변경 될때 이벤트를 발생 할 수 있도록 함
    private val listener = object : ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model의 타입으로 데이터를 매핑해서 모델로 받을 수 있따.
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()
        //디비를 가져옴
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        userDB = Firebase.database.reference.child(DB_USERS)

        //홈화면에서 아이템을 클릭했을때 이벤트를 recycleView에서 람다식을 추가하고 생성
        articleAdapter = ArticleAdapter(onItemClicked = {
            if(auth.currentUser != null){
                //로그인 상태
                if(auth.currentUser?.uid != it.sellerId){
                    val chatRoom = ChatListItem(
                        buyerId = auth.currentUser!!.uid,
                        sellerId = it.sellerId,
                        itemTitle = it.title,
                        key = System.currentTimeMillis()
                    )
                    userDB.child(auth.currentUser!!.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)
                    userDB.child(it.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view,"채팅방이 생성되었습니다. 채팅탭을 확인해주세요.", Snackbar.LENGTH_LONG).show()
                }else{
                    //내가 올린 아이템
                    Snackbar.make(view,"내가 올린 아이템 입니다.", Snackbar.LENGTH_LONG).show()
                }
            }else{
                //로그인 안한 상태
                Snackbar.make(view,"로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }


        })

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            //context의 safe call을 사용하여 intent에 context에 넣어준다
            context?.let {
                if(auth.currentUser != null){
                    val intent = Intent(it,AddArticleActivity::class.java)
                    startActivity(intent)
                }else{
                    Snackbar.make(view,"로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
                }

            }

        }

        articleDB.addChildEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()

        articleDB.removeEventListener(listener)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        //뷰를 다시그럼
        articleAdapter.notifyDataSetChanged()
    }
}