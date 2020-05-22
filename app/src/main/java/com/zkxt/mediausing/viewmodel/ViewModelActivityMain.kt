package com.zkxt.mediausing.viewmodel

import androidx.lifecycle.*
import com.arcns.core.media.selector.EMedia
import com.arcns.core.util.Event
import com.zkxt.mediausing.IGlobalFileListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Array.get
import java.util.*
import kotlin.collections.ArrayList

class ViewModelActivityMain : ViewModel(), IGlobalFileListener {

    // 正在加载或保存
    private var _loadIng = MutableLiveData<Boolean>()
    var loadIng: LiveData<Boolean> = _loadIng

    // 弹出提示
    private var _toast = MutableLiveData<Event<String>>()
    var toast: LiveData<Event<String>> = _toast
    fun showToast(value: String) {
        _toast.postValue(Event(value))
    }

    private var _currentFiles = MutableLiveData<ArrayList<EMedia>>().apply {
        value = ArrayList()
    }
    var currentFiles: LiveData<ArrayList<EMedia>> = _currentFiles

    var isShowAddItem = Transformations.map(currentFiles) {
        it?.let {
            it.size != currentFileMaxSize
        } ?: true
    }


    val currentFileMaxSize = 9
    val currentFileSelectMaxSize: Int
        get() = currentFileMaxSize - (currentFiles.value?.size ?: 0)

    var clickMedia = MutableLiveData<Event<EMedia>>()

    var clickMediaAdd = MutableLiveData<Event<Boolean>>()

    override fun onFileDel(item: EMedia) {
        viewModelScope.launch {
            delay(200)
            _currentFiles.value = _currentFiles.value?.let {
                val temp = ArrayList<EMedia>()
                temp.addAll(it)
                temp.remove(item)
                temp
            }
        }
    }

    /**
     * 点击媒体文件
     */
    fun onClickMedia(item: EMedia) {
        clickMedia.value = Event(item)
    }

    /**
     * 点击新增媒体文件
     */
    fun onClickMediaAdd() {
        clickMediaAdd.value = Event(true)
    }


    /**
     * 排序选中列表
     */
    fun onSortingSelectedMedias(fromPosition: Int, toPosition: Int) {
        _currentFiles.value = _currentFiles.value?.let {
            val temp = ArrayList<EMedia>().apply {
                addAll(it)
            }
            Collections.swap(temp, fromPosition, toPosition)
            temp
        }
    }

    fun setCurrentFiles(selectMedia: ArrayList<EMedia>?) {
        _currentFiles.value = _currentFiles.value?.let {
            var temp = ArrayList<EMedia>()
            temp.addAll(it)
            temp.addAll(selectMedia?.map { that ->
                that.apply {
                    itemID = UUID.randomUUID().toString()
                }
            } ?: return)
            temp
        }
    }
}