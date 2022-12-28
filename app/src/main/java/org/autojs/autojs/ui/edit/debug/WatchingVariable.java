package org.autojs.autojs.ui.edit.debug;

import androidx.annotation.Nullable;

public class WatchingVariable {

    private final boolean mPinned;
    private String mDisplayName;
    private String mName;
    @Nullable
    private String mValue;
    @Nullable
    private String mSingleLineValue;

    public WatchingVariable(String displayName, String name, boolean pinned) {
        mDisplayName = displayName;
        mName = name;
        mPinned = pinned;
    }

    public WatchingVariable(String name) {
        this(name, name, false);
    }

    public boolean isPinned() {
        return mPinned;
    }

    @Nullable
    public String getValue() {
        return mValue;
    }

    public void setValue(@Nullable String value) {
        mValue = value;
        mSingleLineValue = value == null ? null : value.replaceAll("\n", " ");
    }

    @Nullable
    public String getSingleLineValue() {
        return mSingleLineValue;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
