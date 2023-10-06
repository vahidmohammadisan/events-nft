package ir.vahidmohammadisan.lst.presentation.ui.nft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.vahidmohammadisan.lst.R
import ir.vahidmohammadisan.lst.databinding.FragmentNftQuestionSheetBinding

class CreateNftQuestionSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentNftQuestionSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNftQuestionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnYes.setOnClickListener {
            findNavController().navigate(R.id.nft)
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }

    }

}
