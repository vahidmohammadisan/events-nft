package ir.vahidmohammadisan.lst.presentation.ui.onboarding

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.vahidmohammadisan.lst.R


class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingIntroFragment.newInstance(
                context.resources.getString(R.string.create_wallet),
                context.resources.getString(R.string.wallet_desc),
                R.raw.animation_wallet
            )

            1 -> OnboardingIntroFragment.newInstance(
                context.resources.getString(R.string.choose_plan),
                context.resources.getString(R.string.plan_desc),
                R.raw.animation_plan
            )

            else -> OnboardingIntroFragment.newInstance(
                context.resources.getString(R.string.driving_well),
                context.resources.getString(R.string.drive_safe),
                R.raw.animation_driving
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}