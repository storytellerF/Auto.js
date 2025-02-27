package com.stardust.autojs.codegeneration;

import static com.stardust.autojs.codegeneration.CodeGenerator.FIND_ONE;
import static com.stardust.autojs.codegeneration.CodeGenerator.UNTIL_FIND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;
import com.stardust.util.Consumer;

/**
 * Created by Stardust on 2017/12/7.
 */

public class UiSelectorGenerator {

    private final UiObject mRoot;
    private final UiObject mTarget;
    private boolean mUsingId = true;
    private boolean mUsingDesc = true;
    private boolean mUsingText = true;
    private int mSearchMode = FIND_ONE;

    public UiSelectorGenerator(UiObject root, UiObject target) {
        mRoot = root;
        mTarget = target;
    }

    @Nullable
    public UiGlobalSelector generateSelector() {
        UiGlobalSelector selector = new UiGlobalSelector();
        if (mUsingId &&
                tryWithStringCondition(selector, mTarget.id(), selector::id)) {
            return selector;
        }

        if (tryWithStringCondition(selector, mTarget.className(), selector::className)) {
            return selector;
        }
        if (mUsingText &&
                tryWithStringCondition(selector, mTarget.text(), selector::text)) {
            return selector;
        }
        if (mUsingDesc &&
                tryWithStringCondition(selector, mTarget.desc(), selector::desc)) {
            return selector;
        }
        if (mTarget.scrollable() && tryWithBooleanCondition(selector, mTarget.scrollable(), selector::scrollable)) {
            return selector;
        }
        if (mTarget.clickable() && tryWithBooleanCondition(selector, mTarget.clickable(), selector::clickable)) {
            return selector;
        }
        if (mTarget.selected() && tryWithBooleanCondition(selector, mTarget.selected(), selector::selected)) {
            return selector;
        }
        if (mTarget.checkable() && tryWithBooleanCondition(selector, mTarget.checkable(), selector::checkable)) {
            return selector;
        }
        if (mTarget.checked() && tryWithBooleanCondition(selector, mTarget.checked(), selector::checked)) {
            return selector;
        }
        if (mTarget.longClickable() && tryWithBooleanCondition(selector, mTarget.longClickable(), selector::longClickable)) {
            return selector;
        }
        if (tryWithIntCondition(selector, mTarget.depth(), selector::depth)) {
            return selector;
        }
        return null;
    }

    @Nullable
    public String generateSelectorCode() {
        UiGlobalSelector selector = generateSelector();
        if (selector == null) {
            return null;
        }
        if (mSearchMode == FIND_ONE) {
            return selector + ".findOne()";
        } else if (mSearchMode == UNTIL_FIND) {
            return selector + ".untilFind()";
        } else {
            return selector.toString();
        }
    }


    public void setUsingId(boolean usingId) {
        mUsingId = usingId;
    }

    public void setUsingDesc(boolean usingDesc) {
        mUsingDesc = usingDesc;
    }

    public void setUsingText(boolean usingText) {
        mUsingText = usingText;
    }

    public void setSearchMode(int searchMode) {
        mSearchMode = searchMode;
    }

    private boolean tryWithBooleanCondition(@NonNull UiGlobalSelector selector, boolean value, @NonNull Consumer<Boolean> condition) {
        condition.accept(value);
        return shouldStopGeneration(selector);
    }


    private boolean tryWithStringCondition(@NonNull UiGlobalSelector selector, @Nullable String value, @NonNull Consumer<String> condition) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        condition.accept(value);
        return shouldStopGeneration(selector);
    }

    private boolean shouldStopGeneration(@NonNull UiGlobalSelector selector) {
        if (mSearchMode == UNTIL_FIND) {
            return !selector.findAndReturnList(mRoot, 1).isEmpty();
        } else {
            return selector.findAndReturnList(mRoot, 2).size() == 1;

        }
    }

    private boolean tryWithIntCondition(@NonNull UiGlobalSelector selector, int value, @NonNull Consumer<Integer> condition) {
        condition.accept(value);
        return shouldStopGeneration(selector);
    }


}
