package hu.exprog.beecomposit.middle.service;

import javax.ejb.Stateless;

@Stateless
public class AppProperties {

	private Long SystemId = 1L;
	private String projectStage = "Development";

	public Long getSystemId() {
		return SystemId;
	}

	public void setSystemId(Long systemId) {
		SystemId = systemId;
	}

	public String getProjectStage() {
		return projectStage;
	}

	public void setProjectStage(String projectStage) {
		this.projectStage = projectStage;
	}
	
}
