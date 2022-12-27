package org.autojs.autojs.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.main.community.CommunityWebView;

import java.util.ArrayList;


/**
 * Created by Stardust on 2017/10/20.
 */

public class OptionListView extends LinearLayout {


    private final ArrayList<Integer> mIds = new ArrayList<>();
    private final ArrayList<Integer> mIcons = new ArrayList<>();
    private final ArrayList<String> mTexts = new ArrayList<>();
    private CommunityWebView mOnItemClickTarget;
    private TextView mTitleView;
    public OptionListView(Context context) {
        super(context);
    }

    public OptionListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = findViewById(R.id.title);
        RecyclerView mOptionList = findViewById(R.id.list);
        mOptionList.setLayoutManager(new LinearLayoutManager(getContext()));
        mOptionList.setAdapter(new Adapter());
    }

    public static class Builder {

        @NonNull
        private final OptionListView mOptionListView;
        private final Context mContext;

        public Builder(Context context) {
            mContext = context;
            mOptionListView = (OptionListView) View.inflate(context, R.layout.option_list_view, null);
        }

        @NonNull
        public Builder item(int id, int iconRes, int textRes) {
            return item(id, iconRes, mContext.getString(textRes));
        }

        @NonNull
        public Builder item(int id, int iconRes, String text) {
            mOptionListView.mIds.add(id);
            mOptionListView.mIcons.add(iconRes);
            mOptionListView.mTexts.add(text);
            return this;
        }

        @NonNull
        public Builder bindItemClick(CommunityWebView target) {
            mOptionListView.mOnItemClickTarget = target;
            return this;
        }

        @NonNull
        public Builder title(String title) {
            mOptionListView.mTitleView.setVisibility(VISIBLE);
            mOptionListView.mTitleView.setText(title);
            return this;
        }

        @NonNull
        public Builder title(int title) {
            return title(mContext.getString(title));
        }

        @NonNull
        public OptionListView build() {
            return mOptionListView;
        }
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

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.operation_dialog_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setId(mIds.get(position));
            holder.text.setText(mTexts.get(position));
            holder.icon.setImageResource(mIcons.get(position));
            holder.itemView.setOnClickListener(view -> {
                if (mIds.get(position)==R.id.run){
                    mOnItemClickTarget.run();
                }else {
                    mOnItemClickTarget.save();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mIds.size();
        }
    }
}
