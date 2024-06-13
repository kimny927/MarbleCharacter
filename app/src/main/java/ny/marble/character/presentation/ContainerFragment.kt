package ny.marble.character.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ny.marble.character.R

private const val ARG_INITIAL_TAB = "INITIAL_TAB"

class ContainerFragment : Fragment() {
    private var initialTab: Tab? = null

    private val pagerAdapter : TabPagerAdapter by lazy {
        TabPagerAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initialTab = it.getSerializable(ARG_INITIAL_TAB, Tab::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager)
        viewPager.adapter = pagerAdapter
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            assert(position in 0..Tab.entries.lastIndex)
            tab.text = Tab.entries[position].title
        }.attach()
        if(savedInstanceState == null) {
            initialTab?.let {
                tabLayout.getTabAt(it.ordinal)?.select()
            }
        }
    }

    companion object {
        fun newInstanceWithInitialTab(tab: Tab) =
            ContainerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_INITIAL_TAB, tab)
                }
            }
    }
}