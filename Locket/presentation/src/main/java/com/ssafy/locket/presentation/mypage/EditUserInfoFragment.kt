package com.ssafy.locket.presentation.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.model.user.UserInfo
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentEditUserInfoBinding
import com.ssafy.locket.presentation.databinding.PopupJobMenuBinding
import com.ssafy.locket.presentation.home.UserInfoState
import com.ssafy.locket.presentation.home.UserInfoViewModel
import com.ssafy.locket.presentation.utils.CommonUtils.expandTouchArea
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class EditUserInfoFragment : BaseFragment<FragmentEditUserInfoBinding>(
    FragmentEditUserInfoBinding::bind,
    R.layout.fragment_edit_user_info
) {
    //수정하기 위한 여부
    var isJobSelected = false
    var isBirthValid = true

    //회원 정보 받아오기
    private val userInfoViewModel: UserInfoViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
    }

    fun initEvent(){
        binding.editAge.hint = "2000"
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.layoutAge.setOnClickListener {
            binding.editAge.requestFocus()
            binding.editAge.setSelection(binding.editAge.text.length)
            // 키보드 띄우기
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editAge, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.layoutJob.setOnClickListener {
            showPopupWindow(it)
        }
        binding.editAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                checkIfFormIsValid()
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // 4자리 숫자가 입력되었는지 확인
                isBirthValid = editable.toString().length == 4
                if (isBirthValid) {
                    binding.layoutAge.setBackgroundResource(R.drawable.bg_card_border_active) // 선택된 상태 테두리
                } else {
                    binding.layoutAge.setBackgroundResource(R.drawable.bg_card_border_inactive) // 기본 테두리
                }
                checkIfFormIsValid()  // 양식이 유효한지 확인하는 함수 호출
            }
        })

        binding.root.setOnTouchListener { _, _ ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editAge.windowToken, 0)
            binding.editAge.clearFocus() // 포커스 해제
            false
        }


        binding.btnChange.setOnClickListener {
            if (isBirthValid) {
                if(binding.editAge.text.toString().toInt()<=1930||binding.editAge.text.toString().toInt()>=2026){
                    isBirthValid = false
                    binding.editAge.text.clear()
                    Toast.makeText(requireContext(),"연도를 1930년도 이후나 2025년도 수정해 입력해주세요", Toast.LENGTH_LONG).show()
                }
                //정확한 값이 나왔을때
                else{
                    lifecycleScope.launch {
                        // 최신 사용자 정보 가져오기
                        val user = userDataStoreSource.user.first()

                        user?.let {
                            val updatedUser = it.copy(
                                birthYear = binding.editAge.text.toString().toInt(),
                                userJob = binding.tvJobSelect.text.toString()
                            )
                            Log.d("SignFragment", updatedUser.toString())

                            // 데이터 저장 (IO 스레드에서 실행)
                            withContext(Dispatchers.IO) {
                                userDataStoreSource.saveUser(updatedUser)
                                userInfoViewModel.updateUser(it.userId.toLong(),updatedUser)
                            }

                            // UI 업데이트는 Main 스레드에서 실행
                            withContext(Dispatchers.Main) {
                                binding.editAge.text.clear()
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.editUserInfoFragment, true) // 현재 Fragment 제거
                                    .setLaunchSingleTop(true)
                                    .build()

                                findNavController().navigate(
                                    R.id.action_editUserInfoFragment_to_myPageFragment,
                                    null,
                                    navOptions
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun showPopupWindow(view: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupBinding = PopupJobMenuBinding.inflate(inflater) // ViewBinding 사용

        // PopupWindow 설정
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        //크기 설정
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val popupWidth = (screenWidth * 0.85).toInt()  // 화면 너비의 90% 크기로 설정
        popupWindow.width = popupWidth
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(view, -view.x.toInt(), 0)

        val clickListener = View.OnClickListener { clickedView ->
            val jobTitle = when (clickedView.id) {
                R.id.popupItemStudent -> "무직"
                R.id.popupItemEmployee -> "직장인"
                R.id.popupItemSelfEmployed -> "자영업자"
                else -> return@OnClickListener
            }
            binding.tvJobSelect.text = jobTitle
            checkIfJobSelected()
            popupWindow.dismiss()
        }
        popupBinding.popupItemStudent.setOnClickListener(clickListener)
        popupBinding.popupItemEmployee.setOnClickListener(clickListener)
        popupBinding.popupItemSelfEmployed.setOnClickListener(clickListener)
    }

    private fun checkIfFormIsValid() {
        // 직업이 선택되었고, 생년월일이 4자리 숫자로 입력되었을 때만 버튼을 활성화
        if (isBirthValid) {
            binding.btnChange.isEnabled = true
            binding.btnChange.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorButtonActive))  // 활성화된 색상
        } else {
            binding.btnChange.isEnabled = false
            binding.btnChange.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorButtonInactive))  // 비활성화된 색상
        }
    }

    private fun checkIfJobSelected() {
        isJobSelected = binding.tvJobSelect.text.toString() != "직업을 선택해주세요"
        if (isJobSelected) {
            binding.layoutJob.setBackgroundResource(R.drawable.bg_card_border_active) // 선택된 상태 테두리
        } else {
            binding.layoutJob.setBackgroundResource(R.drawable.bg_card_border_inactive) // 기본 테두리
        }
    }

    fun initView() {
        lifecycleScope.launch {
            val user = userDataStoreSource.user.first() // 한 번만 가져옴
            user?.let {
                binding.tvNickname.text = it.nickname  // nickname을 TextView에 설정
                binding.tvJobSelect.text = it.userJob
                binding.editAge.setText(it.birthYear.toString())
            }
        }
    }

}