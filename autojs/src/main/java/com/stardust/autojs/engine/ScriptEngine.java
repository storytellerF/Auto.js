package com.stardust.autojs.engine;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.ScriptSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/4/2.
 * <p>
 * <p>
 * A ScriptEngine is created by {@link ScriptEngineManager#createEngine(String, int)} ()}, and then can be
 * used to execute script with {@link ScriptEngine#execute(ScriptSource)} in the **same** thread.
 * When the execution finish successfully, the engine should be destroy in the thread that created it.
 * <p>
 * If you want to stop the engine in other threads, you should call {@link ScriptEngine#forceStop()}.
 */

public interface ScriptEngine<S extends ScriptSource> {


    String TAG_ENV_PATH = "env_path";
    String TAG_SOURCE = "source";
    String TAG_WORKING_DIRECTORY = "execute_path";

    void put(String name, Object value);

    Object execute(S scriptSource);

    void forceStop();

    void destroy();

    boolean isDestroyed();

    void setTag(String key, Object value);

    @Nullable
    Object getTag(String key);

    @Nullable
    String cwd();

    void uncaughtException(Throwable throwable);

    Throwable getUncaughtException();

    int getId();

    void setId(int id);

    /**
     * @hide
     */
    void setOnDestroyListener(OnDestroyListener listener);

    /**
     * @hide
     */
    void init();

    interface OnDestroyListener {
        void onDestroy(ScriptEngine engine);
    }

    abstract class AbstractScriptEngine<S extends ScriptSource> implements ScriptEngine<S> {


        private final Map<String, Object> mTags = new ConcurrentHashMap<>();
        private final AtomicInteger mId = new AtomicInteger(ScriptExecution.NO_ID);
        private OnDestroyListener mOnDestroyListener;
        private volatile boolean mDestroyed = false;
        private Throwable mUncaughtException;

        @Override
        public void setTag(String key, @Nullable Object value) {
            if (value == null) {
                mTags.remove(key);
            } else {
                mTags.put(key, value);
            }
        }

        @Nullable
        @Override
        public Object getTag(String key) {
            return mTags.get(key);
        }

        @Override
        public boolean isDestroyed() {
            return mDestroyed;
        }

        @CallSuper
        @Override
        public void destroy() {
            if (mOnDestroyListener != null) {
                mOnDestroyListener.onDestroy(this);
            }
            mDestroyed = true;
        }

        @Nullable
        public String cwd() {
            return (String) getTag(TAG_WORKING_DIRECTORY);
        }

        public void setOnDestroyListener(OnDestroyListener onDestroyListener) {
            if (mOnDestroyListener != null)
                throw new SecurityException("setOnDestroyListener can be called only once");
            mOnDestroyListener = onDestroyListener;
        }

        @Override
        public void uncaughtException(Throwable throwable) {
            mUncaughtException = throwable;
            forceStop();
        }

        @Override
        public Throwable getUncaughtException() {
            return mUncaughtException;
        }

        @Override
        public int getId() {
            return mId.get();
        }

        @Override
        public void setId(int id) {
            mId.compareAndSet(ScriptExecution.NO_ID, id);
        }
    }
}
