package com.ecommerce.factory;
import com.ecommerce.model.Usuario;
import com.ecommerce.model.Usuario.TipoUsuario;

public class ClienteFactory implements UsuarioFactory {
    
    @Override
    public Usuario crearUsuario(String nombre, String email, String password) {
        Usuario cliente = new Usuario();
        cliente.setNombre(nombre);
        cliente.setEmail(email);
        cliente.setPassword(password);
        cliente.setTipoUsuario(TipoUsuario.CLIENTE);
        cliente.setActivo(true);
        return cliente;
    }
}