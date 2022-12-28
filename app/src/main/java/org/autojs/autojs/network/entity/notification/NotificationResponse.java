package org.autojs.autojs.network.entity.notification;

import androidx.annotation.NonNull;

import java.util.List;

public class NotificationResponse {

    private List<Notification> notifications;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "NotificationResponse{" +
                        "notifications = '" + notifications + '\'' +
                        "}";
    }
}