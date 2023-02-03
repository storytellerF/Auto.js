package com.stardust.autojs.core.ui.dialog

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.os.Build
import android.os.Looper
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.stardust.autojs.runtime.ScriptBridges
import com.stardust.autojs.runtime.ScriptRuntime
import com.stardust.autojs.runtime.exception.ScriptInterruptedException
import com.stardust.concurrent.VolatileDispose
import com.stardust.util.ArrayUtils
import com.stardust.util.UiHandler

/**
 * Created by Stardust on 2017/5/8.
 */
class BlockedMaterialDialog protected constructor(builder: MaterialDialog.Builder) : MaterialDialog(builder) {
    override fun show() {
        if (!isActivityContext(context)) {
            val type: Int
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            window!!.setType(type)
        }
        super.show()
    }

    private fun isActivityContext(context: Context?): Boolean {
        if (context == null) return false
        if (context is Activity) {
            return !context.isFinishing
        }
        return if (context is ContextWrapper) {
            isActivityContext(context.baseContext)
        } else false
    }

    class Builder(context: Context, runtime: ScriptRuntime, callback: Any?) : MaterialDialog.Builder(context) {
        private val mUiHandler: UiHandler
        private val mCallback: Any?
        private val mScriptBridges: ScriptBridges
        private var mResultBox: VolatileDispose<Any?>? = null
        private var mNotified = false

        init {
            super.theme(Theme.LIGHT)
            mUiHandler = runtime.uiHandler
            mScriptBridges = runtime.bridges
            mCallback = callback
            if (Looper.getMainLooper() != Looper.myLooper()) {
                mResultBox = VolatileDispose()
            }
        }

        fun input(hint: CharSequence?, prefill: CharSequence?, allowEmptyInput: Boolean): MaterialDialog.Builder {
            super.input(hint, prefill, allowEmptyInput) { dialog: MaterialDialog?, input: CharSequence -> setAndNotify(input.toString()) }
            cancelListener { dialog: DialogInterface? -> setAndNotify(null) }
            return this
        }

        private fun setAndNotify(r: Any?) {
            if (mNotified) {
                return
            }
            mNotified = true
            if (mCallback != null) {
                mScriptBridges.callFunction(mCallback, null, arrayOf(r))
            }
            mResultBox?.setAndNotify(r)
        }

        private fun setAndNotify(r: Int) {
            if (mNotified) {
                return
            }
            mNotified = true
            if (mCallback != null) {
                mScriptBridges.callFunction(mCallback, null, intArrayOf(r))
            }
            mResultBox?.setAndNotify(r)
        }

        private fun setAndNotify(r: Boolean) {
            if (mNotified) {
                return
            }
            mNotified = true
            if (mCallback != null) {
                mScriptBridges.callFunction(mCallback, null, booleanArrayOf(r))
            }
            mResultBox?.setAndNotify(r)
        }

        fun alert(): Builder {
            dismissListener { dialog: DialogInterface? -> setAndNotify(null) }
            onAny { dialog: MaterialDialog?, which: DialogAction? -> setAndNotify(null) }
            return this
        }

        fun confirm(): Builder {
            dismissListener { dialog: DialogInterface? -> setAndNotify(false) }
            onAny { dialog: MaterialDialog?, which: DialogAction -> setAndNotify(which == DialogAction.POSITIVE) }
            return this
        }

        fun itemsCallback(): MaterialDialog.Builder {
            dismissListener { dialog: DialogInterface? -> setAndNotify(-1) }
            super.itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence? -> setAndNotify(position) }
            return this
        }

        fun itemsCallbackMultiChoice(selectedIndices: Array<Int?>?): MaterialDialog.Builder {
            dismissListener { dialog: DialogInterface? -> setAndNotify(IntArray(0)) }
            super.itemsCallbackMultiChoice(selectedIndices) { dialog: MaterialDialog?, which: Array<Int?>?, text: Array<CharSequence?>? ->
                setAndNotify(
                    ArrayUtils.unbox(
                        which!!
                    )
                )
                true
            }
            return this
        }

        fun itemsCallbackSingleChoice(selectedIndex: Int): MaterialDialog.Builder {
            dismissListener { dialog: DialogInterface? -> setAndNotify(-1) }
            super.itemsCallbackSingleChoice(selectedIndex) { dialog: MaterialDialog?, itemView: View?, which: Int, text: CharSequence? ->
                setAndNotify(which)
                true
            }
            return this
        }

        fun showAndGet(): Any? {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.show()
            } else {
                mUiHandler.post { super@Builder.show() }
            }
            return mResultBox?.blockedGetOrThrow(ScriptInterruptedException::class.java)
        }

        override fun build(): MaterialDialog {
            return BlockedMaterialDialog(this)
        }
    }
}