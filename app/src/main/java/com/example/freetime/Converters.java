package com.example.freetime;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.List;

public class Converters {

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) return null;
        return String.join(",", list);
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null) return null;
        return Arrays.asList(value.split(","));
    }
}
