package hu.infokristaly.utils;

import java.io.Serializable;

public class FieldModel implements Serializable {

	private static final long serialVersionUID = 4063465058747639784L;

	private String label;
	private String propertyName;
	private Object value;
	private String detailDialogFile;
	private boolean required;	

	public FieldModel(String propertyName, String label, boolean required) {
		this.propertyName = propertyName;
		this.label = label;
		this.required = required;
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

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

    /**
     * @return the detailDialogFile
     */
    public String getDetailDialogFile() {
        return detailDialogFile;
    }

    /**
     * @param detailDialogFile the detailDialogFile to set
     */
    public void setDetailDialogFile(String detailDialogFile) {
        this.detailDialogFile = detailDialogFile;
    }
}
