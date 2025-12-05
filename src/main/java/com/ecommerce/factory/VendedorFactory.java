package com.ecommerce.factory;
import com.ecommerce.model.Usuario;
import com.ecommerce.model.Usuario.TipoUsuario;

public class VendedorFactory implements UsuarioFactory {
    
    @Override
    public Usuario crearUsuario(String nombre, String email, String password) {
        Usuario vendedor = new Usuario();
        vendedor.setNombre(nombre);
        vendedor.setEmail(email);
        vendedor.setPassword(password);
        vendedor.setTipoUsuario(TipoUsuario.VENDEDOR);
        vendedor.setActivo(true);
        return vendedor;
    }
}