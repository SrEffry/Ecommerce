package com.ecommerce.mediator;
import com.ecommerce.model.*;

/**
 * Patrón Mediator: Coordina las interacciones entre compradores y vendedores
 */
public interface PedidoMediator {
    Pedido procesarCompra(Usuario cliente, Producto producto, Integer cantidad, String direccion);
    void actualizarEstadoPedido(Long pedidoId, Pedido.EstadoPedido nuevoEstado, Usuario vendedor);
    void notificarCliente(Pedido pedido, String mensaje);
    void notificarVendedor(Pedido pedido, String mensaje);
}
