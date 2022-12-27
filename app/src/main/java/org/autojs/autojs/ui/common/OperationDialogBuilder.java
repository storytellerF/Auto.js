package org.autojs.autojs.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.floating.CircularMenu;

import java.util.ArrayList;

/**
 * Created by Stardust on 2017/6/26.
 */

public class OperationDialogBuilder extends MaterialDialog.Builder {

    @NonNull
    private final RecyclerView mOperations;
    private final ArrayList<Integer> mIds = new ArrayList<>();
    private final ArrayList<Integer> mIcons = new ArrayList<>();
    private final ArrayList<String> mTexts = new ArrayList<>();
    private CircularMenu mOnItemClickTarget;

    public OperationDialogBuilder(@NonNull Context context) {
        super(context);
        mOperations = new RecyclerView(context);
        mOperations.setLayoutManager(new LinearLayoutManager(context));
        mOperations.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @Override
            @NonNull
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.operation_dialog_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.itemView.setId(mIds.get(position));
                holder.text.setText(mTexts.get(position));
                holder.icon.setImageResource(mIcons.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.itemView.getId() == R.id.layout_bounds) {
                            mOnItemClickTarget.showLayoutBounds();
                        } else if (holder.itemView.getId() == R.id.layout_hierarchy) {
                            mOnItemClickTarget.showLayoutHierarchy();
                        } else if (holder.itemView.getId() == R.id.accessibility_service) {
                            mOnItemClickTarget.enableAccessibilityService();
                        } else if (holder.itemView.getId() == R.id.package_name) {
                            mOnItemClickTarget.copyPackageName();
                        } else if (holder.itemView.getId() == R.id.class_name) {
                            mOnItemClickTarget.copyActivityName();
                        } else if (holder.itemView.getId() == R.id.open_launcher) {
                            mOnItemClickTarget.openLauncher();
                        } else if (holder.itemView.getId() == R.id.exit) {
                            mOnItemClickTarget.close();
                        } else if (holder.itemView.getId() == R.id.pointer_location) {
                            mOnItemClickTarget.togglePointerLocation();
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mIds.size();
            }
        });
        customView(mOperations, false);
    }

    @NonNull
    public OperationDialogBuilder item(int id, int iconRes, int textRes) {
        return item(id, iconRes, getContext().getString(textRes));
    }

    @NonNull
    public OperationDialogBuilder item(int id, int iconRes, String text) {
        mIds.add(id);
        mIcons.add(iconRes);
        mTexts.add(text);
        return this;
    }

    @NonNull
    public OperationDialogBuilder bindItemClick(CircularMenu target) {
        mOnItemClickTarget = target;
        return this;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            text = itemView.findViewById(R.id.text);
        }

    }
}