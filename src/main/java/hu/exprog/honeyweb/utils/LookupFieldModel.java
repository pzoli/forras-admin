package hu.exprog.honeyweb.utils;

import javax.faces.convert.Converter;

public class LookupFieldModel extends FieldModel {

	private static final long serialVersionUID = -4236529135351789651L;

	private String lookupLabelfield;
	private String lookupKeyfield;
	private String detailDialogFile;
	private String filterFunction;

	public LookupFieldModel(String propertyName, String label, Converter converter, String filterFunction, String format, boolean required) {
		super(propertyName, label, converter, format, required);
		this.filterFunction = filterFunction;
	}

	public String getLookupLabelfield() {
		return lookupLabelfield;
	}

	public void setLookupLabelfield(String lookupLabelfield) {
		this.lookupLabelfield = lookupLabelfield;
	}

	public String getLookupKeyfield() {
		return lookupKeyfield;
	}

	public void setLookupKeyfield(String lookupKeyfield) {
		this.lookupKeyfield = lookupKeyfield;
	}

	public void setDetailDialogFile(String detailDialogFile) {
		this.detailDialogFile = detailDialogFile;
	}

	public String getDetailDialogFile() {
		return detailDialogFile;
	}

	public String getFilterFunction() {
		return filterFunction;
	}

	public void setFilterFunction(String filterFunction) {
		this.filterFunction = filterFunction;
	}
		
}
