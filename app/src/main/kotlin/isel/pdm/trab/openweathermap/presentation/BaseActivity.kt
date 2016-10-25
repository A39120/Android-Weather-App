package isel.pdm.trab.openweathermap.presentation

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu

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
        //setContentView(R.layout.activity_base)
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
            menuInflater.inflate(it, menu);
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }
}

