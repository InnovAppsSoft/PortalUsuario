package com.marlon.portalusuario.une;

import java.util.ArrayList;

public class tarifaElect {
    public ArrayList<RangoConsumo> rangosConsumo;

    public tarifaElect() {
        this.rangosConsumo = new ArrayList<>();
        this.rangosConsumo.add(new RangoConsumo(0.0d, 100.0d, 0.33d));
        this.rangosConsumo.add(new RangoConsumo(100.0d, 150.0d, 1.07d));
        this.rangosConsumo.add(new RangoConsumo(150.0d, 200.0d, 1.43d));
        this.rangosConsumo.add(new RangoConsumo(200.0d, 250.0d, 2.46d));
        this.rangosConsumo.add(new RangoConsumo(250.0d, 300.0d, 3.0d));
        this.rangosConsumo.add(new RangoConsumo(300.0d, 350.0d, 4.0d));
        this.rangosConsumo.add(new RangoConsumo(350.0d, 400.0d, 5.0d));
        this.rangosConsumo.add(new RangoConsumo(400.0d, 450.0d, 6.0d));
        this.rangosConsumo.add(new RangoConsumo(450.0d, 500.0d, 7.0d));
        this.rangosConsumo.add(new RangoConsumo(500.0d, 600.0d, 9.2d));
        this.rangosConsumo.add(new RangoConsumo(600.0d, 700.0d, 9.45d));
        this.rangosConsumo.add(new RangoConsumo(700.0d, 1000.0d, 9.85d));
        this.rangosConsumo.add(new RangoConsumo(1000.0d, 1800.0d, 10.8d));
        this.rangosConsumo.add(new RangoConsumo(1800.0d, 2600.0d, 11.8d));
        this.rangosConsumo.add(new RangoConsumo(2600.0d, 3400.0d, 12.9d));
        this.rangosConsumo.add(new RangoConsumo(3400.0d, 4200.0d, 13.95d));
        this.rangosConsumo.add(new RangoConsumo(4200.0d, 5000.0d, 15.0d));
        this.rangosConsumo.add(new RangoConsumo(5000.0d, 9.99999999E8d, 20.0d));
    }

    public ArrayList<RangoConsumo> getRangosConsumo() {
        return this.rangosConsumo;
    }

    public void setRangosConsumo(ArrayList<RangoConsumo> rangosConsumo2) {
        this.rangosConsumo = rangosConsumo2;
    }

    public tarifaElect(ArrayList<RangoConsumo> rangosConsumo2) {
        this.rangosConsumo = rangosConsumo2;
    }

    public int size() {
        return this.rangosConsumo.size();
    }
}
