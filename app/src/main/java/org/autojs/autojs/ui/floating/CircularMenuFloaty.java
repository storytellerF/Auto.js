package org.autojs.autojs.ui.floating;

import android.view.View;

import androidx.annotation.NonNull;

import com.stardust.enhancedfloaty.FloatyService;

public interface CircularMenuFloaty {

    View inflateActionView(FloatyService service, CircularMenuWindow window);

    @NonNull
    CircularActionMenu inflateMenuItems(FloatyService service, CircularMenuWindow window);
}
