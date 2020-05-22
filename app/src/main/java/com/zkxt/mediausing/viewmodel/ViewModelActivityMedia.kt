package com.zkxt.mediausing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arcns.core.util.Event

class ViewModelActivityMedia : ViewModel() {

    // 正在加载或保存
    private var _loadIng = MutableLiveData<Boolean>()
    var loadIng: LiveData<Boolean> = _loadIng

    // 弹出提示
    private var _toast = MutableLiveData<Event<String>>()
    var toast: LiveData<Event<String>> = _toast
    fun showToast(value: String) {
        _toast.postValue(Event(value))
    }
}