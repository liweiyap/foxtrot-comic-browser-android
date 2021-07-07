package com.liweiyap.foxtrot.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * https://stackoverflow.com/a/53902935/12367873
 * https://stackoverflow.com/a/45071364/12367873
 * https://medium.com/@toddcookevt/android-room-storing-lists-of-objects-766cca57e3f9
 */
class DataConverter {

    @TypeConverter
    fun getArrayListFromJsonString(string: String?): ArrayList<String?>? {
        if (string == null) {
            return arrayListOf()
        }

        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(string, listType)
    }

    @TypeConverter
    fun getJsonStringFromArrayList(list: ArrayList<String?>?): String? = Gson().toJson(list)
}