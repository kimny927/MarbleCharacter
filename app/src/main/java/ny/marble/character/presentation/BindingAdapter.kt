package ny.marble.character.presentation

import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import ny.marble.character.R

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("thumbnail")
    fun ImageView.loadThumbnail(url: String) {
        Glide.with(this).load(url)
            .centerCrop()
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("favorite")
    fun CardView.setFavoriteBackground(isFavorite : Boolean) {
        this.setCardBackgroundColor(
            ContextCompat.getColor(this.context, if(isFavorite) R.color.favorite else R.color.white)
        )
    }
}