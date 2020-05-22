package com.zkxt.mediausing

import com.arcns.core.media.selector.EMedia


interface IGlobalFileListener {
    fun onFileDel(item: EMedia)
}
