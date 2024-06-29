package hu.exprog.honeyweb.utils;

import hu.exprog.honeyweb.front.annotations.QueryFieldInfo;

public class FieldRights {
	private Boolean disabled;
	private Boolean readOnly;
	private Boolean visible;
	private Boolean admin;
	
	public FieldRights(Boolean disabled, Boolean readOnly, Boolean visible, Boolean admin) {
		this.disabled = disabled;
		this.readOnly = readOnly;
		this.visible = visible;
		this.admin = admin;
	}
	
	public FieldRights(QueryFieldInfo queryFieldInfo) {
		this.disabled = queryFieldInfo.disabled();
		this.readOnly = queryFieldInfo.readOnly();
		this.visible = queryFieldInfo.visible();
		this.admin = queryFieldInfo.admin(); 
	}
	
	public Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	public Boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public Boolean getAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
}
