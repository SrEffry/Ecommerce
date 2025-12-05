package com.ecommerce.factory;
import com.ecommerce.model.*;

/**
 * Patrón Factory: Crea instancias de Usuario según el tipo
 */
public interface UsuarioFactory {
    Usuario crearUsuario(String nombre, String email, String password);
}