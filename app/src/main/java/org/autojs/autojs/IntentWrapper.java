package org.autojs.autojs;

import android.content.Context;
import android.content.Intent;

/**
 * @author storyteller_f
 */
public class IntentWrapper {
    public Intent intent;
    private Context context;

    public IntentWrapper(Context context, Intent intent) {
        this.intent = intent;
        this.context = context;
    }

    public IntentWrapper(Context mainActivity) {

    }

    public void start() {
        context.startActivity(intent);
    }
}
