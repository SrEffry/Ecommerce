package com.ecommerce.controller;
import com.ecommerce.facade.EcommerceFacade;
import com.ecommerce.model.*;
import com.ecommerce.model.Usuario.TipoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    @Autowired
    private EcommerceFacade ecommerceFacade;
    
    /**
     * Verifica que el usuario sea un cliente
     */
    private Usuario verificarCliente(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            throw new SecurityException("No autenticado");
        }
        if (!usuario.getTipoUsuario().equals(TipoUsuario.CLIENTE)) {
            throw new SecurityException("Acceso denegado");
        }
        return usuario;
    }
    
    /**
     * Muestra el catálogo de productos disponibles
     */
    @GetMapping("/catalogo")
    public String verCatalogo(HttpSession session, Model model) {
        try {
            verificarCliente(session);
            List<Producto> productos = ecommerceFacade.obtenerProductosDisponibles();
            model.addAttribute("productos", productos);
            return "Catalogo-productos";
        } catch (SecurityException e) {
            return "redirect:/login";
        }
    }
    
    /**
     * Muestra el detalle de un producto específico
     */
    @GetMapping("/producto/{id}")
    public String verDetalleProducto(@PathVariable Long id, 
                                    HttpSession session, 
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            verificarCliente(session);
            Producto producto = ecommerceFacade.obtenerProductoPorId(id);
            model.addAttribute("producto", producto);
            return "Detalle-producto";
        } catch (SecurityException e) {
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/cliente/catalogo";
        }
    }
    
    /**
     * Procesa la compra de un producto
     */
    @PostMapping("/comprar")
    public String realizarCompra(@RequestParam Long productoId,
                                @RequestParam Integer cantidad,
                                @RequestParam String direccion,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario cliente = verificarCliente(session);
            
            // Validar datos de entrada
            if (cantidad <= 0) {
                redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a 0");
                return "redirect:/cliente/producto/" + productoId;
            }
            
            if (direccion == null || direccion.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debes proporcionar una dirección de envío");
                return "redirect:/cliente/producto/" + productoId;
            }
            
            ecommerceFacade.realizarCompra(cliente, productoId, cantidad, direccion);
            redirectAttributes.addFlashAttribute("success", "¡Compra realizada exitosamente!");
            return "redirect:/cliente/mis-compras";
        } catch (SecurityException e) {
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la compra: " + e.getMessage());
            return "redirect:/cliente/catalogo";
        }
    }
    
    /**
     * Muestra el historial de compras del cliente
     */
    @GetMapping("/mis-compras")
    public String verMisCompras(HttpSession session, Model model) {
        try {
            Usuario cliente = verificarCliente(session);
            List<Pedido> compras = ecommerceFacade.obtenerComprasCliente(cliente);
            model.addAttribute("compras", compras);
            return "Mis-compras";
        } catch (SecurityException e) {
            return "redirect:/login";
        }
    }
}