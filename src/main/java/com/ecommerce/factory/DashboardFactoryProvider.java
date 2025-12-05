package com.ecommerce.factory;
import com.ecommerce.model.Usuario.TipoUsuario;
import org.springframework.stereotype.Component;

@Component
public class DashboardFactoryProvider {
    
    public DashboardFactory obtenerDashboardFactory(TipoUsuario tipoUsuario) {
        switch (tipoUsuario) {
            case CLIENTE:
                return new ClienteDashboardFactory();
            case VENDEDOR:
                return new VendedorDashboardFactory();
            default:
                throw new IllegalArgumentException("Tipo de usuario no válido");
        }
    }
}