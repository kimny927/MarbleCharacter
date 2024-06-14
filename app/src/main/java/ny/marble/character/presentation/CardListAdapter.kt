package ny.marble.character.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ny.marble.character.databinding.ItemCardBinding
import ny.marble.character.presentation.model.CharacterCardModel

class CardListAdapter(
    private val clickListener: (Int) -> Unit,
    private val bottomReachListener: ((Int) -> Unit)? = null
) :
    ListAdapter<CharacterCardModel, CardListAdapter.ViewHolder>(
        getDiffUtil()
    ) {

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), clickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position == itemCount - 1) {
            bottomReachListener?.invoke(itemCount)
        }

    }

    class ViewHolder(private val binding: ItemCardBinding, clickListener: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.item?.let { item ->
                    clickListener(item.id)
                }
            }
        }

        fun bind(item: CharacterCardModel) {
            binding.item = item
        }
    }

    companion object {
        fun getDiffUtil() = object : DiffUtil.ItemCallback<CharacterCardModel>() {
            override fun areItemsTheSame(oldItem: CharacterCardModel, newItem: CharacterCardModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CharacterCardModel,
                newItem: CharacterCardModel
            ) =
                oldItem.name == newItem.name &&
                        oldItem.favorite == newItem.favorite &&
                        oldItem.thumbnail == newItem.thumbnail &&
                        oldItem.description == newItem.description
        }
    }
}