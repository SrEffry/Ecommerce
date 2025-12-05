package com.ecommerce.factory;

public class ClienteDashboardFactory implements DashboardFactory {
    
    @Override
    public String obtenerVistaDashboard() {
        return "dashboard-cliente";
    }
    
    @Override
    public String obtenerTituloDashboard() {
        return "Mi Panel de Compras";
    }
}