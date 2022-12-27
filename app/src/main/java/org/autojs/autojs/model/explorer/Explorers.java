package org.autojs.autojs.model.explorer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.app.GlobalAppContext;

import org.greenrobot.eventbus.EventBus;

public class Explorers {


    private static final Explorer sWorkspaceExplorer = new Explorer(Providers.workspace(), 20);

    private static final Explorer sExternalExplorer = new Explorer(new ExplorerFileProvider(), 10);

    @NonNull
    public static Explorer workspace() {
        return sWorkspaceExplorer;
    }

    @NonNull
    public static Explorer external() {
        return sExternalExplorer;
    }

    public static class Providers {
        @Nullable
        private static final WorkspaceFileProvider sWorkspaceFileProvider = new WorkspaceFileProvider(GlobalAppContext.get(), null);

        @Nullable
        public static WorkspaceFileProvider workspace() {
            return sWorkspaceFileProvider;
        }
    }
}
