package org.autojs.autojs.ui.edit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.autojs.autojs.R;
import org.autojs.autojs.model.indices.AndroidClassIndices;
import org.autojs.autojs.model.indices.ClassSearchingItem;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.ui.widget.AutoAdapter;
import org.autojs.autojs.ui.widget.BindableViewHolder;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import io.reactivex.android.schedulers.AndroidSchedulers;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ClassSearchDialogBuilder extends ThemeColorMaterialDialogBuilder {

    @NonNull
    private final Handler mHandler;
    private AutoAdapter<ClassSearchingItem> mResultListAdapter;
    private MaterialProgressBar mProgressBar;
    private EditText mKeywords;
    private OnItemClickListener mOnItemClickListener;
    private int mSearchId = 0;
    private MaterialDialog mDialog;
    public ClassSearchDialogBuilder(@NonNull Context context) {
        super(new ContextThemeWrapper(context, R.style.AppTheme));
        mHandler = new Handler();
        initViews(getContext());
        AndroidClassIndices.getInstance(getContext());
    }

    @NonNull
    public ClassSearchDialogBuilder setQuery(String text) {
        mKeywords.setText(text);
        return this;
    }

    @NonNull
    public ClassSearchDialogBuilder itemClick(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    private void initViews(Context context) {
        View view = View.inflate(context, R.layout.dialog_class_search, null);
        customView(view, true);
        mKeywords = view.findViewById(R.id.keywords);
        mKeywords.addTextChangedListener(new SimpleTextWatcher(this::postSearch));
        mProgressBar = view.findViewById(R.id.progress_bar);
        initResultList(view, context);
    }

    private void initResultList(@NonNull View view, Context context) {
        RecyclerView resultList = view.findViewById(R.id.result_list);
        resultList.setLayoutManager(new LinearLayoutManager(context));
        mResultListAdapter = new AutoAdapter<>(ClassSearchingItemViewHolder::new,
                R.layout.item_class_searching_result_list);
        resultList.setAdapter(mResultListAdapter);
    }

    @Override
    public MaterialDialog build() {
        mDialog = super.build();
        return mDialog;
    }

    private void postSearch(@NonNull CharSequence s) {
        mSearchId++;
        int searchId = mSearchId;
        mHandler.postDelayed(() -> {
            if (searchId == mSearchId) {
                search(s);
            }
        }, 300);
    }

    @SuppressLint("CheckResult")
    private void search(@NonNull CharSequence s) {
        mResultListAdapter.removeAll();
        mProgressBar.setVisibility(View.VISIBLE);
        String keywords = s.toString();
        AndroidClassIndices.getInstance(getContext())
                .search(keywords)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mResultListAdapter.notifyDataSetChanged(result);
                    mProgressBar.setVisibility(View.GONE);
                }, t -> {
                    t.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public interface OnItemClickListener {
        void onItemClick(MaterialDialog dialog, ClassSearchingItem item, int position);
    }

    private class ClassSearchingItemViewHolder extends BindableViewHolder<ClassSearchingItem> {

        private final TextView mLabel;

        ClassSearchingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mLabel = itemView.findViewById(R.id.label);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mDialog, mResultListAdapter.get(pos), pos);
                }
            });
        }

        @Override
        public void bind(@NonNull ClassSearchingItem data, int position) {
            mLabel.setText(data.getLabel());
        }
    }

}
