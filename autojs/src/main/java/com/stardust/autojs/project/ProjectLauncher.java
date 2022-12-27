package com.stardust.autojs.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.script.JavaScriptFileSource;

import java.io.File;

public class ProjectLauncher {

    private final String mProjectDir;
    @NonNull
    private final File mMainScriptFile;
    @Nullable
    private final ProjectConfig mProjectConfig;

    public ProjectLauncher(String projectDir) {
        mProjectDir = projectDir;
        mProjectConfig = ProjectConfig.fromProjectDir(projectDir);
        mMainScriptFile = new File(mProjectDir, mProjectConfig.getMainScriptFile());
    }

    public void launch(@NonNull ScriptEngineService service) {
        ExecutionConfig config = new ExecutionConfig();
        config.setWorkingDirectory(mProjectDir);
        config.getScriptConfig().setFeatures(mProjectConfig.getFeatures());
        service.execute(new JavaScriptFileSource(mMainScriptFile), config);
    }

}
