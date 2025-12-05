package com.ecommerce.facade;
import com.ecommerce.model.*;
import com.ecommerce.model.Usuario.TipoUsuario;
import java.math.BigDecimal;
import java.util.List;

/**
 * Patrón Facade: Proporciona una interfaz unificada y simplificada 
 * para todas las operaciones del sistema
 */
public interface EcommerceFacade {
    
    // Operaciones de usuarios
    Usuario registrarUsuario(String nombre, String email, String password, TipoUsuario tipo);
    Usuario autenticarUsuario(String email, String password);
    
    // Operaciones de productos
    Producto publicarProducto(String nombre, String descripcion, BigDecimal precio, 
                             Integer stock, String categoria, Usuario vendedor);
    List<Producto> obtenerProductosDisponibles();
    List<Producto> obtenerProductosVendedor(Usuario vendedor);
    Producto obtenerProductoPorId(Long id);
    
    // Operaciones de compra (cliente)
    Pedido realizarCompra(Usuario cliente, Long productoId, Integer cantidad, String direccion);
    List<Pedido> obtenerComprasCliente(Usuario cliente);
    
    // Operaciones de venta (vendedor)
    List<Pedido> obtenerPedidosVendedor(Usuario vendedor);
    void actualizarEstadoPedido(Long pedidoId, Pedido.EstadoPedido nuevoEstado, Usuario vendedor);
    
    // Dashboard
    String obtenerVistaDashboard(Usuario usuario);
}