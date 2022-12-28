package org.autojs.autojs.network.entity.config;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Markdown {

    @SerializedName("highlight")
    private int highlight;

    @SerializedName("theme")
    private String theme;

    public int getHighlight() {
        return highlight;
    }

    public void setHighlight(int highlight) {
        this.highlight = highlight;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "Markdown{" +
                        "highlight = '" + highlight + '\'' +
                        ",theme = '" + theme + '\'' +
                        "}";
    }
}