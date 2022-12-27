package com.stardust.autojs.core.database;

import androidx.annotation.NonNull;

public class Databases {

    @NonNull
    public static Database openDatabase(String name, int version, String desc, long size){
        return new Database();
    }



}
