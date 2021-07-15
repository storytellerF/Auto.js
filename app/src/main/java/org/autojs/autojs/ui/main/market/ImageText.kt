package org.autojs.autojs.ui.main.market

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.autojs.autojs.R
import org.autojs.autojs.databinding.ImageTextBinding

class ImageText : LinearLayout {
    lateinit var inflate: ImageTextBinding
    var text: CharSequence?
        get() = inflate.textView.text
        set(value) {
            inflate.textView.text = value
        }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        inflate = ImageTextBinding.inflate(LayoutInflater.from(context), this)
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.ImageText)
        a.getString(R.styleable.ImageText_text)?.let {
            inflate.textView.text = it
        }
        val iconResId = a.getResourceId(R.styleable.ImageText_src, 0)
        if (iconResId != 0) {
            inflate.imageView.setImageResource(iconResId)
        }
        val imageWidth = a.getDimensionPixelSize(R.styleable.ImageText_image_width, 0)
        if (imageWidth != 0) {
            inflate.imageView.layoutParams.width = imageWidth
        }
        a.recycle()
    }

    fun setColor(color: Int) {
        inflate.textView.setTextColor(color)
        inflate.imageView.setColorFilter(color)
    }

}