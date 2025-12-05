package com.ecommerce.repository;
import com.ecommerce.model.*;
import com.ecommerce.model.Pedido.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(Usuario cliente);
    List<Pedido> findByVendedor(Usuario vendedor);
    List<Pedido> findByClienteOrderByFechaPedidoDesc(Usuario cliente);
    List<Pedido> findByVendedorOrderByFechaPedidoDesc(Usuario vendedor);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByClienteAndEstado(Usuario cliente, EstadoPedido estado);
    List<Pedido> findByVendedorAndEstado(Usuario vendedor, EstadoPedido estado);
}