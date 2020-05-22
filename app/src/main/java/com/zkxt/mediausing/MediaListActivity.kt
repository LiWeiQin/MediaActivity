package com.zkxt.mediausing

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.arcns.core.media.selector.MediaSelectorViewModel
import com.arcns.core.util.EventObserver
import com.arcns.core.util.setActionBarAsToolbar
import com.arcns.core.util.shareData
import com.zkxt.mediausing.databinding.ActivityMediaListBinding
import kotlinx.android.synthetic.main.activity_media_list.*
import kotlinx.android.synthetic.main.media_selector_layout_image_selector.*

class MediaListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaListBinding
    private val viewModelMediaSelector by shareData<MediaSelectorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_list)
        binding.lifecycleOwner = this
        binding.viewModel = viewModelMediaSelector
        setActionBarAsToolbar(toolbar, menuResId = R.menu.global_finish) {
            when (it.itemId) {
                R.id.menuComplete -> {
                    val intent = Intent(
                        this@MediaListActivity,
                        MainActivity::class.java
                    )
                    intent.putExtra(
                        MainActivity.FINISH_FORM_LIST_CODE,
                        MainActivity.MEDIA_RESULT_CODE
                    )
                    startActivity(
                        intent
                    )
                }
            }
        }
        setupResult()
    }

    override fun onRestart() {
        super.onRestart()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun setupResult() {
        viewModelMediaSelector?.toast?.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
        viewModelMediaSelector?.eventClickMedia?.observe(this, EventObserver {
            startActivityForResult(
                Intent(
                    this@MediaListActivity,
                    MediaDetailActivity::class.java
                ).apply {
                }, MainActivity.MEDIA_REQUEST_CODE
            )
        })
        viewModelMediaSelector?.eventClickPreview?.observe(this, EventObserver {
            startActivityForResult(
                Intent(
                    this@MediaListActivity,
                    MediaDetailActivity::class.java
                ).apply {
                }, MainActivity.MEDIA_REQUEST_CODE
            )
        })
        viewModelMediaSelector?.selectedMedias?.observe(this, Observer {
            (toolbar as Toolbar).menu.findItem(R.id.menuComplete)?.isEnabled =
                viewModelMediaSelector?.selectedMediasSize!! > 0
            (toolbar as Toolbar).menu.findItem(R.id.menuComplete)?.title =
                viewModelMediaSelector?.completeButtonText
        })
    }
}
