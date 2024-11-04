/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Interfaz DAO para gestionar las operaciones de base de datos relacionadas con la entidad User. Proporciona métodos para insertar, actualizar, eliminar y consultar datos de usuario.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Utilizado para realizar operaciones CRUD en la entidad User en la base de datos.
 */

package com.example.freetime.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.freetime.entities.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);
    @Update
    void updateUser(User user);


    // Consulta para obtener un usuario por su ID
    @Query("SELECT * FROM user WHERE id = :userId")
    User getUserById(int userId);

    // Consulta para obtener todos los usuarios
    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    // Consulta para obtener un usuario por su correo electrónico
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
}
