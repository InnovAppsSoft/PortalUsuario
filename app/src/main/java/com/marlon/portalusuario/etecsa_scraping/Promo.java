package com.marlon.portalusuario.etecsa_scraping;

public class Promo {
    private final String ETECSA = "https://www.etecsa.cu";
    private String svg;
    private String image;
    private String link;

    public Promo(String svg, String image, String link){
        this.svg = svg;
        this.image = image;
        this.link = link;
    }

    public String getSvg() {
        return ETECSA + svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public String getImage() {
        return ETECSA + image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return ETECSA + link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString(){
        return this.svg + "\n" + this.image + "\n" + this.link;
    }
}
