package com.edit.universaledit

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener


class CommonEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : RelativeLayout(context, attrs, defStyleAttr), TextWatcher, View.OnFocusChangeListener,
    View.OnClickListener {

    companion object {
        @JvmStatic
        @InverseBindingAdapter(attribute = "text", event = "textAttrChanged")
        fun getText(commonEditText: CommonEditText): String =
            commonEditText.getText().toString().trim()

        @JvmStatic
        @BindingAdapter("text")
        fun setText(commonEditText: CommonEditText, value: String?) {
            val currentString = commonEditText.getText()
            if (!value.equals(currentString, true)) {
                commonEditText.setText(value)
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["textAttrChanged"], requireAll = false)
        fun setChangeListener(commonEditText: CommonEditText, listener: InverseBindingListener) {
            commonEditText.editTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    listener.onChange()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private val hintID = 0x01
    private val editID = 0x02
    private val clearID = 0x03
    private val errorID = 0x04
    private val passwordID = 0x05
    private val lineID = 0x06

    /**
     * 提示内容
     */
    private var hintText: String? = null

    /**
     * 提示字体颜色
     */
    private var hintColor = 0

    /**
     * 提示字体大小
     */
    private var hintSize = 14f

    /**
     * 左边图标
     */
    private var drawableId: Int? = null

    /**
     * 图标padding
     */
    private var drawablePadding = 5

    /**
     * 输入内容
     */
    private var text: String = ""

    /**
     * 输入框height
     */
    private var editHeight = 5

    /**
     * 输入框padding
     */
    private var editPadding = 2

    /**
     * 输入框paddingleft
     */
    private var editPaddingLeft = px2dip(10).toInt()
    private var editPaddingRight = px2dip(10).toInt()
    private var editPaddingTop = px2dip(10).toInt()
    private var editPaddingBottom = px2dip(10).toInt()

    /**
     * 输入框背景
     */
    private var editBackground: Drawable? = null

    /**
     * 输入字体大小
     */
    private var contentSize = 13f

    /**
     * 输入字体颜色
     */
    private var textColor = 0

    /**
     * 是否显示删除图标
     */
    private var clearImageViewVisibility = INVISIBLE

    /**
     * 输入类型
     */
    private var contentType = InputType.TYPE_TEXT_FLAG_MULTI_LINE

    private var mContext: Context = context

    /**
     * 提示text
     */
    private lateinit var hintView: TextView

    /**
     * 错误text
     */
    private lateinit var errorView: TextView

    /**
     * 错误内容
     */
    private var errorString = ""

    private var errorColor: Int? = null

    /**
     * 验证规则
     */
    private var regulation = ""
    private var showError = false

    /**
     * 输入框
     */
    private lateinit var editTextView: EditText

    /**
     * 底部横线
     */
    private lateinit var lineView: View

    /**
     * 删除图标
     */
    private var clearImage = 0
    private lateinit var mClearImageView: ImageView
    private lateinit var mClearImageParams: LayoutParams

    /**
     * 密码加密图标
     */
    private var encryptImageId = 0
    private lateinit var mEncryptImageView: ImageView
    private var encryptImageVisibility: Int? = null
    private lateinit var mEncryptParams: LayoutParams
    private var showPassword = true

    private var textWatcher: TextWatcher? = null
    private var focusChangeListener: OnFocusChangeListener? = null
    private var hintViewVisible = false
    private var showHintView = true

    /**
     * 单行输入
     */
    private var singleLine = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonEditText)
        hintText = typedArray.getString(R.styleable.CommonEditText_android_hint)
        hintColor = typedArray.getColor(R.styleable.CommonEditText_hintColor, Color.GRAY)
        hintSize = typedArray.getDimension(R.styleable.CommonEditText_hintSize, 14f)
        showHintView = typedArray.getBoolean(R.styleable.CommonEditText_showHintView, true)
        drawableId = typedArray.getResourceId(R.styleable.CommonEditText_android_drawableStart, -1)
        drawablePadding =
            typedArray.getDimensionPixelOffset(
                R.styleable.CommonEditText_android_drawablePadding,
                -1
            )
        editHeight = typedArray.getLayoutDimension(
            R.styleable.CommonEditText_editHeight,
            LayoutParams.WRAP_CONTENT
        )
        editPadding =
            typedArray.getDimensionPixelOffset(R.styleable.CommonEditText_editPadding, -1)
        editPaddingLeft =
            typedArray.getDimensionPixelOffset(
                R.styleable.CommonEditText_editPaddingLeft,
                px2dip(10).toInt()
            )
        editPaddingRight =
            typedArray.getDimensionPixelOffset(
                R.styleable.CommonEditText_editPaddingRight,
                px2dip(10).toInt()
            )
        editPaddingTop =
            typedArray.getDimensionPixelOffset(
                R.styleable.CommonEditText_editPaddingTop,
                px2dip(10).toInt()
            )
        editPaddingBottom =
            typedArray.getDimensionPixelOffset(
                R.styleable.CommonEditText_editPaddingBottom,
                px2dip(10).toInt()
            )
        editBackground =
            typedArray.getDrawable(R.styleable.CommonEditText_android_background)
        contentSize = typedArray.getDimension(R.styleable.CommonEditText_contentSize, 13f)

        textColor = typedArray.getColor(R.styleable.CommonEditText_android_textColor, Color.BLACK)
        text = typedArray.getString(R.styleable.CommonEditText_text).toString()
        clearImage = typedArray.getResourceId(
            R.styleable.CommonEditText_clearImageView,
            R.drawable.ic_delete_icon
        )
        contentType = typedArray.getInt(
            R.styleable.CommonEditText_android_inputType,
            InputType.TYPE_TEXT_FLAG_MULTI_LINE
        )
        encryptImageId = typedArray.getResourceId(
            R.styleable.CommonEditText_encryptImageView,
            R.drawable.eyes_open_close
        )
        encryptImageVisibility = typedArray.getInt(
            R.styleable.CommonEditText_encryptImageVisibility,
            GONE
        )
        singleLine = typedArray.getBoolean(R.styleable.CommonEditText_android_singleLine, false)
        typedArray.recycle()
        initView()
        editTextView.setText(text)
        initConfig()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {

        lineView = View(mContext)
        lineView.id = lineID

        hintView = TextView(mContext)
        hintView.id = hintID

        errorView = TextView(mContext)
        errorView.id = errorID

        editTextView = EditText(mContext)
        editTextView.id = editID

        mClearImageView = ImageView(mContext)
        mClearImageView.id = clearID
        mClearImageView.setImageDrawable(ContextCompat.getDrawable(mContext, clearImage))
        mClearImageView.setOnClickListener(this)

        mEncryptImageView = ImageView(mContext)
        mEncryptImageView.id = passwordID
        mEncryptImageView.setImageDrawable(ContextCompat.getDrawable(mContext, encryptImageId))
        mEncryptImageView.setOnClickListener(this)
        if (showHintView) {
            val hintViewParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            hintView.text = hintText
            addView(hintView, hintViewParams)
        }

        if (encryptImageVisibility == VISIBLE) {
            mEncryptParams = LayoutParams(px2dip(240).toInt(), px2dip(240).toInt()).apply {
                addRule(ALIGN_PARENT_RIGHT, TRUE)
                addRule(ALIGN_BOTTOM, editID)
                addRule(ALIGN_TOP, editID)
                rightMargin = px2dip(20).toInt()
                bottomMargin=px2dip(20).toInt()
            }
            addView(mEncryptImageView, mEncryptParams)

            mClearImageParams = LayoutParams(px2dip(200).toInt(), px2dip(200).toInt()).apply {
                addRule(LEFT_OF, mEncryptImageView.id)
                addRule(ALIGN_BOTTOM, editID)
                addRule(ALIGN_TOP, editID)
                rightMargin = px2dip(40).toInt()
            }
            addView(mClearImageView, mClearImageParams)
        } else {
            mClearImageParams = LayoutParams(px2dip(200).toInt(), px2dip(200).toInt()).apply {
                addRule(ALIGN_PARENT_RIGHT, TRUE)
                addRule(ALIGN_BOTTOM, editID)
                addRule(ALIGN_TOP, editID)
                rightMargin = px2dip(20).toInt()
                bottomMargin = editPaddingBottom
            }
            addView(mClearImageView, mClearImageParams)
        }

        val editTextParams = LayoutParams(LayoutParams.MATCH_PARENT, editHeight).apply {
            if (showHintView) {
                addRule(BELOW, hintView.id) //  hintView 的下方
            }
            addRule(LEFT_OF, mClearImageView.id)
        }
        addView(editTextView, editTextParams)

        val lineParams = LayoutParams(LayoutParams.MATCH_PARENT, 5).apply {
            addRule(BELOW, editTextView.id)
        }
        addView(lineView, lineParams)

        val errorViewParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(ABOVE, mClearImageView.id)
                addRule(ALIGN_PARENT_RIGHT, TRUE)
                rightMargin = 20
            }
        addView(errorView, errorViewParams)

    }

    @SuppressLint("UseCompatLoadingForDrawables", "NewApi")
    private fun initConfig() {
        errorView.visibility = GONE
        hintView.apply {
            visibility = INVISIBLE
            setTextColor(hintColor)
            textSize = hintSize
            text = hintText
        }

        if (drawableId != -1) {
            val drawable = ContextCompat.getDrawable(mContext, drawableId!!)
            drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            editTextView.setCompoundDrawables(drawable, null, null, null)
            editTextView.compoundDrawablePadding = drawablePadding
            //使上方的hintText与hint的左边起始点位置相同
            hintView.setPadding(drawable?.minimumWidth?.plus(drawablePadding)!!, 0, 0, 0)
        } else {
            hintView.setPadding(drawablePadding, 0, 0, 0)
        }

        if (editPadding == -1) {
            editTextView.setPadding(
                editPaddingLeft,
                editPaddingTop,
                10,
                editPaddingBottom
            )
            if (encryptImageVisibility == VISIBLE) {
                //设置密码图标的rightMargin
                mEncryptParams.rightMargin = editPaddingRight + px2dip(20).toInt()
            } else {
                //设置清除图标的rightMargin
                mClearImageParams.rightMargin = editPaddingRight + px2dip(20).toInt()
            }
        } else {
            editTextView.setPadding(
                editPadding,
                editPadding,
                10,
                editPadding
            )
            if (encryptImageVisibility == VISIBLE) {
                mEncryptParams.rightMargin = editPadding + px2dip(20).toInt()
            } else {
                mClearImageParams.rightMargin = editPadding + px2dip(20).toInt()
            }
        }

        editTextView.apply {
            setTextColor(textColor)
            textSize = contentSize
            hint = hintText
            inputType = contentType
            setBackgroundColor(Color.TRANSPARENT)
            setHintTextColor(hintColor)
            isSingleLine = singleLine
        }

        editTextView.addTextChangedListener(this)
        this.background = editBackground
        editTextView.onFocusChangeListener = this
        mClearImageView.visibility = clearImageViewVisibility

        if (editBackground != null) {
            lineView.visibility = GONE
        }
        lineView.setBackgroundColor(Color.GRAY)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //强制高度为WRAP_CONTENT，不然有输入的时候会出现显示不全的bug
        layoutParams.height = LayoutParams.WRAP_CONTENT
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 设置单行输入
     */
    fun setSingleLine(single: Boolean) {
        this.singleLine = single
        editTextView.isSingleLine = single
    }

    /**
     * 提示文字出现动画
     */
    private fun showHintView() {
        hintView.visibility = VISIBLE
        hintView.loadAnimation(mContext, R.anim.showhint)
    }

    /**
     * 提示文字隐藏动画
     */
    private fun hideHintView() {
        hintView.loadAnimation(mContext, R.anim.hidehint)
        hintView.visibility = INVISIBLE
    }

    /**
     * 输入监听
     */
    fun addTextChangedListener(textWatcher: TextWatcher) {
        this.textWatcher = textWatcher
    }

    /**
     * 设置输入框提示
     */
    fun setHintText(inputHint: String) {
        this.hintText = inputHint
        hintView.text = inputHint
        editTextView.hint = inputHint
    }

    /**
     * 设置有输入时，输入框上方提示文字颜色
     */
    fun setTopHintColor(hintColor: Int) {
        this.hintColor = hintColor
        hintView.setTextColor(hintColor)
    }

    /**
     * 设置有输入时，输入框上方提示文字大小 单位：sp
     */
    fun setTopHintSize(hintSize: Int) {
        this.hintSize = px2sp(hintSize)
        hintView.textSize = px2sp(hintSize)
    }

    /**
     * 设置输入框输入类型
     */
    fun setInputType(type: Int) {
        editTextView.inputType = type
    }

    /**
     * 设置输入框左边图标
     */
    fun setDrawable(id: Int) {
        drawableId = id
        val drawable = ContextCompat.getDrawable(mContext, id)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        editTextView.setCompoundDrawables(drawable, null, null, null)
    }

    fun setDrawable(id: Int, width: Int, height: Int) {
        drawableId = id
        val drawable = ContextCompat.getDrawable(mContext, id)
        drawable?.setBounds(0, 0, width, height)
        editTextView.setCompoundDrawables(drawable, null, null, null)
    }

    fun setDrawableWidthAndHeight(width: Int, height: Int) {
        val drawable = ContextCompat.getDrawable(mContext, drawableId!!)
        drawable?.setBounds(0, 0, width, height)
        editTextView.setCompoundDrawables(drawable, null, null, null)
    }

    /**
     * 设置输入框图标 Padding
     */
    fun setDrawablePadding(drawablePadding: Int) {
        this.drawablePadding = px2dip(drawablePadding).toInt()
        editTextView.compoundDrawablePadding = px2dip(drawablePadding).toInt()
    }

    /**
     * 设置输入框内容
     */

    fun setText(content: String?) {
        editTextView.setText(content)
    }

    /**
     * 获取输入框内容
     */

    fun getText(): String? {
        return editTextView.text.toString().trim()
    }

    /**
     * 设置输入框高度
     */
    fun setEditHeight(inputHeight: Int) {
        this.editHeight = px2dip(inputHeight).toInt()
    }

    /**
     * 获取输入框高度
     */
    fun getEditHeight(): Int = editHeight

    /**
     * 设置padding
     */
    fun setPadding(editPadding: Int) {
        this.editPadding = px2dip(editPadding).toInt()
    }

    /**
     * 设置paddingTop
     */
    fun setPaddingTop(editPaddingTop: Int) {
        this.editPaddingTop = px2dip(editPaddingTop).toInt()
    }

    /**
     * 设置paddingBottom
     */
    fun setPaddintBottom(editPaddingBottom: Int) {
        this.editPaddingBottom = px2dip(editPaddingBottom).toInt()
    }

    /**
     * 设置paddingLeft
     */
    fun setPaddingLeft(editPaddingLeft: Int) {
        this.editPaddingLeft = px2dip(editPaddingLeft).toInt()
    }

    /**
     * 设置 paddingRight
     */
    fun setPaddingRight(editPaddingRight: Int) {
        this.editPaddingRight = px2dip(editPaddingRight).toInt()
    }

    /**
     * 设置背景
     */
    fun setEditBackgroundResource(@ColorRes resId: Int) {
        this.setBackgroundResource(resId)
    }

    /**
     * 设置输入框背景
     */
    fun setEditBackgroundColor(@ColorInt color: Int) {
        this.setBackgroundColor(color)
    }

    /**
     * 设置输入框字体大小
     */
    fun setTextSize(textSize: Int) {
        this.contentSize = px2sp(textSize)
        editTextView.textSize = px2sp(textSize)
    }

    /**
     * 设置输入框字体颜色
     */
    fun setTextColor(@ColorInt textColor: Int) {
        this.textColor = textColor
        editTextView.setTextColor(textColor)
    }

    fun setTextColorResource(@ColorRes textColor: Int) {
        val color = ContextCompat.getColor(mContext, textColor)
        this.textColor = color
        editTextView.setTextColor(color)
    }

    /**
     * 设置删除图标显示隐藏 默认隐藏
     */
    fun setClearImageVisibility(clearImageVisibility: Int) {
        this.clearImageViewVisibility = clearImageVisibility
        mClearImageView.visibility = clearImageVisibility
    }

    /**
     * 设置输入有误信息
     */

    fun setErrorString(error: String, regulation: String) {
        this.errorString = error
        this.regulation = regulation
        showError = true
    }

    fun setErrorStringWithColor(
        error: String,
        regulation: String,
        @ColorInt color: Int = Color.RED
    ) {
        setErrorString(error, regulation)
        errorColor = color
        errorView.setTextColor(color)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setErrorStringWithResource(
        error: String,
        regulation: String,
        @ColorRes color: Int = R.color.red
    ) {
        setErrorString(error, regulation)
        errorColor = ContextCompat.getColor(mContext, color)
        errorView.setTextColor(ContextCompat.getColor(mContext, color))
    }

    /**
     * 设置错误提示显示/隐藏
     */
    fun setErrorVisibility(visibility: Int) {
        errorView.visibility = visibility
    }

    /**
     * 设置清除按钮大小
     */
    fun setClearImageSize(size: Int) {
        mClearImageView.layoutParams.apply {
            this.width = size
            this.height = size
        }
    }

    fun setClearImageSize(width: Int, height: Int) {
        mClearImageView.layoutParams.apply {
            this.width = width
            this.height = height
        }
    }

    /**
     * 设置显示密码按钮大小
     */
    fun setEncryImageSize(size: Int) {
        mEncryptImageView.layoutParams.apply {
            this.width = size
            this.height = size
        }
    }

    fun setEncryImageSize(width: Int, height: Int) {
        mEncryptImageView.layoutParams.apply {
            this.width = width
            this.height = height
        }
    }

    /**
     * 设置下划线颜色
     */
    fun setLineBackgroundColor(@ColorInt color: Int) {
        lineView.setBackgroundColor(color)
    }

    fun setLineBackgroundResource(@ColorRes color: Int) {
        lineView.setBackgroundResource(color)
    }

    /**
     * 设置下划线高度
     */
    fun setLineHeight(height: Int) {
        lineView.layoutParams.height = height
    }


    override fun setOnFocusChangeListener(listener: OnFocusChangeListener) {
        focusChangeListener = listener
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            clearID -> editTextView.setText("")
            passwordID -> {
                mEncryptImageView.isSelected = showPassword
                showPassword = !showPassword
                if (showPassword) {
                    editTextView.transformationMethod = PasswordTransformationMethod.getInstance()
                } else {
                    editTextView.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
                editTextView.setSelection(editTextView.text.length)
            }
            else -> {
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        textWatcher?.beforeTextChanged(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mClearImageView.visibility = if (s!!.isEmpty()) INVISIBLE else VISIBLE
        textWatcher?.onTextChanged(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable) {
        textWatcher?.afterTextChanged(s)
        if (s.isNotEmpty()) {
            //显示HintView
            if (!hintViewVisible && showHintView) {
                showHintView()
                hintViewVisible = true
            }
            //控制ErrorView的显示与隐藏
            if (!editTextView.verifyString(regulation) && showError) {
                errorView.visibility = VISIBLE
                errorView.text = errorString

            } else if (editTextView.verifyString(regulation) && showError) {
                errorView.visibility = GONE
            }
        } else {
            if (hintViewVisible) {
                hideHintView()
                hintViewVisible = false
            }
            errorView.visibility = GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        focusChangeListener?.onFocusChange(v, hasFocus)
        if (hasFocus) {
            //获得焦点
            editTextView.setSelection(editTextView.text.length)
            lineView.setBackgroundColor(getCurrentColorAccent(mContext))
        } else {
            lineView.setBackgroundColor(Color.DKGRAY)
        }
    }

}
