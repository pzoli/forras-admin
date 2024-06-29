package hu.exprog.honeyweb.utils;

import java.io.Serializable;

public class LookupColumnModel extends ColumnModel implements Serializable {

	private static final long serialVersionUID = -8635286932374945640L;

	private String lookupLabelfield;
	private String filterFunction;

	public LookupColumnModel(String header, String property, String sortField, String filterField, String lookupLabelfield, String filterFunction, String format) {
		super(header, property, sortField, filterField, format);
		this.setLookupLabelfield(lookupLabelfield);
		this.setFilterFunction(filterFunction);
	}

	public String getLookupLabelfield() {
		return lookupLabelfield;
	}

	public void setLookupLabelfield(String lookupLabelfield) {
		this.lookupLabelfield = lookupLabelfield;
	}

	public String getFilterFunction() {
		return filterFunction;
	}

	public void setFilterFunction(String filterFunction) {
		this.filterFunction = filterFunction;
	}
	
	
}
