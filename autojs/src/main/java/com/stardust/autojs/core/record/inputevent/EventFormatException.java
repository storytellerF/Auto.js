package com.stardust.autojs.core.record.inputevent;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/3/7.
 */

public class EventFormatException extends RuntimeException {


    public EventFormatException(Exception e) {
        super(e);
    }

    public EventFormatException(String eventStr, Exception e) {
        super("eventStr=" + eventStr, e);
    }

    public EventFormatException(String eventStr) {
        super("eventStr=" + eventStr);
    }

    @NonNull
    public static EventFormatException forEventStr(String eventStr, NumberFormatException e) {
        return new EventFormatException(eventStr, e);
    }
}