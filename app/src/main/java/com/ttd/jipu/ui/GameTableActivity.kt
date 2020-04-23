package com.ttd.jipu.ui

import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ttd.jipu.widget.JipuView
import com.ttd.jipu.entity.Question
import com.ttd.jipu.R


class GameTableActivity : BaseActivity() {
    lateinit var jpvMain: JipuView
    lateinit var question: Question
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_table)

        question = intent.getSerializableExtra(Question::class.java.simpleName) as Question
        jpvMain = findViewById(R.id.jpv_main) as JipuView
        jpvMain.imgRes = question.imgRes
        jpvMain.saturability = question.saturability
        jpvMain.row = question.row
        jpvMain.col = question.col

        jpvMain.shelterCount = question.shelterCount
        jpvMain.shelterRes = R.drawable.ic_blank
        /**
         * 横竖屏切换
         */
//        requestedOrientation = if(question.col > question.row){
//            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        }else{
//            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_reset -> {
                jpvMain.initPuzzle()
                jpvMain.createRandomBoard()
//                jpvMain.invalidate()
//                Observable.create(Observable.OnSubscribe<String> { t ->
//                    jpvMain.createRandomBoard()
//                    t?.onNext("")
//                }).subscribeOn(Schedulers.newThread())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(object : Subscriber<String>() {
//                            override fun onNext(t: String?) {
//                                jpvMain.invalidate()
//                            }
//
//                            override fun onCompleted() {
//                            }
//
//                            override fun onError(e: Throwable?) {
//                            }
//                        })
            }
            R.id.action_play -> {
                jpvMain.initPuzzle()
                jpvMain.createRandomBoard()
                jpvMain.invalidate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsPanel(view: View?, menu: Menu?): Boolean {
        if (menu != null) {
            if (menu.javaClass == MenuBuilder::class.java) {
                try {
                    val m = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
                    m.isAccessible = true
                    m.invoke(menu, true)
                } catch (e: Exception) {

                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu)
    }


}
