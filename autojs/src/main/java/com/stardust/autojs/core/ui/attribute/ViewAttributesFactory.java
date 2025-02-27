package com.stardust.autojs.core.ui.attribute;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stardust.autojs.core.ui.inflater.ResourceParser;

import java.util.HashMap;


public class ViewAttributesFactory {

    private static final HashMap<Class<? extends View>, ViewAttributesCreator> sViewAttributesCreators = new HashMap<>();

    static {
        sViewAttributesCreators.put(ImageView.class, ImageViewAttributes::new);
        sViewAttributesCreators.put(AppBarLayout.class, AppbarAttributes::new);
        sViewAttributesCreators.put(TextView.class, TextViewAttributes::new);
        sViewAttributesCreators.put(CardView.class, CardAttributes::new);
        sViewAttributesCreators.put(FloatingActionButton.class, FabViewAttributes::new);
    }

    public static void put(Class<? extends View> clazz, ViewAttributesCreator creator) {
        sViewAttributesCreators.put(clazz, creator);
    }

    @NonNull
    public static ViewAttributes create(@NonNull ResourceParser resourceParser, @NonNull View view) {
        Class viewClass = view.getClass();
        while (viewClass != null && !viewClass.equals(Object.class)) {
            ViewAttributesCreator creator = sViewAttributesCreators.get(viewClass);
            if (creator != null) {
                return creator.create(resourceParser, view);
            }
            viewClass = viewClass.getSuperclass();
        }
        return new ViewAttributes(resourceParser, view);
    }

    interface ViewAttributesCreator {
        @NonNull
        ViewAttributes create(ResourceParser resourceParser, View view);
    }
}
