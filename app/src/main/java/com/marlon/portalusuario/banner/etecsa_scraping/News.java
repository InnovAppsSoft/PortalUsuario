package com.marlon.portalusuario.banner.etecsa_scraping;

public class News {
	private String title;
	private String link;
	public News() {
		// TODO Auto-generated constructor stub
	}
	public News(String title, String link) {
		this.title = title;
		this.link = link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	@Override
	public String toString() {
		return this.title;
	}
}
