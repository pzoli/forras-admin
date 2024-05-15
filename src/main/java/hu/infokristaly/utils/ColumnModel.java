package hu.infokristaly.utils;

import java.io.Serializable;

public class ColumnModel implements Serializable {

	private static final long serialVersionUID = 4063465058747639784L;

	private String header;
	private String property;

	public ColumnModel(String header, String property) {
		this.header = header;
		this.property = property;
	}

	public String getHeader() {
		return header;
	}

	public String getProperty() {
		return property;
	}
}
