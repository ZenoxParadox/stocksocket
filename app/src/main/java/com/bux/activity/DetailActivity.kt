package com.bux.activity

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.bux.R
import com.bux.presenter.DetailPresenter
import com.bux.presenter.contract.DetailContract
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.toolbar

const val SECURITY_ID = "SECURITY_ID"

/**
 * Detail screen where the current price vs the closing price is displayed
 */
class DetailActivity : BaseActivity(), DetailContract.View {

    private val LOG_TAG = this::class.java.simpleName

    private lateinit var presenter: DetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        setPresenter(DetailPresenter(this))
    }

    override fun setPresenter(presenter: DetailContract.Presenter) {
        this.presenter = presenter as DetailPresenter
    }

    override fun setDisplayName(name: String) {
        toolbar.title = name
    }

    override fun setClosingPrice(price: String) {
        activity_detail_closing_price.text = price
    }

    override fun setCurrentPrice(price: String) {
        activity_detail_currentPrice.text = price
    }

    override fun setArrowUp() {
        activity_detail_arrow.setImageResource(R.drawable.ic_arrow_up)
    }

    override fun setArrowDown() {
        activity_detail_arrow.setImageResource(R.drawable.ic_arrow_down)
    }

    override fun setPercentage(percentage: String) {
        activity_detail_percentage.text = percentage
    }

    override fun setLatency(latency: String, @ColorRes color: Int) {
        activity_detail_latency.text = latency
        activity_detail_latency.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun setError(message: String, duration: Int) {
        val bar = Snackbar.make(activity_detail_rootView, message, duration)
        bar.setAction(android.R.string.ok) {
            bar.dismiss()
        }
        bar.view.setBackgroundColor(ContextCompat.getColor(bar.view.context, R.color.king_red))
        bar.show()
    }

    override fun setError(@StringRes message: Int, duration: Int) {
        val bar = Snackbar.make(activity_detail_rootView, message, duration)
        bar.setAction(android.R.string.ok) {
            bar.dismiss()
        }
        bar.view.setBackgroundColor(ContextCompat.getColor(bar.view.context, R.color.king_red))
        bar.show()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    override fun onResume() {
        super.onResume()

        val id = intent.getStringExtra(SECURITY_ID)

        val bundle = Bundle()
        bundle.putString(SECURITY_ID, id)
        this.presenter.start(bundle)
    }

}
