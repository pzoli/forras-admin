package hu.infokristaly.utils;

public class LookupFieldModel extends FieldModel {

	private static final long serialVersionUID = -4236529135351789651L;

	public LookupFieldModel(String propertyName, String label, boolean required) {
		super(propertyName, label, required);
	}

	private String lookupLabelfield;
	private String lookupKeyfield;
	private String detailDialogFile;

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
}
