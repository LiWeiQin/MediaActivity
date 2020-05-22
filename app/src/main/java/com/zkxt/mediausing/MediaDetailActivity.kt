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
import com.zkxt.mediausing.databinding.ActivityMediaDetailBinding
import kotlinx.android.synthetic.main.activity_media_list.*

class MediaDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaDetailBinding
    private val viewModel by shareData<MediaSelectorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_detail)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setActionBarAsToolbar(toolbar, menuResId = R.menu.global_finish) {
            when (it.itemId) {
                R.id.menuComplete -> {
                    binding.lifecycleOwner = null
                    val intent = Intent(
                        this@MediaDetailActivity,
                        MainActivity::class.java
                    )
                    intent.putExtra(
                        MainActivity.FINISH_FORM_DETAIL_CODE,
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


    override fun onDestroy() {
        super.onDestroy()
        if (viewModel?.isOnlyPreview?.value == true) {
            viewModel?.destroy()
        }
    }

    private fun setupResult() {
        viewModel?.toast?.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
        viewModel?.selectedMedias?.observe(this, Observer {
            (toolbar as Toolbar).menu.findItem(R.id.menuComplete)?.isEnabled =
                viewModel?.selectedMediasSize!! > 0
            (toolbar as Toolbar).menu.findItem(R.id.menuComplete)?.title =
                viewModel?.completeButtonText
        })
        viewModel?.currentMedia?.observe(this, Observer {
            binding.toolbar.tvTitle.text = viewModel?.detailsTitleText
        })
    }
}
