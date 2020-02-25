package com.bux.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.bux.R
import com.bux.presenter.DetailPresenter
import com.bux.presenter.contract.DetailContract
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.core.KoinComponent

const val SECURITY_ID = "SECURITY_ID"

/**
 * TODO move everything out of here
 */
class DetailActivity : BaseActivity(), KoinComponent, DetailContract.View {

    private val LOG_TAG = this::class.java.simpleName

    private lateinit var vRoot: View

    private lateinit var tvClosingPrice: TextView
    private lateinit var tvLatency: TextView

    private lateinit var tvCurrentPrice: TextView
    private lateinit var tvPercentage: TextView
    private lateinit var ivArrow: ImageView

    private lateinit var presenter: DetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        vRoot = findViewById(R.id.activity_detail_rootView)
        tvClosingPrice = findViewById(R.id.activity_detail_closing_price)
        tvLatency = findViewById(R.id.activity_detail_latency)

        tvCurrentPrice = findViewById(R.id.activity_detail_currentPrice)
        tvPercentage = findViewById(R.id.activity_detail_percentage)
        ivArrow = findViewById(R.id.activity_detail_arrow)

        setPresenter(DetailPresenter(this))
    }

    override fun setPresenter(presenter: DetailContract.Presenter) {
        this.presenter = presenter as DetailPresenter
    }


    override fun setDisplayName(name: String) {
        toolbar.title = name
    }

    override fun setClosingPrice(price: String) {
        tvClosingPrice.text = price
    }

    override fun setCurrentPrice(price: String) {
        tvCurrentPrice.text = price
    }

    override fun setArrowUp() {
        ivArrow.setImageResource(R.drawable.ic_arrow_up)
    }

    override fun setArrowDown() {
        ivArrow.setImageResource(R.drawable.ic_arrow_down)
    }

    override fun setPercentage(percentage: String) {
        tvPercentage.text = percentage
    }

    override fun setLatency(latency: String, @ColorRes color: Int) {
        tvLatency.text = latency
        tvLatency.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun setError(message: String, duration: Int) {
        val bar = Snackbar.make(vRoot, message, duration)
        bar.setAction(android.R.string.ok) {
            bar.dismiss()
        }
        bar.view.setBackgroundColor(ContextCompat.getColor(bar.view.context, R.color.king_red))
        bar.show()
    }

    override fun setError(@StringRes message: Int, duration: Int) {
        val bar = Snackbar.make(vRoot, message, duration)
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
