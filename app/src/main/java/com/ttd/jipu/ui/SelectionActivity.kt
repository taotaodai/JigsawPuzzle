package com.ttd.jipu.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ttd.jipu.R
import com.ttd.jipu.entity.Question
import com.ttd.jipu.utils.QuestionBank

/**
 * Created by wt on 2018/4/20.
 */
open class SelectionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_selection)

        val rvLevel = findViewById<RecyclerView>(R.id.rv_level)
        val adapter = SelectionAdapter(R.layout.adapter_level_item, SELECTIONS)
        rvLevel.adapter = adapter
        rvLevel.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        adapter.setOnItemClickListener { _, view, position ->
            val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SelectionActivity, view?.findViewById(R.id.iv_level) as View, getString(R.string.transition_level))
            val intent = Intent(this@SelectionActivity, GameTableActivity::class.java)
            intent.putExtra(Question::class.java.simpleName, SELECTIONS[position])
            ActivityCompat.startActivity(this@SelectionActivity, intent, compat.toBundle())
        }
        initToolBar()

    }

    private fun initToolBar() {
        val toolBar = findViewById<Toolbar>(R.id.tb_main)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener {
            ActivityCompat.finishAfterTransition(this)
        }
//        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    class SelectionAdapter(layoutResId: Int, data: List<Question>) : BaseQuickAdapter<Question, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder?, item: Question?) {
            val iv = helper?.getView<ImageView>(R.id.iv_level)
            iv?.setImageResource(item!!.imgRes)
        }

    }
    companion object {
        private val SELECTIONS: List<Question> = QuestionBank.getQuestions()
    }
}