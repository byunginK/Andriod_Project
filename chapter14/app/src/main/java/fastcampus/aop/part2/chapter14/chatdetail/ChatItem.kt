package fastcampus.aop.part2.chapter14.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String
){
    constructor():this ("","")
}
