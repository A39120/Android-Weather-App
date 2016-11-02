package isel.pdm.trab.openweathermap.services


import android.graphics.Bitmap
import com.android.volley.toolbox.ImageLoader

/**
 * Implementation of a null cache, that is, a cache implementation that does not perform caching.
 */
class NullImageCache : ImageLoader.ImageCache {
    override fun getBitmap(url: String?): Bitmap? = null
    override fun putBitmap(url: String?, bitmap: Bitmap?) = Unit
}
