package isel.pdm.trab.openweathermap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView

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
            System.err.println("Error getting weather image")
            //TODO probably add a default error image so in case we have an error we display it (hint: sun with a X in bottom right corner)
            //activity_current_day.curday_image.setImageBitmap(icon)
        }

        return icon
    }

    override fun onPostExecute(result: Bitmap) {
        imageView.setImageBitmap(result)
    }
}
