package com.marlon.portalusuario.une;

public class RangoConsumo {
    public double finRango;
    public double inicioRango;
    public double precioRango;

    public RangoConsumo() {
    }

    public RangoConsumo(double inicioRango2, double finRango2, double precioRango2) {
        this.inicioRango = inicioRango2;
        this.finRango = finRango2;
        this.precioRango = precioRango2;
    }

    public double getFinRango() {
        return this.finRango;
    }

    public double getInicioRango() {
        return this.inicioRango;
    }

    public double getPrecioRango() {
        return this.precioRango;
    }

    public void setFinRango(int finRango2) {
        this.finRango = finRango2;
    }

    public void setInicioRango(int inicioRango2) {
        this.inicioRango = inicioRango2;
    }

    public void setPrecioRango(double precioRango2) {
        this.precioRango = precioRango2;
    }
}