package com.ecommerce.facade;
import com.ecommerce.factory.*;
import com.ecommerce.mediator.PedidoMediator;
import com.ecommerce.model.*;
import com.ecommerce.model.Pedido.EstadoPedido;
import com.ecommerce.model.Usuario.TipoUsuario;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class EcommerceFacadeImpl implements EcommerceFacade {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioFactoryProvider usuarioFactoryProvider;
    
    @Autowired
    private DashboardFactoryProvider dashboardFactoryProvider;
    
    @Autowired
    private PedidoMediator pedidoMediator;
    
    // ===== OPERACIONES DE USUARIOS =====
    
    @Override
    public Usuario registrarUsuario(String nombre, String email, String password, TipoUsuario tipo) {
        // Validar que el email no esté registrado
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Validar que la contraseña no esté vacía
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        
        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        
        UsuarioFactory factory = usuarioFactoryProvider.obtenerFactory(tipo);
        Usuario usuario = factory.crearUsuario(nombre, email, password);
        
        if (usuario == null) {
            throw new IllegalArgumentException("No se pudo crear el usuario");
        }
        
        return usuarioRepository.save(usuario);
    }
    
    @Override
    public Usuario autenticarUsuario(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        
        // Validar que la contraseña coincida
        if (!usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        
        if (!usuario.getActivo()) {
            throw new IllegalStateException("Usuario inactivo");
        }
        
        return usuario;
    }
    
    // ===== OPERACIONES DE PRODUCTOS =====
    
    @Override
    public Producto publicarProducto(String nombre, String descripcion, BigDecimal precio,
                                    Integer stock, String categoria, Usuario vendedor) {
        if (!vendedor.getTipoUsuario().equals(TipoUsuario.VENDEDOR)) {
            throw new IllegalArgumentException("Solo los vendedores pueden publicar productos");
        }
        
        Producto producto = new Producto(nombre, descripcion, precio, stock, vendedor, categoria);
        return productoRepository.save(producto);
    }
    
    @Override
    public List<Producto> obtenerProductosDisponibles() {
        return productoRepository.findByActivoTrue();
    }
    
    @Override
    public List<Producto> obtenerProductosVendedor(Usuario vendedor) {
        return productoRepository.findByVendedor(vendedor);
    }
    
    @Override
    public Producto obtenerProductoPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        return productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }
    
    // ===== OPERACIONES DE COMPRA =====
    
    @Override
    public Pedido realizarCompra(Usuario cliente, Long productoId, Integer cantidad, String direccion) {
        if (!cliente.getTipoUsuario().equals(TipoUsuario.CLIENTE)) {
            throw new IllegalArgumentException("Solo los clientes pueden realizar compras");
        }
        
        Producto producto = obtenerProductoPorId(productoId);
        return pedidoMediator.procesarCompra(cliente, producto, cantidad, direccion);
    }
    
    @Override
    public List<Pedido> obtenerComprasCliente(Usuario cliente) {
        return pedidoRepository.findByClienteOrderByFechaPedidoDesc(cliente);
    }
    
    // ===== OPERACIONES DE VENTA =====
    
    @Override
    public List<Pedido> obtenerPedidosVendedor(Usuario vendedor) {
        return pedidoRepository.findByVendedorOrderByFechaPedidoDesc(vendedor);
    }
    
    @Override
    public void actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado, Usuario vendedor) {
        pedidoMediator.actualizarEstadoPedido(pedidoId, nuevoEstado, vendedor);
    }
    
    // ===== DASHBOARD =====
    
    @Override
    public String obtenerVistaDashboard(Usuario usuario) {
        DashboardFactory factory = dashboardFactoryProvider.obtenerDashboardFactory(usuario.getTipoUsuario());
        return factory.obtenerVistaDashboard();
    }
}