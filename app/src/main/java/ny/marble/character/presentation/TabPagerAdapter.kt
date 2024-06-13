package ny.marble.character.presentation

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = Tab.entries.size

    override fun createFragment(position: Int): Fragment {
        assert(position in 0..Tab.entries.lastIndex)

        return when(Tab.entries[position]) {
            Tab.SEARCH -> SearchFragment.newInstance()
            Tab.FAVORITE -> FavoriteFragment.newInstance()
        }
    }
}

enum class Tab(val title: String) {
    SEARCH("search"), FAVORITE("favorite")
}