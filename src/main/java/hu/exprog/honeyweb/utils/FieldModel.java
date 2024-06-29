package hu.exprog.honeyweb.utils;

import java.io.Serializable;

import javax.faces.convert.Converter;

import hu.exprog.honeyweb.front.annotations.FieldEntitySpecificRightsInfo;

public class FieldModel implements Serializable {

	private static final long serialVersionUID = 4063465058747639784L;

	private String label;
	private String propertyName;
	private Object value;
	private boolean required;	
	private Converter converter;
	private String format;
	private FieldRights rights;
	private FieldEntitySpecificRightsInfo fieldEntitySpecificRightsInfo;

	public FieldModel(String propertyName, String label, Converter converter, String format, boolean required) {
		this.propertyName = propertyName;
		this.label = label;
		this.required = required;
		this.converter = converter;
		this.format = format;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public FieldRights getRights() {
		return rights;
	}

	public void setRights(FieldRights rights) {
		this.rights = rights;
	}

	public FieldEntitySpecificRightsInfo getFieldEntitySpecificRightsInfo() {
		return fieldEntitySpecificRightsInfo;
	}

	public void setFieldEntitySpecificRightsInfo(FieldEntitySpecificRightsInfo fieldEntitySpecificRightsInfo) {
		this.fieldEntitySpecificRightsInfo = fieldEntitySpecificRightsInfo;
	}
	
}
