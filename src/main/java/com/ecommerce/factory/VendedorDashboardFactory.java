package com.ecommerce.factory;

public class VendedorDashboardFactory implements DashboardFactory {
    
    @Override
    public String obtenerVistaDashboard() {
        return "dashboard-vendedor";
    }
    
    @Override
    public String obtenerTituloDashboard() {
        return "Mi Panel de Ventas";
    }
}