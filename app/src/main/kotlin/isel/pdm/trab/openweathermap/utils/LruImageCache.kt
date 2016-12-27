package isel.pdm.trab.openweathermap.utils

import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader


class LruImageCache(maxSize: Int) : LruCache<String, Bitmap>(maxSize), ImageLoader.ImageCache {

    override fun putBitmap(url: String?, bitmap: Bitmap?) {
        put(url, bitmap)
    }

    override fun getBitmap(url: String?): Bitmap? {
        return get(url)
    }


}