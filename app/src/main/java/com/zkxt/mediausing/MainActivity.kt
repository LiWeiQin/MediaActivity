package com.zkxt.mediausing

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arcns.core.media.selector.EMedia
import com.arcns.core.media.selector.EMediaQuery
import com.arcns.core.media.selector.MediaSelectorViewModel
import com.arcns.core.util.EventObserver
import com.arcns.core.util.addShareData
import com.arcns.core.util.setActionBarAsToolbar
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zkxt.mediausing.databinding.ActivityMainBinding
import com.zkxt.mediausing.viewmodel.ViewModelActivityMain
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    companion object {
        const val MEDIA_REQUEST_CODE = 200
        const val MEDIA_RESULT_CODE = 200
        const val FINISH_FORM_DETAIL_CODE = "是从详情页点击了完成"
        const val FINISH_FORM_LIST_CODE = "是从列表点击了完成"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModelActivityMain by viewModels()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            it.getIntExtra(FINISH_FORM_DETAIL_CODE, -1).run {
                if (this == MEDIA_RESULT_CODE) viewModelMediaSelector.onCompleteSelectedMedias()
            }
            it.getIntExtra(FINISH_FORM_LIST_CODE, -1).run {
                if (this == MEDIA_RESULT_CODE) viewModelMediaSelector.onCompleteSelectedMedias()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModelMediaSelector = ViewModelProvider(this).get(MediaSelectorViewModel::class.java)
        addShareData(this, viewModelMediaSelector)
        binding.viewModel = viewModel
        setActionBarAsToolbar(
            toolbar,
            isTopLevelDestination = true,
            menuResId = R.menu.global_add
        ) {
            when (it.itemId) {
                R.id.add -> {
                    openMediaSelector()
                }
            }
        }
        setupResult()
    }

    private lateinit var viewModelMediaSelector: MediaSelectorViewModel
    private fun openMediaSelector() {
        AndPermission.with(this)
            .runtime()
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .onGranted {
                viewModelMediaSelector.setupMediaSelector(
                    viewModel.currentFileSelectMaxSize,
                    isSetupFromMediaStore = true,
                    setupFromMediaStoreMediaQuerys = arrayOf(
                        EMediaQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        EMediaQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        //                            EMediaQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                    )
                )
                startActivityForResult(
                    Intent(
                        this@MainActivity,
                        MediaListActivity::class.java
                    ).apply {
                    }, MEDIA_REQUEST_CODE
                )
            }.onDenied {
                Toast.makeText(
                    this,
                    "选择文件功能需要使用访问存储权限，请您允许应用使用相应权限",
                    Toast.LENGTH_LONG
                ).show()
            }.start()
    }

    private fun openMediaDetail(item: EMedia, initMedias: List<EMedia>? = arrayListOf()) {
        viewModelMediaSelector.setupMediaSelector(
            isSelector = false,
            isSetupFromMediaStore = false,
            initMedias = initMedias,
            currentMedia = item,
            isOnlyPreview = true
        )
        startActivityForResult(
            Intent(
                this@MainActivity,
                MediaDetailActivity::class.java
            ).apply {
            }, MEDIA_REQUEST_CODE
        )
    }

    private fun setupResult() {
        viewModel.toast.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
        viewModelMediaSelector.resultSelectedMedias.observe(this, EventObserver {
            viewModel.setCurrentFiles(it)
        })
        viewModel.currentFiles.observe(this, Observer {
            (toolbar as Toolbar).menu.findItem(R.id.add)?.isEnabled =
                !(it.isNotEmpty() && it.size == viewModel.currentFileMaxSize)
            if (!(toolbar as Toolbar).menu.findItem(R.id.add)?.isEnabled!!) {
                (toolbar as Toolbar).menu.findItem(R.id.add)?.icon?.colorFilter =
                    PorterDuffColorFilter(Color.parseColor("#DBDBDB"), PorterDuff.Mode.SRC_IN)
            } else {
                (toolbar as Toolbar).menu.findItem(R.id.add)?.icon?.colorFilter =
                    PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
        })

        viewModel.clickMedia.observe(this, EventObserver {
            viewModelMediaSelector.onClickMedia(it)
        })
        viewModelMediaSelector.eventClickMedia.observe(this, EventObserver {
            openMediaDetail(it, viewModel.currentFiles.value)
        })
        viewModel.clickMediaAdd.observe(this, EventObserver {
            openMediaSelector()
        })
    }
}

