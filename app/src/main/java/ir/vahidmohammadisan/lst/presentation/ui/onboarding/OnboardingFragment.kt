package ir.vahidmohammadisan.lst.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ir.vahidmohammadisan.lst.R
import ir.vahidmohammadisan.lst.databinding.FragmentOnboardingBinding


@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = OnboardingViewPagerAdapter(requireActivity(), requireActivity())
        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ -> }.attach()
        binding.viewPager.offscreenPageLimit = 1

        binding.btnCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.wallet)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}