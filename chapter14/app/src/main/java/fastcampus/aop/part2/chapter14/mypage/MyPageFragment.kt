package fastcampus.aop.part2.chapter14.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.chapter14.R
import fastcampus.aop.part2.chapter14.databinding.FragmentMypageBinding

class MyPageFragment: Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth:FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        fragmentMypageBinding.signInOutButton.setOnClickListener {
            binding?.let {
                val email = it.emailEditText.text.toString()
                val password = it.passwordEditText.text.toString()
                if(auth.currentUser == null){
                    //로그인
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()){ task ->
                            if(task.isSuccessful){
                                successSignIn()
                            }else{
                                Toast.makeText(context, "로그인에 실패",Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    //로그아웃
                    auth.signOut()
                    it.emailEditText.text.clear()
                    it.emailEditText.isEnabled = true
                    it.passwordEditText.text.clear()
                    it.passwordEditText.isEnabled = true

                    it.signInOutButton.text = "로그인"
                    it.signInOutButton.isEnabled = false
                    it.signUpButton.isEnabled =false
                }

            }
        }
        fragmentMypageBinding.signUpButton.setOnClickListener {
            binding?.let {
                val email = it.emailEditText.text.toString()
                val password = it.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()){ task ->
                        if(task.isSuccessful){
                            Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        fragmentMypageBinding.emailEditText.addTextChangedListener {
            binding?.let {
                val enable = it.emailEditText.text.isNotEmpty() && it.passwordEditText.text.isNotEmpty()
                it.signInOutButton.isEnabled = enable
                it.signUpButton.isEnabled = enable
            }
        }

        fragmentMypageBinding.passwordEditText.addTextChangedListener {
            binding?.let {
                val enable = it.emailEditText.text.isNotEmpty() && it.passwordEditText.text.isNotEmpty()
                it.signInOutButton.isEnabled = enable
                it.signUpButton.isEnabled = enable
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser == null){
            binding?.let { binding ->
                binding.emailEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.text.clear()
                binding.passwordEditText.isEnabled = true

                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled =false
            }

        }else{
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.setText("**********")
                binding.passwordEditText.isEnabled = false

                binding.signInOutButton.text = "로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled =false
            }
        }
    }

    private fun successSignIn() {
        if(auth.currentUser == null){
            Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
            return
        }
        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled =false
        binding?.signInOutButton?.text = "로그아웃"
    }
}