package org.autojs.autojs.model.indices;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ClassSearchingItem implements Comparable<ClassSearchingItem> {

    static final String BASE_URL = "http://www.android-doc.com/reference/";

    protected int rank;

    public abstract boolean matches(String keywords);

    public abstract String getLabel();

    @NonNull
    public abstract String getUrl();

    @Override
    public int compareTo(@NonNull ClassSearchingItem o) {
        return Integer.compare(o.rank, rank);
    }

    protected int rank(@NonNull String words, @NonNull String keywords) {
        int length = words.length();
        int i = words.indexOf(keywords);
        if (i < 0) {
            return 0;
        }
        //full matches
        if (i == 0 && keywords.length() == length) {
            return 10;
        }
        //words ends with keywords
        if (i + keywords.length() == length) {
            if (i > 0 && words.charAt(i - 1) == '.') {
                return 9;
            }
            return 8;
        }
        //package starts with keywords
        if (i > 0 && words.charAt(i - 1) == '.') {
            //package equals keywords
            if (i < length - 1 && words.charAt(i + 1) == '.') {
                return 7;
            }
            return 6;
        }
        //package ends with keywords
        if (i < length - 1 && words.charAt(i + 1) == '.') {
            return 6;
        }
        if (i == 0) {
            return 5;
        }
        return 1;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClassSearchingItem{" + getLabel() + "}";
    }

    @NonNull
    public abstract String getImportText();

    public static class ClassItem extends ClassSearchingItem {

        private final AndroidClass mAndroidClass;

        public ClassItem(AndroidClass androidClass) {
            mAndroidClass = androidClass;
        }

        @Override
        public boolean matches(@NonNull String keywords) {
            rank = rank(mAndroidClass.getFullName(), keywords);
            Log.d("ClassSearching", "rank = " + rank + ", word = " + mAndroidClass.getFullName());
            return rank > 0;
        }


        @NonNull
        public String getLabel() {
            return String.format("%s (%s)", mAndroidClass.getClassName(), mAndroidClass.getPackageName());
        }

        @NonNull
        @Override
        public String getUrl() {
            return BASE_URL + mAndroidClass.getPackageName().replace('.', '/')
                    + "/" + mAndroidClass.getClassName() + ".html";
        }

        @NonNull
        @Override
        public String getImportText() {
            return String.format("importClass(%s)", mAndroidClass.getFullName());
        }

        public AndroidClass getAndroidClass() {
            return mAndroidClass;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassItem classItem = (ClassItem) o;
            return mAndroidClass.equals(classItem.mAndroidClass);
        }

        @Override
        public int hashCode() {
            return mAndroidClass.hashCode();
        }
    }

    public static class PackageItem extends ClassSearchingItem {

        private final String mPackageName;

        public PackageItem(String packageName) {
            mPackageName = packageName;
        }

        @Override
        public boolean matches(@NonNull String keywords) {
            rank = rank(mPackageName, keywords);
            return rank > 0;
        }

        @Override
        public String getLabel() {
            return mPackageName;
        }

        @NonNull
        @Override
        public String getUrl() {
            return BASE_URL + mPackageName.replace('.', '/') + "/package-summary.html";
        }

        @NonNull
        @Override
        public String getImportText() {
            return String.format("importPackage(%s)", mPackageName);
        }

        public String getPackageName() {
            return mPackageName;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PackageItem that = (PackageItem) o;
            return mPackageName.equals(that.mPackageName);
        }

        @Override
        public int hashCode() {
            return mPackageName.hashCode();
        }
    }
}
