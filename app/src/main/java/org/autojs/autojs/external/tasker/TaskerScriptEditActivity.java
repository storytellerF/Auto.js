package org.autojs.autojs.external.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.autojs.autojs.R;
import org.autojs.autojs.databinding.ActivityTaskerScriptEditBinding;
import org.autojs.autojs.timing.TaskReceiver;
import org.autojs.autojs.tool.Observers;
import org.autojs.autojs.ui.BaseActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static org.autojs.autojs.ui.edit.EditorView.EXTRA_CONTENT;
import static org.autojs.autojs.ui.edit.EditorView.EXTRA_NAME;
import static org.autojs.autojs.ui.edit.EditorView.EXTRA_RUN_ENABLED;
import static org.autojs.autojs.ui.edit.EditorView.EXTRA_SAVE_ENABLED;

/**
 * Created by Stardust on 2017/4/5.
 */
public class TaskerScriptEditActivity extends BaseActivity {

    public static final int REQUEST_CODE = 10016;
    public static final String EXTRA_TASK_ID = TaskReceiver.EXTRA_TASK_ID;
    private ActivityTaskerScriptEditBinding inflate;

    public static void edit(Activity activity, String title, String summary, String content) {
        activity.startActivityForResult(new Intent(activity, TaskerScriptEditActivity.class)
                .putExtra(EXTRA_CONTENT, content)
                .putExtra("summary", summary)
                .putExtra(EXTRA_NAME, title), REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityTaskerScriptEditBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setUpViews();
    }

    void setUpViews() {
        Disposable subscribe = inflate.editorView.handleIntent(getIntent()
                .putExtra(EXTRA_RUN_ENABLED, false)
                .putExtra(EXTRA_SAVE_ENABLED, false))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.emptyConsumer(),
                        ex -> {
                            Toast.makeText(TaskerScriptEditActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        });
        compositeDisposable.add(subscribe);
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, inflate.editorView.getName());
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_CONTENT, inflate.editorView.getEditor().getText()));
        TaskerScriptEditActivity.super.finish();
    }

    @Override
    protected void onDestroy() {
        inflate.editorView.destroy();
        super.onDestroy();
    }
}
