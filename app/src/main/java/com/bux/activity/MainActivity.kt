package com.bux.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bux.R
import com.bux.adapter.BaseRecyclerAdapter
import com.bux.adapter.ProductAdapter
import com.bux.domain.model.Product
import com.bux.presenter.MainPresenter
import com.bux.presenter.contract.MainContract
import com.bux.util.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.core.KoinComponent

/**
 * TODO move everything out of here
 */
class MainActivity : BaseActivity(), KoinComponent, MainContract.View {

    private val LOG_TAG = this::class.java.simpleName

    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.activity_main_recyclerview_products)
        val emptyView: View = findViewById(R.id.activity_main_progress)

        setPresenter(MainPresenter(this))

        // TODO repository

        // TODO view binding

        productAdapter = ProductAdapter(this, object : BaseRecyclerAdapter.ClickListener<Product> {
            override fun onItemClick(item: Product, position: Int) {
                presenter.click(item)
            }
        })

        productAdapter.setEmptyView(recyclerView, emptyView)

        recyclerView.apply {
            adapter = productAdapter
        }

    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun showList(items: List<Product>) {
        productAdapter.setItems(items)
    }

    override fun showDetail(item: Product) {
        Logger.i(LOG_TAG, "showDetail(${item.displayName})")

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)

        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra(SECURITY_ID, item.securityId)
        startActivity(intent, options.toBundle())
    }

    override fun onResume() {
        super.onResume()
        this.presenter.start()
    }

    override fun onPause() {
        super.onPause()

        presenter.stop()
    }



}
