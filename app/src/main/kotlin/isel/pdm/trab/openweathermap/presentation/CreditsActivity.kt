package isel.pdm.trab.openweathermap.presentation

import android.os.Bundle
import isel.pdm.trab.openweathermap.R


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
    }
}
