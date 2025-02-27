package org.autojs.autojs.model.autocomplete;

import android.text.Editable;

import androidx.annotation.NonNull;

import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2018/2/26.
 */

public class AnyWordsCompletion extends SimpleTextWatcher {


    private static final String PATTERN = "[\\W]";
    private final ExecutorService mExecutorService;
    private final AtomicInteger mExecuteId = new AtomicInteger();
    private volatile DictionaryTree<String> mDictionaryTree;

    public AnyWordsCompletion(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    @Override
    public void afterTextChanged(@NonNull Editable s) {
        String str = s.toString();
        int id = mExecuteId.incrementAndGet();
        mExecutorService.execute(() -> splitWords(id, str));
    }

    private void splitWords(int id, @NonNull String s) {
        if (id != mExecuteId.get()) {
            return;
        }
        DictionaryTree<String> tree = new DictionaryTree<>();
        String[] words = s.split(PATTERN);
        for (String word : words) {
            if (id != mExecuteId.get()) {
                return;
            }
            tree.putWord(word, word);
        }
        mDictionaryTree = tree;
    }

    public void findCodeCompletion(@NonNull List<CodeCompletion> completions, @NonNull String wordPrefill) {
        if (mDictionaryTree == null)
            return;
        List<DictionaryTree.Entry<String>> result = mDictionaryTree.searchByPrefill(wordPrefill);
        for (DictionaryTree.Entry<String> entry : result) {
            completions.add(new CodeCompletion(entry.tag, null, wordPrefill.length()));
        }
    }
}
