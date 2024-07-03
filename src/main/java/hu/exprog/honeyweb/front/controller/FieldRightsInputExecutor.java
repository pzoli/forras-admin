package hu.exprog.honeyweb.front.controller;

import java.util.Map;

import javax.el.ELException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import org.primefaces.extensions.util.visitcallback.VisitTaskExecutor;

import hu.exprog.honeyweb.front.annotations.FieldEntitySpecificRightsInfo;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.FieldRights;

public class FieldRightsInputExecutor implements VisitTaskExecutor {

	private Map<String, FieldModel> fieldModelMap;

	public FieldRightsInputExecutor(Map<String, FieldModel> fieldModelMap) {
		this.fieldModelMap = fieldModelMap;
	}

	@Override
	public VisitResult execute(UIComponent component) {
		VisitResult result = VisitResult.ACCEPT;
		UIInput input = (UIInput) component;
		String propertyName = (String) input.getAttributes().get("propertyName");
		FieldModel fieldModel = fieldModelMap.get(propertyName);
		FieldEntitySpecificRightsInfo entityRights = fieldModel.getFieldEntitySpecificRightsInfo();
		FieldRights fieldRights = fieldModel.getRights();
		if (fieldRights != null) {
			if (fieldRights.getDisabled() != null && fieldRights.getAdmin() == null) {
				Boolean disabled = fieldRights.getDisabled();
				component.getAttributes().put("disabled", disabled);
			}
			if (fieldRights.getReadOnly() != null) {
				Boolean readOnly = fieldRights.getReadOnly();
				component.getAttributes().put("readonly", readOnly);
			}
		}

		if (entityRights != null) {
			if (entityRights.disabled() != null) {
				Boolean disabled = evalELToBoolean(entityRights.disabled());
				if (disabled != null) {
					component.getAttributes().put("disabled", disabled);
				}
			}
			if (entityRights.readOnly() != null) {
				Boolean readOnly = evalELToBoolean(entityRights.readOnly());
				if (readOnly != null) {
					component.getAttributes().put("readonly", readOnly);
				}
			}
		}
		return result;
	}

	private Boolean evalELToBoolean(String expression) {
		Boolean result = null;
		if (expression != null && !expression.isEmpty()) {
			FacesContext context = FacesContext.getCurrentInstance();
			try {
				result = context.getApplication().evaluateExpressionGet(context, expression, Boolean.class);
			} catch (ELException ex) {

			}
		}
		return result;
	}

	@Override
	public boolean shouldExecute(UIComponent component) {
		boolean result = false;
		if (component instanceof UIInput) {
			UIInput input = (UIInput) component;
			String propertyName = (String) input.getAttributes().get("propertyName");
			if (propertyName != null) {
				FieldModel fieldModel = fieldModelMap.get(propertyName);
				if (fieldModel != null) {
					FieldRights fieldRights = fieldModel.getRights();
					FieldEntitySpecificRightsInfo entityRights = fieldModel.getFieldEntitySpecificRightsInfo();
					if (fieldRights != null || entityRights != null) {
							result = true;
					}
				}
			}
		}
		return result;
	}

}
