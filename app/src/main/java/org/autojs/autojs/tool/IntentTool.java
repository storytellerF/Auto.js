package org.autojs.autojs.tool;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.stardust.util.IntentUtil;

import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/4/12.
 */

public class IntentTool {

    public static void browse(@NonNull Context context, String url) {
        if (!IntentUtil.browse(context, url)) {
            Toast.makeText(context, R.string.text_no_brower, Toast.LENGTH_SHORT).show();
        }
    }
}
