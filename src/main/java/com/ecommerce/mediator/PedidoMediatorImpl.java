package com.ecommerce.mediator;
import com.ecommerce.model.*;
import com.ecommerce.model.Pedido.EstadoPedido;
import com.ecommerce.repository.PedidoRepository;
import com.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;

/**
 * Implementación del Mediator que coordina todas las operaciones de pedidos
 */
@Component
public class PedidoMediatorImpl implements PedidoMediator {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoMediatorImpl.class);
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Override
    public Pedido procesarCompra(Usuario cliente, Producto producto, Integer cantidad, String direccion) {
        logger.info("Iniciando proceso de compra para cliente: {} - Producto: {}", 
                   cliente.getEmail(), producto.getNombre());
        
        // Validar stock
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        // Calcular precio total
        BigDecimal precioTotal = producto.getPrecio().multiply(new BigDecimal(cantidad));
        
        // Crear pedido
        Pedido pedido = new Pedido(
            cliente,
            producto.getVendedor(),
            producto,
            cantidad,
            precioTotal,
            direccion
        );
        
        // Reducir stock
        producto.reducirStock(cantidad);
        productoRepository.save(producto);
        
        // Guardar pedido
        pedido = pedidoRepository.save(pedido);
        
        // Notificaciones
        notificarCliente(pedido, "Tu compra ha sido procesada exitosamente");
        notificarVendedor(pedido, "Tienes un nuevo pedido");
        
        logger.info("Compra procesada exitosamente. ID Pedido: {}", pedido.getId());
        return pedido;
    }
    
    @Override
    public void actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado, Usuario vendedor) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo");
        }
        logger.info("Actualizando estado del pedido {} a {}", pedidoId, nuevoEstado);
        
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        
        // Validar que el vendedor sea el propietario del pedido
        if (!pedido.getVendedor().getId().equals(vendedor.getId())) {
            throw new SecurityException("No tienes permiso para actualizar este pedido");
        }
        
        EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
        
        // Notificar al cliente del cambio de estado
        String mensaje = "El estado de tu pedido ha cambiado de %s a %s".formatted(
                estadoAnterior.getDescripcion(),
                nuevoEstado.getDescripcion());
        notificarCliente(pedido, mensaje);
        
        logger.info("Estado del pedido {} actualizado exitosamente", pedidoId);
    }
    
    @Override
    public void notificarCliente(Pedido pedido, String mensaje) {
        // Simulación de notificación al cliente
        logger.info("[NOTIFICACIÓN CLIENTE] Para: {} - Mensaje: {} - Pedido ID: {}", 
                   pedido.getCliente().getEmail(), mensaje, pedido.getId());
        // Aquí se podría implementar envío de email, SMS, etc.
    }
    
    @Override
    public void notificarVendedor(Pedido pedido, String mensaje) {
        // Simulación de notificación al vendedor
        logger.info("[NOTIFICACIÓN VENDEDOR] Para: {} - Mensaje: {} - Pedido ID: {}", 
                   pedido.getVendedor().getEmail(), mensaje, pedido.getId());
        // Aquí se podría implementar envío de email, SMS, etc.
    }
}
