package com.stardust.autojs.core.util;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.util.ProcessUtils;
import com.stardust.pio.UncheckedIOException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Stardust on 2017/1/20.
 * <p>
 * 来自网络~~
 */

public class ProcessShell extends AbstractShell {

    private static final String TAG = "ProcessShell";
    private final StringBuilder mSucceedOutput = new StringBuilder();
    private final StringBuilder mErrorOutput = new StringBuilder();
    @Nullable
    private Process mProcess;
    private DataOutputStream mCommandOutputStream;
    @Nullable
    private BufferedReader mSucceedReader;
    @Nullable
    private BufferedReader mErrorReader;

    public ProcessShell() {

    }

    public ProcessShell(boolean root) {
        super(root);
    }

    @NonNull
    public static Result exec(@NonNull String command, boolean isRoot) {
        String[] commands = command.split("\n");
        return exec(commands, isRoot);
    }

    @NonNull
    public static Result exec(@NonNull String[] commands, boolean isRoot) {
        ProcessShell shell = null;
        try {
            shell = new ProcessShell(isRoot);
            for (String command : commands) {
                shell.exec(command);
            }
            shell.exec(COMMAND_EXIT);
            Result result = new Result();
            result.code = shell.waitFor();
            shell.readAll();
            result.error = shell.getErrorOutput().toString();
            result.result = shell.getSucceedOutput().toString();
            shell.exit();
            return result;
        } finally {
            if (shell != null) {
                shell.exit();
            }
        }
    }

    @NonNull
    public static Result execCommand(@Nullable String[] commands, boolean isRoot) {
        Result commandResult = new Result();
        if (commands == null || commands.length == 0)
            throw new IllegalArgumentException("command is empty");
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            Log.d(TAG, "pid = " + ProcessUtils.getProcessPid(process));
            commandResult.code = process.waitFor();
            commandResult.result = readAll(process.getInputStream());
            commandResult.error = readAll(process.getErrorStream());
            Log.d(TAG, commandResult.toString());
        } catch (Exception e) {
            throw new ScriptInterruptedException(e);
        } finally {
            try {
                if (os != null) os.close();
                if (process != null) {
                    process.getInputStream().close();
                    process.getOutputStream().close();
                }
            } catch (IOException ignored) {

            }
            if (process != null) {
                process.destroy();
            }
        }
        return commandResult;
    }

    @NonNull
    private static String readAll(InputStream inputStream) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            builder.append(line).append('\n');
        }
        return builder.toString();
    }

    @NonNull
    public static Result execCommand(@NonNull String command, boolean isRoot) {
        String[] commands = command.split("\n");
        return execCommand(commands, isRoot);
    }

    @Override
    protected void init(String initialCommand) {
        try {
            mProcess = new ProcessBuilder(initialCommand).redirectErrorStream(true).start();
            mCommandOutputStream = new DataOutputStream(mProcess.getOutputStream());
            mSucceedReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            mErrorReader = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void exec(@NonNull String command) {
        try {
            mCommandOutputStream.writeBytes(command);
            if (!command.endsWith(COMMAND_LINE_END)) {
                mCommandOutputStream.writeBytes(COMMAND_LINE_END);
            }
            mCommandOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exit() {
        if (mProcess != null) {
            Log.d(TAG, "exit: pid = " + ProcessUtils.getProcessPid(mProcess));
            mProcess.destroy();
            mProcess = null;
        }
        if (mSucceedReader != null) {
            try {
                mSucceedReader.close();
            } catch (IOException ignored) {

            }
            mSucceedReader = null;
        }
        if (mErrorReader != null) {
            try {
                mErrorReader.close();
            } catch (IOException ignored) {

            }
            mErrorReader = null;
        }

    }

    @Override
    public void exitAndWaitFor() {
        exec(COMMAND_EXIT);
        waitFor();
        exit();
    }

    public int waitFor() {
        try {
            return mProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public ProcessShell readAll() {
        return readSucceedOutput().readErrorOutput();
    }

    @NonNull
    public ProcessShell readSucceedOutput() {
        read(mSucceedReader, mSucceedOutput);
        return this;
    }

    private void read(@NonNull BufferedReader reader, @NonNull StringBuilder sb) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NonNull
    public ProcessShell readErrorOutput() {
        read(mErrorReader, mErrorOutput);
        return this;
    }

    @NonNull
    public StringBuilder getSucceedOutput() {
        return mSucceedOutput;
    }

    @NonNull
    public StringBuilder getErrorOutput() {
        return mErrorOutput;
    }

    @Nullable
    public Process getProcess() {
        return mProcess;
    }

    @Nullable
    public BufferedReader getSucceedReader() {
        return mSucceedReader;
    }

    @Nullable
    public BufferedReader getErrorReader() {
        return mErrorReader;
    }


}