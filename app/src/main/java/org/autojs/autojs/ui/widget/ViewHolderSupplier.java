package org.autojs.autojs.ui.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;

/**
 * Created by Stardust on 2017/4/8.
 */

public interface ViewHolderSupplier<VH extends RecyclerView.ViewHolder> {

    @NonNull
    static <VH extends RecyclerView.ViewHolder> ViewHolderSupplier<VH> of(@NonNull final Class<VH> c, final int layoutRes) {
        return (parent, viewType) -> {
            try {
                Constructor<VH> constructor = c.getConstructor(View.class);
                return constructor.newInstance(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @NonNull
    static <VH extends RecyclerView.ViewHolder> ViewHolderSupplier<VH> of(@NonNull ViewHolderCreator<VH> creator, final int layoutRes) {
        return (parent, viewType) ->
                creator.createViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false)
                );
    }

    @NonNull
    VH createViewHolder(ViewGroup parent, int viewType);

    interface ViewHolderCreator<VH extends RecyclerView.ViewHolder> {
        @NonNull
        VH createViewHolder(View itemView);
    }


}
