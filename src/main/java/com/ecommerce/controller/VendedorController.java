package com.ecommerce.controller;
import com.ecommerce.facade.EcommerceFacade;
import com.ecommerce.model.*;
import com.ecommerce.model.Pedido.EstadoPedido;
import com.ecommerce.model.Usuario.TipoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/vendedor")
public class VendedorController {
    
    @Autowired
    private EcommerceFacade ecommerceFacade;
    
    /**
     * Verifica que el usuario sea un vendedor
     */
    private Usuario verificarVendedor(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            throw new SecurityException("No autenticado");
        }
        if (!usuario.getTipoUsuario().equals(TipoUsuario.VENDEDOR)) {
            throw new SecurityException("Acceso denegado");
        }
        return usuario;
    }
    
    @GetMapping("/mis-productos")
    public String verMisProductos(HttpSession session, Model model) {
        try {
            Usuario vendedor = verificarVendedor(session);
            List<Producto> productos = ecommerceFacade.obtenerProductosVendedor(vendedor);
            model.addAttribute("productos", productos);
            return "mis-productos";
        } catch (SecurityException e) {
            return "redirect:/login";
        }
    }
    
    @GetMapping("/publicar-producto")
    public String mostrarFormularioPublicar(HttpSession session) {
        try {
            verificarVendedor(session);
            return "publicar-producto";
        } catch (SecurityException e) {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/publicar-producto")
    public String publicarProducto(@RequestParam String nombre,
                                  @RequestParam String descripcion,
                                  @RequestParam BigDecimal precio,
                                  @RequestParam Integer stock,
                                  @RequestParam String categoria,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Usuario vendedor = verificarVendedor(session);
            ecommerceFacade.publicarProducto(nombre, descripcion, precio, stock, categoria, vendedor);
            redirectAttributes.addFlashAttribute("success", "Producto publicado exitosamente");
            return "redirect:/vendedor/mis-productos";
        } catch (SecurityException e) {
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al publicar producto: " + e.getMessage());
            return "redirect:/vendedor/publicar-producto";
        }
    }
    
    @GetMapping("/pedidos")
    public String verPedidos(HttpSession session, Model model) {
        try {
            Usuario vendedor = verificarVendedor(session);
            List<Pedido> pedidos = ecommerceFacade.obtenerPedidosVendedor(vendedor);
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("estados", EstadoPedido.values());
            return "pedidos-vendedor";
        } catch (SecurityException e) {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/actualizar-estado-pedido")
    public String actualizarEstadoPedido(@RequestParam Long pedidoId,
                                        @RequestParam String nuevoEstado,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        try {
            Usuario vendedor = verificarVendedor(session);
            EstadoPedido estado = EstadoPedido.valueOf(nuevoEstado);
            ecommerceFacade.actualizarEstadoPedido(pedidoId, estado, vendedor);
            redirectAttributes.addFlashAttribute("success", "Estado del pedido actualizado");
            return "redirect:/vendedor/pedidos";
        } catch (SecurityException e) {
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar estado: " + e.getMessage());
            return "redirect:/vendedor/pedidos";
        }
    }
}