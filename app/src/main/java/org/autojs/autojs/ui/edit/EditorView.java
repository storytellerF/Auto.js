package org.autojs.autojs.ui.edit;

import static org.autojs.autojs.model.script.Scripts.ACTION_ON_EXECUTION_FINISHED;
import static org.autojs.autojs.model.script.Scripts.EXTRA_EXCEPTION_COLUMN_NUMBER;
import static org.autojs.autojs.model.script.Scripts.EXTRA_EXCEPTION_LINE_NUMBER;
import static org.autojs.autojs.model.script.Scripts.EXTRA_EXCEPTION_MESSAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.pio.PFiles;
import com.stardust.util.BackPressedHandler;
import com.stardust.util.Callback;
import com.stardust.util.ViewUtils;

import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.databinding.EditorViewBinding;
import org.autojs.autojs.model.autocomplete.AutoCompletion;
import org.autojs.autojs.model.autocomplete.CodeCompletion;
import org.autojs.autojs.model.autocomplete.CodeCompletions;
import org.autojs.autojs.model.autocomplete.Symbols;
import org.autojs.autojs.model.indices.Module;
import org.autojs.autojs.model.indices.Property;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.tool.Observers;
import org.autojs.autojs.ui.doc.ManualDialog;
import org.autojs.autojs.ui.edit.completion.CodeCompletionBar;
import org.autojs.autojs.ui.edit.debug.DebugBar;
import org.autojs.autojs.ui.edit.editor.CodeEditor;
import org.autojs.autojs.ui.edit.keyboard.FunctionsKeyboardHelper;
import org.autojs.autojs.ui.edit.keyboard.FunctionsKeyboardView;
import org.autojs.autojs.ui.edit.theme.Theme;
import org.autojs.autojs.ui.edit.theme.Themes;
import org.autojs.autojs.ui.edit.toolbar.DebugToolbarFragment;
import org.autojs.autojs.ui.edit.toolbar.NormalToolbarFragment;
import org.autojs.autojs.ui.edit.toolbar.SearchToolbarFragment;
import org.autojs.autojs.ui.edit.toolbar.ToolbarFragment;
import org.autojs.autojs.ui.log.LogActivity;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/9/28.
 */
public class EditorView extends FrameLayout implements CodeCompletionBar.OnHintClickListener, FunctionsKeyboardView.ClickCallback, ToolbarFragment.OnMenuItemClickListener {

    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_READ_ONLY = "readOnly";
    public static final String EXTRA_SAVE_ENABLED = "saveEnabled";
    public static final String EXTRA_RUN_ENABLED = "runEnabled";
    private final SparseBooleanArray mMenuItemStatus = new SparseBooleanArray();
    private final NormalToolbarFragment mNormalToolbar = new NormalToolbarFragment();
    @NonNull
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String mName;
    private Uri mUri;
    private boolean mReadOnly = false;
    private int mScriptExecutionId;
    private AutoCompletion mAutoCompletion;
    private Theme mEditorTheme;
    private FunctionsKeyboardHelper mFunctionsKeyboardHelper;
    @Nullable
    private String mRestoredText;
    private boolean mDebugging = false;
    private EditorViewBinding inflate;
    private final BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if (ACTION_ON_EXECUTION_FINISHED.equals(intent.getAction())) {
                mScriptExecutionId = ScriptExecution.NO_ID;
                if (mDebugging) {
                    exitDebugging();
                }
                setMenuItemStatus(R.id.run, true);
                String msg = intent.getStringExtra(EXTRA_EXCEPTION_MESSAGE);
                int line = intent.getIntExtra(EXTRA_EXCEPTION_LINE_NUMBER, -1);
                int col = intent.getIntExtra(EXTRA_EXCEPTION_COLUMN_NUMBER, 0);
                if (line >= 1) {
                    inflate.editor.jumpTo(line - 1, col);
                }
                if (msg != null) {
                    showErrorMessage(msg);
                }
            }
        }
    };

    public EditorView(Context context) {
        super(context);
        ad();
    }

    public EditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ad();
    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ad();
    }

    private void ad() {
        inflate = EditorViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(mOnRunFinishedReceiver, new IntentFilter(ACTION_ON_EXECUTION_FINISHED));
        if (getContext() instanceof BackPressedHandler.HostActivity) {
            ((BackPressedHandler.HostActivity) getContext()).getBackPressedObserver().registerHandler(mFunctionsKeyboardHelper);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mOnRunFinishedReceiver);
        if (getContext() instanceof BackPressedHandler.HostActivity) {
            ((BackPressedHandler.HostActivity) getContext()).getBackPressedObserver().unregisterHandler(mFunctionsKeyboardHelper);
        }
        compositeDisposable.dispose();
    }

    public Uri getUri() {
        return mUri;
    }

    public Observable<String> handleIntent(@NonNull Intent intent) {
        mName = intent.getStringExtra(EXTRA_NAME);
        return handleText(intent)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(str -> {
                    mReadOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, false);
                    boolean saveEnabled = intent.getBooleanExtra(EXTRA_SAVE_ENABLED, true);
                    if (mReadOnly || !saveEnabled) {
                        findViewById(R.id.save).setVisibility(View.GONE);
                    }
                    if (!intent.getBooleanExtra(EXTRA_RUN_ENABLED, true)) {
                        findViewById(R.id.run).setVisibility(GONE);
                    }
                    if (mReadOnly) {
                        inflate.editor.setReadOnly(true);
                    }
                });
    }

    public void setRestoredText(String text) {
        mRestoredText = text;
        inflate.editor.setText(text);
    }

    private Observable<String> handleText(@NonNull Intent intent) {
        String path = intent.getStringExtra(EXTRA_PATH);
        String content = intent.getStringExtra(EXTRA_CONTENT);
        if (content != null) {
            setInitialText(content);
            return Observable.just(content);
        } else {
            if (path == null) {
                if (intent.getData() == null) {
                    return Observable.error(new IllegalArgumentException("path and content is empty"));
                } else {
                    mUri = intent.getData();
                }
            } else {
                mUri = Uri.fromFile(new File(path));
            }
            if (mName == null) {
                mName = PFiles.getNameWithoutExtension(mUri.getPath());
            }
            return loadUri(mUri);
        }
    }

    private Observable<String> loadUri(final Uri uri) {
        inflate.editor.setProgress(true);
        return Observable.fromCallable(() -> PFiles.read(getContext().getContentResolver().openInputStream(uri)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    setInitialText(s);
                    inflate.editor.setProgress(false);
                });
    }

    private void setInitialText(String text) {
        if (mRestoredText != null) {
            inflate.editor.setText(mRestoredText);
            mRestoredText = null;
            return;
        }
        inflate.editor.setInitialText(text);
    }

    private void setMenuItemStatus(int id, boolean enabled) {
        mMenuItemStatus.put(id, enabled);
        ToolbarFragment fragment = (ToolbarFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.toolbar_menu);
        if (fragment == null) {
            mNormalToolbar.setMenuItemStatus(id, enabled);
        } else {
            fragment.setMenuItemStatus(id, enabled);
        }
    }

    public boolean getMenuItemStatus(int id, boolean defValue) {
        return mMenuItemStatus.get(id, defValue);
    }

    void init() {
        //setTheme(Theme.getDefault(getContext()));
        setUpEditor();
        setUpInputMethodEnhancedBar();
        setUpFunctionsKeyboard();
        setMenuItemStatus(R.id.save, false);
        inflate.docs.getWebView().getSettings().setDisplayZoomControls(true);
        inflate.docs.getWebView().loadUrl(Pref.getDocumentationUrl() + "index.html");
        Disposable subscribe = Themes.getCurrent(getContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setTheme);
        compositeDisposable.add(subscribe);
        initNormalToolbar();
    }

    private void initNormalToolbar() {
        mNormalToolbar.setOnMenuItemClickListener(this);
        mNormalToolbar.setOnMenuItemLongClickListener(id -> {
            if (id == R.id.run) {
                debug();
                return true;
            }
            return false;
        });
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.toolbar_menu);
        if (fragment == null) {
            showNormalToolbar();
        }
    }

    private void setUpFunctionsKeyboard() {
        mFunctionsKeyboardHelper = FunctionsKeyboardHelper.with((Activity) getContext())
                .setContent(inflate.editor)
                .setFunctionsTrigger(inflate.functions)
                .setFunctionsView(inflate.functionsKeyboard)
                .setEditView(inflate.editor.getCodeEditText())
                .build();
        inflate.functionsKeyboard.setClickCallback(this);
    }

    private void setUpInputMethodEnhancedBar() {
        inflate.symbolBar.setCodeCompletions(Symbols.getSymbols());
        inflate.codeCompletionBar.setOnHintClickListener(this);
        inflate.symbolBar.setOnHintClickListener(this);
        mAutoCompletion = new AutoCompletion(getContext(), inflate.editor.getCodeEditText());
        mAutoCompletion.setAutoCompleteCallback(inflate.codeCompletionBar::setCodeCompletions);
    }

    private void setUpEditor() {
        inflate.editor.getCodeEditText().addTextChangedListener(new SimpleTextWatcher(s -> {
            setMenuItemStatus(R.id.save, inflate.editor.isTextChanged());
            setMenuItemStatus(R.id.undo, inflate.editor.canUndo());
            setMenuItemStatus(R.id.redo, inflate.editor.canRedo());
        }));
        inflate.editor.addCursorChangeCallback(this::autoComplete);
        inflate.editor.getCodeEditText().setTextSize(Pref.getEditorTextSize((int) ViewUtils.pxToSp(getContext(), inflate.editor.getCodeEditText().getTextSize())));
    }

    private void autoComplete(String line, int cursor) {
        mAutoCompletion.onCursorChange(line, cursor);
    }

    @NonNull
    public DebugBar getDebugBar() {
        return inflate.debugBar;
    }

    public void setTheme(@NonNull Theme theme) {
        mEditorTheme = theme;
        inflate.editor.setTheme(theme);
        inflate.inputMethodEnhanceBar.setBackgroundColor(theme.getImeBarBackgroundColor());
        int textColor = theme.getImeBarForegroundColor();
        inflate.codeCompletionBar.setTextColor(textColor);
        inflate.symbolBar.setTextColor(textColor);
        inflate.functions.setColorFilter(textColor);
        invalidate();
    }

    public boolean onBackPressed() {
        if (inflate.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (inflate.docs.getWebView().canGoBack()) {
                inflate.docs.getWebView().goBack();
            } else {
                inflate.drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onToolbarMenuItemClick(int id) {
        if (id == R.id.run) {
            runAndSaveFileIfNeeded();
        } else if (id == R.id.save) {
            saveFile();
        } else if (id == R.id.undo) {
            undo();
        } else if (id == R.id.redo) {
            redo();
        } else if (id == R.id.replace) {
            replace();
        } else if (id == R.id.find_next) {
            findNext();
        } else if (id == R.id.find_prev) {
            findPrev();
        } else if (id == R.id.cancel_search) {
            cancelSearch();
        }
    }

    @SuppressLint("CheckResult")
    public void runAndSaveFileIfNeeded() {
        Disposable subscribe = save().observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> run(true), Observers.toastMessage());
        compositeDisposable.add(subscribe);
    }

    @NonNull
    public ScriptExecution run(boolean showMessage) {
        if (showMessage) {
            Snackbar.make(this, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        }
        // TODO: 2018/10/24
        ScriptExecution execution = Scripts.INSTANCE.runWithBroadcastSender(new File(mUri.getPath()));
        mScriptExecutionId = execution.getId();
        setMenuItemStatus(R.id.run, false);
        return execution;
    }


    public void undo() {
        inflate.editor.undo();
    }

    public void redo() {
        inflate.editor.redo();
    }

    public Observable<String> save() {
        String path = mUri.getPath();
        PFiles.move(path, path + ".bak");
        return Observable.just(inflate.editor.getText())
                .observeOn(Schedulers.io())
                .doOnNext(s -> PFiles.write(getContext().getContentResolver().openOutputStream(mUri), s))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    inflate.editor.markTextAsSaved();
                    setMenuItemStatus(R.id.save, false);
                });
    }

    public void forceStop() {
        doWithCurrentEngine(ScriptEngine::forceStop);
    }

    private void doWithCurrentEngine(@NonNull Callback<ScriptEngine> callback) {
        ScriptExecution execution = AutoJs.getInstance().getScriptEngineService().getScriptExecution(mScriptExecutionId);
        if (execution != null) {
            ScriptEngine engine = execution.getEngine();
            if (engine != null) {
                callback.call(engine);
            }
        }
    }

    @SuppressLint("CheckResult")
    public void saveFile() {
        Disposable subscribe = save()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.emptyConsumer(), e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscribe);
    }

    void findNext() {
        inflate.editor.findNext();
    }

    void findPrev() {
        inflate.editor.findPrev();
    }

    void cancelSearch() {
        showNormalToolbar();
    }

    private void showNormalToolbar() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.toolbar_menu, mNormalToolbar)
                .commitAllowingStateLoss();
    }

    @NonNull
    FragmentActivity getActivity() {
        Context context = getContext();
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (FragmentActivity) context;
    }

    void replace() {
        inflate.editor.replaceSelection();
    }

    public String getName() {
        return mName;
    }

    public boolean isTextChanged() {
        return inflate.editor.isTextChanged();
    }

    public void showConsole() {
        doWithCurrentEngine(engine -> ((JavaScriptEngine) engine).getRuntime().console.show());
    }

    public void openByOtherApps() {
        if (mUri != null) {
            Scripts.INSTANCE.openByOtherApps(mUri);
        }
    }

    public void beautifyCode() {
        inflate.editor.beautifyCode();
    }

    public void selectEditorTheme() {
        inflate.editor.setProgress(true);
        Disposable subscribe = Themes.getAllThemes(getContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(themes -> {
                    inflate.editor.setProgress(false);
                    selectEditorTheme(themes);
                });
        compositeDisposable.add(subscribe);
    }

    public void selectTextSize() {
        new TextSizeSettingDialogBuilder(getContext())
                .initialValue((int) ViewUtils.pxToSp(getContext(), inflate.editor.getCodeEditText().getTextSize()))
                .callback(this::setTextSize)
                .show();
    }

    public void setTextSize(int value) {
        Pref.setEditorTextSize(value);
        inflate.editor.getCodeEditText().setTextSize(value);
    }

    private void selectEditorTheme(@NonNull List<Theme> themes) {
        int i = themes.indexOf(mEditorTheme);
        if (i < 0) {
            i = 0;
        }
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_editor_theme)
                .items(themes)
                .itemsCallbackSingleChoice(i, (dialog, itemView, which, text) -> {
                    setTheme(themes.get(which));
                    Themes.setCurrent(themes.get(which).getName());
                    return true;
                })
                .show();
    }

    @NonNull
    public CodeEditor getEditor() {
        return inflate.editor;
    }

    public void find(@NonNull String keywords, boolean usingRegex) throws CodeEditor.CheckedPatternSyntaxException {
        inflate.editor.find(keywords, usingRegex);
        showSearchToolbar(false);
    }

    private void showSearchToolbar(boolean showReplaceItem) {
        SearchToolbarFragment searchToolbarFragment = SearchToolbarFragment.builder()
                .arg(SearchToolbarFragment.ARGUMENT_SHOW_REPLACE_ITEM, showReplaceItem)
                .build();
        searchToolbarFragment.setOnMenuItemClickListener(this);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.toolbar_menu, searchToolbarFragment)
                .commit();
    }

    public void replace(@NonNull String keywords, String replacement, boolean usingRegex) throws CodeEditor.CheckedPatternSyntaxException {
        inflate.editor.replace(keywords, replacement, usingRegex);
        showSearchToolbar(true);
    }

    public void replaceAll(String keywords, @NonNull String replacement, boolean usingRegex) throws CodeEditor.CheckedPatternSyntaxException {
        inflate.editor.replaceAll(keywords, replacement, usingRegex);
    }


    public void debug() {
        DebugToolbarFragment debugToolbarFragment = DebugToolbarFragment.builder()
                .build();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.toolbar_menu, debugToolbarFragment)
                .commit();
        inflate.debugBar.setVisibility(VISIBLE);
        inflate.inputMethodEnhanceBar.setVisibility(GONE);
        mDebugging = true;
    }

    public void exitDebugging() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.toolbar_menu);
        if (fragment instanceof DebugToolbarFragment) {
            ((DebugToolbarFragment) fragment).detachDebugger();
        }
        showNormalToolbar();
        inflate.editor.setDebuggingLine(-1);
        inflate.debugBar.setVisibility(GONE);
        inflate.inputMethodEnhanceBar.setVisibility(VISIBLE);
        mDebugging = false;
    }

    private void showErrorMessage(String msg) {
        Snackbar.make(EditorView.this, getResources().getString(R.string.text_error) + ": " + msg, Snackbar.LENGTH_LONG)
                .setAction(R.string.text_detail, v -> LogActivity.intent(getContext()).start())
                .show();
    }

    @Override
    public void onHintClick(@NonNull CodeCompletions completions, int pos) {
        CodeCompletion completion = completions.get(pos);
        inflate.editor.insert(completion.getInsertText());
    }

    @Override
    public void onHintLongClick(@NonNull CodeCompletions completions, int pos) {
        CodeCompletion completion = completions.get(pos);
        if (completion.getUrl() == null)
            return;
        showManual(completion.getUrl(), completion.getHint());
    }

    private void showManual(String url, String title) {
        String absUrl = Pref.getDocumentationUrl() + url;
        new ManualDialog(getContext())
                .title(title)
                .url(absUrl)
                .pinToLeft(v -> {
                    inflate.docs.getWebView().loadUrl(absUrl);
                    inflate.drawerLayout.openDrawer(GravityCompat.START);
                })
                .show();
    }

    @Override
    public void onModuleLongClick(@NonNull Module module) {
        showManual(module.getUrl(), module.getName());
    }

    @Override
    public void onPropertyClick(@NonNull Module m, @NonNull Property property) {
        String p = property.getKey();
        if (!property.isVariable()) {
            p = p + "()";
        }
        if (property.isGlobal()) {
            inflate.editor.insert(p);
        } else {
            inflate.editor.insert(m.getName() + "." + p);
        }
        if (!property.isVariable()) {
            inflate.editor.moveCursor(-1);
        }
        mFunctionsKeyboardHelper.hideFunctionsLayout(true);
    }

    @Override
    public void onPropertyLongClick(@NonNull Module m, @NonNull Property property) {
        if (TextUtils.isEmpty(property.getUrl())) {
            showManual(m.getUrl(), property.getKey());
        } else {
            showManual(property.getUrl(), property.getKey());
        }
    }

    public int getScriptExecutionId() {
        return mScriptExecutionId;
    }

    @Nullable
    public ScriptExecution getScriptExecution() {
        return AutoJs.getInstance().getScriptEngineService().getScriptExecution(mScriptExecutionId);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable superData = super.onSaveInstanceState();
        bundle.putParcelable("super_data", superData);
        bundle.putInt("script_execution_id", mScriptExecutionId);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable("super_data");
        mScriptExecutionId = bundle.getInt("script_execution_id", ScriptExecution.NO_ID);
        super.onRestoreInstanceState(superData);
        setMenuItemStatus(R.id.run, mScriptExecutionId == ScriptExecution.NO_ID);
    }

    public void destroy() {
        inflate.editor.destroy();
        mAutoCompletion.shutdown();
    }
}
