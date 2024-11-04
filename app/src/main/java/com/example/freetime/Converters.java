/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Contiene métodos para convertir datos entre tipos específicos usados en la base de datos.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Utilizado por la base de datos para convertir tipos de datos.
 */


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
