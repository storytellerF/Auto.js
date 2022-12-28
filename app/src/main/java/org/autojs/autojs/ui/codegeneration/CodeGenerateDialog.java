package org.autojs.autojs.ui.codegeneration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.stardust.app.DialogUtils;
import com.stardust.autojs.codegeneration.CodeGenerator;
import com.stardust.theme.util.ListBuilder;
import com.stardust.util.ClipboardUtil;
import com.stardust.view.accessibility.NodeInfo;

import org.autojs.autojs.R;
import org.autojs.autojs.databinding.DialogCodeGenerateBinding;
import org.autojs.autojs.databinding.DialogCodeGenerateOptionBinding;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/11/6.
 */

public class CodeGenerateDialog extends ThemeColorMaterialDialogBuilder {

    private final List<OptionGroup> mOptionGroups = new ListBuilder<OptionGroup>()
            .add(new OptionGroup(R.string.text_options, false)
                    .addOption(R.string.text_using_id_selector, true)
                    .addOption(R.string.text_using_text_selector, true)
                    .addOption(R.string.text_using_desc_selector, true))
            .add(new OptionGroup(R.string.text_select)
                    .addOption(R.string.text_find_one, true)
                    .addOption(R.string.text_until_find)
                    .addOption(R.string.text_wait_for)
                    .addOption(R.string.text_selector_exists))
            .add(new OptionGroup(R.string.text_action)
                    .addOption(R.string.text_click)
                    .addOption(R.string.text_long_click)
                    .addOption(R.string.text_set_text)
                    .addOption(R.string.text_scroll_forward)
                    .addOption(R.string.text_scroll_backward))
            .list();

    private final NodeInfo mRootNode;
    private final NodeInfo mTargetNode;
    private Adapter mAdapter;
    private DialogCodeGenerateBinding bind;

    public CodeGenerateDialog(@NonNull Context context, NodeInfo rootNode, NodeInfo targetNode) {
        super(context);
        mRootNode = rootNode;
        mTargetNode = targetNode;
        positiveText(R.string.text_generate);
        negativeText(R.string.text_cancel);
        onPositive(((dialog, which) -> generateCodeAndShow()));
        setupViews();
    }

    private void generateCodeAndShow() {
        String code = generateCode();
        if (code == null) {
            Toast.makeText(getContext(), R.string.text_generate_fail, Toast.LENGTH_SHORT).show();
            return;
        }
        DialogUtils.showDialog(new ThemeColorMaterialDialogBuilder(getContext())
                .title(R.string.text_generated_code)
                .content(code)
                .positiveText(R.string.text_copy)
                .onPositive(((dialog, which) -> ClipboardUtil.setClip(getContext(), code)))
                .build());
    }


    private String generateCode() {
        CodeGenerator generator = new CodeGenerator(mRootNode, mTargetNode);
        OptionGroup settings = getOptionGroup(R.string.text_options);
        generator.setUsingId(settings.getOption(R.string.text_using_id_selector).checked);
        generator.setUsingText(settings.getOption(R.string.text_using_text_selector).checked);
        generator.setUsingDesc(settings.getOption(R.string.text_using_desc_selector).checked);
        generator.setSearchMode(getSearchMode());
        setAction(generator);
        return generator.generateCode();
    }

    private void setAction(@NonNull CodeGenerator generator) {
        OptionGroup action = getOptionGroup(R.string.text_action);
        if (action.getOption(R.string.text_click).checked) {
            generator.setAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
        }
        if (action.getOption(R.string.text_long_click).checked) {
            generator.setAction(AccessibilityNodeInfoCompat.ACTION_LONG_CLICK);
        }
        if (action.getOption(R.string.text_scroll_forward).checked) {
            generator.setAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD);
        }
        if (action.getOption(R.string.text_scroll_backward).checked) {
            generator.setAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD);
        }
    }

    private int getSearchMode() {
        OptionGroup selectMode = getOptionGroup(R.string.text_select);
        if (selectMode.getOption(R.string.text_find_one).checked) {
            return CodeGenerator.FIND_ONE;
        }
        if (selectMode.getOption(R.string.text_until_find).checked) {
            return CodeGenerator.UNTIL_FIND;
        }
        if (selectMode.getOption(R.string.text_wait_for).checked) {
            return CodeGenerator.WAIT_FOR;
        }
        if (selectMode.getOption(R.string.text_selector_exists).checked) {
            return CodeGenerator.EXISTS;
        }
        return CodeGenerator.FIND_ONE;
    }

    private void setupViews() {
        View view = View.inflate(context, R.layout.dialog_code_generate, null);
        bind = DialogCodeGenerateBinding.bind(view);
        customView(view, false);
        bind.options.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter(mOptionGroups);
        bind.options.setAdapter(mAdapter);
    }


    @NonNull
    private OptionGroup getOptionGroup(int title) {
        for (OptionGroup group : mOptionGroups) {
            if (group.titleRes == title) {
                return group;
            }
        }
        throw new IllegalArgumentException();
    }


    private void uncheckOthers(int parentAdapterPosition, @NonNull Option child) {
        boolean notify = false;
        for (Option other : child.group.options) {
            if (other != child) {
                if (other.checked) {
                    other.checked = false;
                    notify = true;
                }
            }
        }
        if (notify)
            mAdapter.notifyParentChanged(parentAdapterPosition);
    }

    private static class Option {
        int titleRes;
        boolean checked;
        OptionGroup group;

        Option(int titleRes, boolean checked) {
            this.titleRes = titleRes;
            this.checked = checked;
        }

    }

    private static class OptionGroup implements Parent<Option> {
        private final boolean mInitialExpanded;
        int titleRes;
        @NonNull
        List<Option> options = new ArrayList<>();


        OptionGroup(int titleRes, boolean initialExpanded) {
            this.titleRes = titleRes;
            mInitialExpanded = initialExpanded;
        }

        OptionGroup(int titleRes) {
            this(titleRes, true);
        }

        @NonNull
        Option getOption(int titleRes) {
            for (Option option : options) {
                if (option.titleRes == titleRes) {
                    return option;
                }
            }
            throw new IllegalArgumentException();
        }

        @NonNull
        @Override
        public List<Option> getChildList() {
            return options;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return mInitialExpanded;
        }

        @NonNull
        OptionGroup addOption(int titleRes) {
            return addOption(titleRes, false);
        }

        @NonNull
        OptionGroup addOption(int res, boolean checked) {
            Option option = new Option(res, checked);
            option.group = this;
            options.add(option);
            return this;
        }
    }

    private static class OptionGroupViewHolder extends ParentViewHolder<OptionGroup, Option> {

        TextView title;
        ImageView icon;

        OptionGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(view -> {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            });
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            icon.setRotation(expanded ? -90 : 0);
        }
    }

    class OptionViewHolder extends ChildViewHolder<Option> {

        @NonNull
        private final DialogCodeGenerateOptionBinding bind;

        OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            bind = DialogCodeGenerateOptionBinding.bind(itemView);
            bind.checkbox.setOnCheckedChangeListener((compoundButton, b) -> OptionViewHolder.this.onCheckedChanged());
            itemView.setOnClickListener(view -> bind.checkbox.toggle());
        }

        void onCheckedChanged() {
            getChild().checked = bind.checkbox.isChecked();
            if (bind.checkbox.isChecked() && getChild().group.titleRes != R.string.text_options)
                uncheckOthers(getParentAdapterPosition(), getChild());
        }

    }

    private class Adapter extends ExpandableRecyclerAdapter<OptionGroup, Option, OptionGroupViewHolder, OptionViewHolder> {

        public Adapter(@NonNull List<OptionGroup> parentList) {
            super(parentList);
        }

        @NonNull
        @Override
        public OptionGroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
            return new OptionGroupViewHolder(LayoutInflater.from(parentViewGroup.getContext())
                    .inflate(R.layout.dialog_code_generate_option_group, parentViewGroup, false));
        }

        @NonNull
        @Override
        public OptionViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
            return new OptionViewHolder(LayoutInflater.from(childViewGroup.getContext())
                    .inflate(R.layout.dialog_code_generate_option, childViewGroup, false));
        }

        @Override
        public void onBindParentViewHolder(@NonNull OptionGroupViewHolder viewHolder, int parentPosition, @NonNull OptionGroup optionGroup) {
            viewHolder.title.setText(optionGroup.titleRes);
            viewHolder.icon.setRotation(viewHolder.isExpanded() ? 0 : -90);
        }

        @Override
        public void onBindChildViewHolder(@NonNull OptionViewHolder viewHolder, int parentPosition, int childPosition, @NonNull Option option) {
            viewHolder.bind.title.setText(option.titleRes);
            viewHolder.bind.checkbox.setChecked(option.checked, false);
        }
    }

}
