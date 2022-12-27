package org.autojs.autojs.tool;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/7/21.
 */

public class GsonUtils {

    @NonNull
    public static List<String> toReservedStringList(@NonNull JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        List<String> list = new ArrayList<>(array.size());
        for (int i = array.size() - 1; i >= 0; i--) {
            list.add(array.get(i).getAsString());
        }
        return list;
    }

    @NonNull
    public static List<String> toStringList(@NonNull JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        List<String> list = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i).getAsString());
        }
        return list;
    }
}
