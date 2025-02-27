package org.autojs.autojs.network.entity.config;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Cookies {

    @SerializedName("link")
    private String link;

    @SerializedName("dismiss")
    private String dismiss;

    @SerializedName("message")
    private String message;

    @SerializedName("enabled")
    private boolean enabled;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDismiss() {
        return dismiss;
    }

    public void setDismiss(String dismiss) {
        this.dismiss = dismiss;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "Cookies{" +
                        "link = '" + link + '\'' +
                        ",dismiss = '" + dismiss + '\'' +
                        ",message = '" + message + '\'' +
                        ",enabled = '" + enabled + '\'' +
                        "}";
    }
}