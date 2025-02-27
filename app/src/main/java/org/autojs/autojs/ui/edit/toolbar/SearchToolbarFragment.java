package org.autojs.autojs.ui.edit.toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.androidannotations.api.builder.FragmentBuilder;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.FragmentSearchToolbarBinding;

import java.util.Arrays;
import java.util.List;

public class SearchToolbarFragment extends ToolbarFragment {

    public static final String ARGUMENT_SHOW_REPLACE_ITEM = "show_replace_item";

    @NonNull
    public static <I extends FragmentBuilder<I, SearchToolbarFragment>> FragmentBuilder<I, SearchToolbarFragment> builder() {
        return new FragmentBuilder<I, SearchToolbarFragment>() {

            @NonNull
            @Override
            public SearchToolbarFragment build() {
                SearchToolbarFragment searchToolbarFragment = new SearchToolbarFragment();
                searchToolbarFragment.setArguments(args);
                return searchToolbarFragment;
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        org.autojs.autojs.databinding.FragmentSearchToolbarBinding inflate = FragmentSearchToolbarBinding.inflate(getLayoutInflater());
        return inflate.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean showReplaceItem = getArguments().getBoolean(ARGUMENT_SHOW_REPLACE_ITEM, false);
        view.findViewById(R.id.replace).setVisibility(showReplaceItem ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    public List<Integer> getMenuItemIds() {
        return Arrays.asList(R.id.replace, R.id.find_next, R.id.find_prev, R.id.cancel_search);
    }

}
