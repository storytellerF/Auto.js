package org.autojs.autojs.ui.edit.toolbar;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.autojs.autojs.ui.edit.EditorView;

import java.util.List;

public abstract class ToolbarFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {


    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnMenuItemLongClickListener mOnMenuItemLongClickListener;
    private List<Integer> mMenuItemIds;

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        mOnMenuItemClickListener = listener;
    }

    public void setOnMenuItemLongClickListener(OnMenuItemLongClickListener onMenuItemLongClickListener) {
        mOnMenuItemLongClickListener = onMenuItemLongClickListener;
    }

    public abstract List<Integer> getMenuItemIds();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateMenuItemStatus(view);
    }

    @NonNull
    protected EditorView findEditorView(View view) {
        while (!(view instanceof EditorView) && view.getParent() != null) {
            view = (View) view.getParent();
        }
        if (!(view instanceof EditorView)) {
            throw new IllegalStateException("cannot find EditorView from child: " + view);
        }
        return (EditorView) view;
    }

    private void updateMenuItemStatus(@Nullable View rootView) {
        if (rootView == null) {
            return;
        }
        EditorView editorView = findEditorView(rootView);
        if (mMenuItemIds == null) {
            mMenuItemIds = getMenuItemIds();
        }
        for (int id : mMenuItemIds) {
            View view = rootView.findViewById(id);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setEnabled(editorView.getMenuItemStatus(id, view.isEnabled()));
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        if (mOnMenuItemClickListener != null) {
            mOnMenuItemClickListener.onToolbarMenuItemClick(view.getId());
        }
    }

    @Override
    public boolean onLongClick(@NonNull View v) {
        return mOnMenuItemLongClickListener != null &&
                mOnMenuItemLongClickListener.onToolbarMenuItemLongClick(v.getId());
    }

    public void setMenuItemStatus(int id, boolean enabled) {
        if (mMenuItemIds == null) {
            mMenuItemIds = getMenuItemIds();
        }
        if (!mMenuItemIds.contains(id)) {
            return;
        }
        View rootView = getView();
        if (rootView == null) {
            return;
        }
        View view = rootView.findViewById(id);
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
    }


    public interface OnMenuItemClickListener {
        void onToolbarMenuItemClick(int id);
    }

    public interface OnMenuItemLongClickListener {
        boolean onToolbarMenuItemLongClick(int id);
    }

}
