package com.ecommerce.factory;
import com.ecommerce.model.Usuario.TipoUsuario;
import org.springframework.stereotype.Component;

/**
 * Factory principal que selecciona qué factory usar
 */
@Component
public class UsuarioFactoryProvider {
    
    public UsuarioFactory obtenerFactory(TipoUsuario tipoUsuario) {
        switch (tipoUsuario) {
            case CLIENTE:
                return new ClienteFactory();
            case VENDEDOR:
                return new VendedorFactory();
            default:
                throw new IllegalArgumentException("Tipo de usuario no válido: " + tipoUsuario);
        }
    }
}
