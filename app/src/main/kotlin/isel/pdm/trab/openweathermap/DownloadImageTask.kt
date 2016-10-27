package isel.pdm.trab.openweathermap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import android.widget.Toast

class DownloadImageTask(imageV : ImageView) : AsyncTask<String, Void, Bitmap>() {
    lateinit var imageView: ImageView

    init {
        this.imageView = imageV
    }

    override fun doInBackground(vararg uris: String): Bitmap? {
        val uri = uris[0]
        var icon: Bitmap? = null
        try {
            val stream = java.net.URL(uri).openStream()
            icon = BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            icon = BitmapFactory.decodeResource(imageView.context.getResources(), R.drawable.error_icon)
            Toast.makeText(imageView.context , R.string.could_not_download_icon_for_weather,Toast.LENGTH_SHORT)
        }
        return icon
    }

    override fun onPostExecute(result: Bitmap) {
        imageView.setImageBitmap(result)
    }
}
