package org.autojs.autojs.ui.edit.toolbar;

import androidx.annotation.NonNull;

import org.androidannotations.annotations.EFragment;
import org.autojs.autojs.R;

import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_normal_toolbar)
public class NormalToolbarFragment extends ToolbarFragment {

    @NonNull
    @Override
    public List<Integer> getMenuItemIds() {
        return Arrays.asList(R.id.run, R.id.undo, R.id.redo, R.id.save);
    }
}
