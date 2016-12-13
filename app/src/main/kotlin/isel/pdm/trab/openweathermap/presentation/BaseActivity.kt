package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import isel.pdm.trab.openweathermap.*

/**
 * Abstract class to be extended by any other Activity,
 * that class has to define it's own layoytResId
 */
abstract class BaseActivity : AppCompatActivity() {

    protected abstract val layoutResId: Int

    /**
     * @property actionBarId the identifier of the toolbar as specified in the activity layout, or
     * null if the activity does not include a toolbar
     */
    protected open val actionBarId: Int? = null

    /**
     * @property actionBarMenuResId the menu resource identifier that specifies the toolbar's
     * contents, or null if the activity does not include a toolbar
     */
    protected open val actionBarMenuResId: Int? = null

    /**
     * Method used to initiate the activity layout and action bar, if one exists.
     */
    private fun initContents() {
        setContentView(layoutResId)
        actionBarId?.let {
            setSupportActionBar(findViewById(it) as Toolbar)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContents()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initContents()
    }

    /**
     * Callback method used to initiate the activity action bar by inflating its contents
     * @param savedInstanceState The menu instance where the action bar contents are to be placed
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        actionBarMenuResId?.let {
            menuInflater.inflate(it, menu)
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Method used to display a loading icon
     */
    protected fun setLoadingImg(img: ImageView){
        img.setImageBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.loading_icon))
    }
    /**
     * Method used to display an error icon showing a toast
     */
    protected fun setErrorImg(img: ImageView){
        img.setImageBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.error_icon))
        Toast.makeText(this , R.string.could_not_download_icon_for_weather, Toast.LENGTH_SHORT).show()
    }

    /**
     * Callback method invoked when an item from the options menu is selected
     * @param item MenuItem that was selected
     * @returns Returns true when the menu item is successfully handled
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_preferences -> {
            startActivity(Intent(this, PreferencesActivity::class.java))
            true
        }

        R.id.action_credits -> {
            startActivity(Intent(this, CreditsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}

