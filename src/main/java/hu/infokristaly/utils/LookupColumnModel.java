package hu.infokristaly.utils;

import java.io.Serializable;

public class LookupColumnModel extends ColumnModel implements Serializable {

	private static final long serialVersionUID = -8635286932374945640L;

	private String lookupLabelfield;

	public LookupColumnModel(String header, String property, String lookupLabelfield) {
		super(header, property);
		this.setLookupLabelfield(lookupLabelfield);
	}

	public String getLookupLabelfield() {
		return lookupLabelfield;
	}

	public void setLookupLabelfield(String lookupLabelfield) {
		this.lookupLabelfield = lookupLabelfield;
	}
}
