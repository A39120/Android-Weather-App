package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import isel.pdm.trab.openweathermap.R
import kotlinx.android.synthetic.main.activity_credits.*

/**
 * Activity responsible for displaying the authors' information
 * and references to what was used in the project
 */
class CreditsActivity : BaseActivity() {

    override var layoutResId: Int = R.layout.activity_credits

    override val actionBarId: Int? = R.id.toolbar

    /**
     * @property actionBarMenuResId the menu resource identifier that specifies the toolbar's
     * contents, or null if the activity does not include a toolbar
     */
    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_credits

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        owmLogo.setOnClickListener { navigateToUrlPage(R.string.api_base_url)}
        person1Url.setOnClickListener { navigateToUrlPage(R.string.person1_url) }
        person2Url.setOnClickListener { navigateToUrlPage(R.string.person2_url) }
        person3Url.setOnClickListener { navigateToUrlPage(R.string.person3_url) }
    }

    /**
     * Method that starts the device's web browser and goes to a specific address
     * @param address an Int that points to a resource string containing the address string
     */
    private fun navigateToUrlPage(address: Int) {
        val url = Uri.parse(resources.getString(address))
        startActivity(Intent(Intent.ACTION_VIEW, url))
    }
}
