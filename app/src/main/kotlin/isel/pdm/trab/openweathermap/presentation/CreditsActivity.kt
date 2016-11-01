package isel.pdm.trab.openweathermap.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import isel.pdm.trab.openweathermap.R
import kotlinx.android.synthetic.main.activity_credits.*


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

    private fun navigateToUrlPage(address: Int) {
        val url = Uri.parse(resources.getString(address))
        startActivity(Intent(Intent.ACTION_VIEW, url))
    }
}
