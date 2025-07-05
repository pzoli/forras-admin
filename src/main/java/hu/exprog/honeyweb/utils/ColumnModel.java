package hu.exprog.honeyweb.utils;

import java.io.Serializable;

import javax.faces.convert.Converter;

public class ColumnModel implements Serializable {

	private static final long serialVersionUID = 4063465058747639784L;

	private String header;
	private String property;
	private String format;
	private String sortField;
	private String filterField;
	private Converter converter;

	public ColumnModel(String header, String property, String sortField, String filterField, String format, Converter converter) {
		this.header = header;
		this.property = property;
		this.sortField = sortField;
		this.filterField = filterField;
		this.format = format;
		this.converter = converter;
	}

	public String getHeader() {
		return header;
	}

	public String getProperty() {
		return property;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getFilterField() {
		return filterField;
	}

	public void setFilterField(String filterField) {
		this.filterField = filterField;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}
		
}
