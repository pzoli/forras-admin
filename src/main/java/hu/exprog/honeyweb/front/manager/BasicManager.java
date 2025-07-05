package hu.exprog.honeyweb.front.manager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.EJBTransactionRolledbackException;
import javax.el.ELException;
import javax.faces.application.FacesMessage;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.extensions.component.dynaform.DynaForm;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;
import org.primefaces.extensions.util.visitcallback.ExecutableVisitCallback;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import hu.exprog.honeyweb.front.annotations.DynamicMirror;
import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.FieldEntitySpecificRightsInfo;
import hu.exprog.honeyweb.front.annotations.FieldRightsInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;
import hu.exprog.honeyweb.front.annotations.QueryFieldInfo;
import hu.exprog.honeyweb.front.annotations.QueryInfo;
import hu.exprog.honeyweb.front.controller.FieldRightsInputExecutor;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.exprog.honeyweb.utils.ColumnModel;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.FieldRights;
import hu.exprog.honeyweb.utils.LookupColumnModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;
import hu.exprog.honeyweb.utils.StringTools;

public abstract class BasicManager<T> {

	protected Optional<T> current;

	protected List<ColumnModel> columns;

	protected LazyDataModel<T> lazyDataModel;

	protected DynaFormModel formModel;

	protected String dialogMode;

	protected String formName = ":mainForm:dynaForm";

	protected String tableName = ":tableForm:itemsTable";

	protected List<FieldModel> properties;

	protected List<T> selectedItems = new ArrayList<T>();

	protected String VALIDATION_FAULT = "validationFailed";

	private String selectMode = "multiple";

	private Locale initLocale;

	private Map<String, Object> staticFilters = new HashMap<String, Object>();

	private boolean selectEnabled = true;

	public void initModel() {
		columns = new ArrayList<ColumnModel>();
		formModel = new DynaFormModel();
		initLocale = getLocale();
		Class<?> clazz = getDomainClass();
		prepareModel(clazz, columns, formModel, initLocale, null);
	}

	public void initModel(String queryInfoName) throws Exception {
		columns = new ArrayList<ColumnModel>();
		formModel = new DynaFormModel();
		initLocale = getLocale();
		Class<?> clazz = getDomainClass();
		QueryInfo queryInfo = getQueryInfo(clazz, queryInfoName);
		if (queryInfoName != null && queryInfo == null) {
			throw new Exception("QueryInfo not found");
		}
		prepareModel(clazz, columns, formModel, initLocale, queryInfo);
	}

	private QueryInfo getQueryInfo(Class<?> clazz, String queryInfoName) {
		QueryInfo result = null;
		if (clazz.isAnnotationPresent(DynamicMirror.class)) {
			DynamicMirror mirror = clazz.getAnnotation(DynamicMirror.class);
			QueryInfo[] queryes = mirror.queryInfoArray();
			for (QueryInfo queryInfo : queryes) {
				if (queryInfo.queryName().equals(queryInfoName)) {
					result = queryInfo;
					break;
				}
			}
		}
		return result;
	}

	private void entitySpecificRightsSetup(Field field, FieldModel model) {
		if (field.isAnnotationPresent(FieldEntitySpecificRightsInfo.class)) {
			FieldEntitySpecificRightsInfo fieldEntitySpecificRightsInfo = field.getAnnotation(FieldEntitySpecificRightsInfo.class);
			model.setFieldEntitySpecificRightsInfo(fieldEntitySpecificRightsInfo);
		}
	}

	public void prepareModel(Class<?> clazz, List<ColumnModel> initialColumns, DynaFormModel initialFormModel, Locale currentLocale, QueryInfo queryInfo) {
		FacesContext.getCurrentInstance().getViewRoot().setLocale(getLocale());
		if (clazz != null) {
			DynaFormRow row = initialFormModel.createRegularRow();
			do {
				List<SimpleEntry<Integer, Field>> fieldOrderMap = new ArrayList<SimpleEntry<Integer, Field>>();
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(EntityFieldInfo.class)) {
						if (queryInfo == null || queryFiledInfo(field, queryInfo).isPresent()) {
							EntityFieldInfo entityInfo = field.getAnnotation(EntityFieldInfo.class);
							fieldOrderMap.add(new SimpleEntry<Integer, Field>(entityInfo.weight(), field));
						}
					}
				}
				fields = fieldOrderMap.stream().sorted(new Comparator<SimpleEntry<Integer, Field>>() {
					public int compare(SimpleEntry<Integer, Field> c1, SimpleEntry<Integer, Field> c2) {
						return c1.getKey().compareTo(c2.getKey());
					}
				}).map(m -> m.getValue()).collect(Collectors.toList()).toArray(new Field[] {});
				for (Field field : fields) {
					if (field.isAnnotationPresent(EntityFieldInfo.class)) {
						EntityFieldInfo entityInfo = field.getAnnotation(EntityFieldInfo.class);
						String i18nLabel = evalELToString(entityInfo.info());
						
						Optional<QueryFieldInfo> queryFieldInfo = queryInfo != null ? queryFiledInfo(field, queryInfo) : Optional.empty();
						if (entityInfo.listable() || (queryFieldInfo.isPresent() && queryFieldInfo.get().listable())) {
							if (field.isAnnotationPresent(LookupFieldInfo.class)) {
								LookupFieldInfo lookupInfo = field.getAnnotation(LookupFieldInfo.class);
								String sortField = StringTools.isNotBlank(lookupInfo.sortField()) ? lookupInfo.sortField() : field.getName();
								String filterField = StringTools.isNotBlank(lookupInfo.filterField()) ? lookupInfo.filterField() : field.getName();
								Converter conv = getConverter(lookupInfo.converter());
								initialColumns.add(new LookupColumnModel(i18nLabel, field.getName(), sortField, filterField, lookupInfo.labelField(), lookupInfo.filterFunction(), entityInfo.format(), conv));
							} else {
								Converter conv = getConverter(entityInfo.converter());
								initialColumns.add(new ColumnModel(i18nLabel, field.getName(), field.getName(), field.getName(), entityInfo.format(), conv));
							}
						}
						row = initialFormModel.createRegularRow();
						FieldModel model = null;
						FieldRights rights = null;
						FieldRights queryFieldInfoRights = null;
						if (queryFieldInfo.isPresent()) {
							queryFieldInfoRights = new FieldRights(queryFieldInfo.get());
						}
						if (field.isAnnotationPresent(FieldRightsInfo.class)) {
							FieldRightsInfo FieldRightsInfo = field.getAnnotation(FieldRightsInfo.class);
							Boolean disabled = evalELToBoolean(FieldRightsInfo.disabled());
							Boolean readOnly = evalELToBoolean(FieldRightsInfo.readOnly());
							Boolean visible = evalELToBoolean(FieldRightsInfo.visible());
							Boolean admin = evalELToBoolean(FieldRightsInfo.admin());
							rights = new FieldRights(disabled, readOnly, visible, admin);
						} else {
							rights = new FieldRights(false, false, true, true);
						}
						if (field.isAnnotationPresent(LookupFieldInfo.class)) {
							LookupFieldInfo lookupFieldInfo = field.getAnnotation(LookupFieldInfo.class);
							Converter conv = getConverter(lookupFieldInfo.converter());
							model = new LookupFieldModel(field.getName(), i18nLabel, conv, lookupFieldInfo.filterFunction(), lookupFieldInfo.format(), entityInfo.required());
							((LookupFieldModel) model).setLookupKeyfield(lookupFieldInfo.keyField());
							((LookupFieldModel) model).setLookupLabelfield(lookupFieldInfo.labelField());
							((LookupFieldModel) model).setDetailDialogFile(lookupFieldInfo.detailDialogFile());
							((LookupFieldModel) model).setRights(queryFieldInfoRights != null ? queryFieldInfoRights : rights);
						} else if (entityInfo.editor() != null && !entityInfo.editor().trim().isEmpty()) {
							Converter conv = getConverter(entityInfo.converter());
							model = new FieldModel(field.getName(), i18nLabel, conv, entityInfo.format(), entityInfo.required());
							((FieldModel) model).setRights(queryFieldInfoRights != null ? queryFieldInfoRights : rights);
						}
						entitySpecificRightsSetup(field, model);
						if (model != null) {
							DynaFormLabel label = row.addLabel(i18nLabel);
							DynaFormControl control = row.addControl(model, entityInfo.editor());
							control.setKey(model.getPropertyName());
							label.setForControl(control);
						}
					}
				}
				clazz = clazz.getSuperclass();
			} while (clazz != null);
		}
	}

	private Optional<QueryFieldInfo> queryFiledInfo(Field field, QueryInfo queryInfo) {
		Optional<QueryFieldInfo> result = Arrays.asList(queryInfo.fields()).stream().filter(fieldInfo -> fieldInfo.fieldName().equalsIgnoreCase(field.getName())).findFirst();
		return result;
	}

	public Map<String, EntityFieldInfo> getEntityInfoMap(Class<?> domainClass) {
		Map<String, EntityFieldInfo> result = new HashMap<String, EntityFieldInfo>();
		Field[] fields = domainClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(EntityFieldInfo.class)) {
				EntityFieldInfo entityInfo = field.getAnnotation(EntityFieldInfo.class);
				result.put(field.getName(), entityInfo);
			}
		}
		return result;
	}

	public Map<String, EntityFieldInfo> getEntityInfoMap() {
		Map<String, EntityFieldInfo> result = getEntityInfoMap(getDomainClass());
		return result;
	}

	public Map<String, LookupFieldInfo> getLookupFieldInfoMap() {
		Map<String, LookupFieldInfo> result = getLookupFieldInfoMap(getDomainClass());
		return result;
	}

	public Map<String, LookupFieldInfo> getLookupFieldInfoMap(Class<?> domainClass) {
		Map<String, LookupFieldInfo> result = new HashMap<String, LookupFieldInfo>();
		Field[] fields = domainClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(LookupFieldInfo.class)) {
				LookupFieldInfo lookupFieldInfo = field.getAnnotation(LookupFieldInfo.class);
				result.put(field.getName(), lookupFieldInfo);
			}
		}
		return result;
	}

	protected Boolean evalELToBoolean(String expression) {
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

	protected String evalELToString(String expression) {
		String result = null;
		if (expression != null && !expression.isEmpty()) {
			FacesContext context = FacesContext.getCurrentInstance();
			try {
				result = context.getApplication().evaluateExpressionGet(context, expression, String.class);
			} catch (ELException ex) {

			}
		}
		return result;
	}

	protected Converter getConverter(String converterName) {
		if (converterName == null || converterName.isEmpty()) {
			return null;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		Converter result = null;
		try {
			result = context.getApplication().createConverter(converterName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, Object> getStaticFilters() {
		return staticFilters;
	}

	public LazyDataModel<T> getLazyDataModel() throws ActionAccessDeniedException {
		checkListRight();
		if (initLocale != getLocale()) {
			initModel();
		}
		if (lazyDataModel == null) {
			lazyDataModel = new LazyDataModel<T>() {

				private static final long serialVersionUID = 1678907483750487431L;

				private Map<String, Object> actualfilters;

				private String actualOrderField;
				private SortOrder actualSortOrder;
				private Integer count;

				@SuppressWarnings("unchecked")
				@Override
				public T getRowData(String rowKey) {
					long primaryKey = Long.valueOf(rowKey);

					T bean = null;
					try {
						bean = (T) getDomainClass().newInstance();
						Field f = null;
						Class<?> clazz = bean.getClass();
						do {
							try {
								f = clazz.getDeclaredField("id");
							} catch (NoSuchFieldException e) {
								clazz = clazz.getSuperclass();
							}
						} while ((f == null) && (clazz != null));
						f.setAccessible(true);
						f.set(bean, primaryKey);
						bean = getService().find((T) bean);
					} catch (InstantiationException | IllegalAccessException | SecurityException e) {
						e.printStackTrace();
					}
					return bean;
				}

				@Override
				public Object getRowKey(T bean) {
					Object result = null;
					try {
						result = BeanUtils.getProperty(bean, "id");
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
					return result;
				}

				@Override
				public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
					this.setPageSize(pageSize);
					this.count = null;
					if (staticFilters.size() > 0) {
						staticFilters.forEach((k, v) -> {
							if (!filters.containsKey(k)) {
								filters.put(k, v);
							}
						});
					}
					this.actualfilters = filters;
					if (sortField != null) {
						this.actualOrderField = sortField;
					}

					if (sortOrder != null) {
						this.actualSortOrder = sortOrder;
					}

					List<T> result = (List<T>) getService().findRange(first, pageSize, this.actualOrderField, hu.exprog.honeyweb.middle.services.SortOrder.valueOf(this.actualSortOrder.name()), filters);
					getLogger().log(Level.INFO, "[" + this.getClass().getName() + "] load finished.");
					return result;
				}

				@Override
				public int getRowCount() {
					if (count == null) {
						count = getService().count(actualfilters);
					}
					return count;
				}

			};
		}
		return lazyDataModel;
	}

	public List<T> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<T> selectedUsers) {
		this.selectedItems = selectedUsers;
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}

	public List<FieldModel> getProperties() {
		if (formModel == null) {
			return null;
		}

		List<FieldModel> properties = new ArrayList<FieldModel>();
		for (DynaFormControl dynaFormControl : formModel.getControls()) {
			if (!((FieldModel) dynaFormControl.getData()).getPropertyName().startsWith("-")) {
				properties.add((FieldModel) dynaFormControl.getData());
			}
		}

		return properties;
	}

	public void clearProperties() {
		if (formModel != null) {
			for (DynaFormControl dynaFormControl : formModel.getControls()) {
				((FieldModel) dynaFormControl.getData()).setValue(null);
			}
		}
	}

	protected void setDetailFieldValue(LookupFieldModel d, Object selected) {
		d.setValue(selected);
	}

	public void setControlData(T selected) {
		clearProperties();
		current = Optional.of(selected);
		Map<String, FieldModel> fieldModelMap = new HashMap<String, FieldModel>();
		for (DynaFormControl f : formModel.getControls()) {
			if (!((FieldModel) f.getData()).getPropertyName().startsWith("-")) {
				fieldModelMap.put(((FieldModel) f.getData()).getPropertyName(), (FieldModel) f.getData());
				try {
					Class<?> clazz = selected.getClass();
					if (f.getData() instanceof LookupFieldModel) {
						do {
							try {
								Field field = selected.getClass().getDeclaredField(((LookupFieldModel) f.getData()).getPropertyName());
								field.setAccessible(true);
								Object value = field.get(selected);
								if (value != null) {
									if (!(value instanceof List)) {
										Object keyValue = BeanUtils.getProperty(value, ((LookupFieldModel) f.getData()).getLookupKeyfield());
										setDetailFieldValue((LookupFieldModel) f.getData(), keyValue);
									} else {
										// TODO
										setDetailFieldValue((LookupFieldModel) f.getData(), value);
									}
								}
							} catch (NoSuchFieldException e) {

							}
							clazz = clazz.getSuperclass();
						} while (clazz != null);
					} else if (f.getData() instanceof FieldModel) {
						do {
							try {
								Field field = clazz.getDeclaredField(((FieldModel) f.getData()).getPropertyName());
								field.setAccessible(true);
								((FieldModel) f.getData()).setValue(field.get(selected));
							} catch (NoSuchFieldException e) {

							}
							clazz = clazz.getSuperclass();
						} while (clazz != null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		setFormRights(formName, fieldModelMap);
	}

	public static final Set<VisitHint> VISIT_HINTS = EnumSet.of(VisitHint.SKIP_UNRENDERED);

	public List<FieldModel> getFieldModel() {
		List<FieldModel> result = new LinkedList<FieldModel>();
		for (DynaFormControl f : formModel.getControls()) {
			if (!((FieldModel) f.getData()).getPropertyName().startsWith("-")) {
				result.add((FieldModel) f.getData());
			}
		}
		return result;
	}

	public void setFormRights(String formName, Map<String, FieldModel> fieldModelMap) {
		FacesContext fc = FacesContext.getCurrentInstance();
		FieldRightsInputExecutor visitTaskExecutor = new FieldRightsInputExecutor(fieldModelMap);
		ExecutableVisitCallback visitCallback = new ExecutableVisitCallback(visitTaskExecutor);
		DynaForm dynaForm = (DynaForm) fc.getViewRoot().findComponent(formName);
		if (dynaForm != null) {
			dynaForm.visitTree(VisitContext.createVisitContext(fc, null, VISIT_HINTS), visitCallback);
		}
	}

	public void setCurrentBeanProperties() {
		properties = getProperties();
		properties.forEach(f -> {
			try {
				Field field = current.get().getClass().getDeclaredField(f.getPropertyName());
				boolean preProcess = field.isAnnotationPresent(EntityFieldInfo.class) && field.getAnnotation(EntityFieldInfo.class).preProcess();
				if (f.getValue() != null) {
					if (f instanceof LookupFieldModel) {
						Object value = getDetailFieldValue((LookupFieldModel) f);
						if (preProcess) {
							value = preProcess(f, value);
						}
						setProperty(current.get(), f.getPropertyName(), value);
					} else if (f instanceof FieldModel) {
						setProperty(current.get(), f.getPropertyName(), f.getValue());
					}
				} else {
					setPropertyNull(f);
				}
				boolean postProcess = field.isAnnotationPresent(EntityFieldInfo.class) && field.getAnnotation(EntityFieldInfo.class).postProcess();
				if (postProcess) {
					Object value = postProcess(f, f.getValue());
					setProperty(current.get(), f.getPropertyName(), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void setProperty(Object bean, String fieldName, Object value) throws SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = null;
		Class<? extends Object> beanClazz = bean.getClass();
		while (f == null && !(beanClazz.equals(Object.class))) {
			try {
				f = current.get().getClass().getDeclaredField(fieldName);
			} catch (NoSuchFieldException ex) {

			}
		}
		if (f != null) {
			f.setAccessible(true);
			if (value instanceof String && f.getGenericType().equals(Integer.class)) {
				f.set(bean, Integer.valueOf((String) value));
			} else if (value instanceof String && f.getGenericType().equals(Short.class)) {
				f.set(bean, Short.valueOf((String) value));
			} else if (value instanceof String && f.getGenericType().equals(Byte.class)) {
				f.set(bean, Byte.valueOf((String) value));
			} else if (value instanceof String && f.getGenericType().equals(Float.class)) {
				f.set(bean, Float.valueOf((String) value));
			} else {
				f.set(bean, value);
			}
		}
	}

	public void setPropertyNull(FieldModel f) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class clazz = current.get().getClass();
		Field fProp = clazz.getDeclaredField(f.getPropertyName());
		fProp.setAccessible(true);
		fProp.set(current.get(), null);
	}

	public void deleteSelected() throws ActionAccessDeniedException, PersistenceException {
		checkDeleteRight();
		try {
			if (getSelectMode().equals("single")) {
				if (current != null && current.isPresent()) {
					try {
						if (checkDeleteRight(current.get())) {
							getService().remove(current.get());
						}
						current = Optional.empty();
					} catch (ActionAccessDeniedException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			} else {
				if (selectedItems != null) {
					for (T c : selectedItems) {
						try {
							if (checkDeleteRight(c)) {
								getService().remove(c);
							}
						} catch (ActionAccessDeniedException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
							e.printStackTrace();
						}
					}
					selectedItems.clear();
				}
			}
		} catch (EJBTransactionRolledbackException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(evalELToString("#{msg['cascade-delete-needed']}")));
		}
	}

	public void showDialog(String dialogFile) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("draggable", true);
		options.put("resizable", true);
		options.put("position", "top");
		options.put("width", "90%");
		// options.put("height","100%");

		options.put("contentWidth", "100%");
		String height = "90vh";// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("widthSize");
		options.put("contentHeight", height);
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		List<String> values = new ArrayList<String>();
		values.add("single");
		params.put("selectMode", values);
		values = new ArrayList<String>();
		values.add("selectOne");
		params.put("dialogMode", values);
		PrimeFaces.current().dialog().openDynamic(dialogFile, options, params); // PF6.2-től
		// RequestContext.getCurrentInstance().openDialog(dialogFile, options,
		// params); //PF6.1-ig
	}

	public void setCurrent(T current) {
		this.current = current != null ? Optional.of(current) : null;
	}

	public T getCurrent() {
		return current != null && current.isPresent() ? current.get() : null;
	}

	public void setFormModel(DynaFormModel formModel) {
		this.formModel = formModel;
	}

	public DynaFormModel getFormModel() {
		return formModel;
	}

	public boolean renderLookup(String className) {
		return className.equals("LookupColumnModel");
	}

	public boolean renderSimple(String className) {
		return className.equals("ColumnModel");
	}

	public String getSelectMode() {
		return selectMode;
	}

	public void setSelectMode(String selectMode) {
		this.selectMode = selectMode;
	}

	public String getSelectionVar() {
		return getSelectMode().equals("multiple") ? "selectedItems" : "current";
	}

	public void selectFromDialog() {
		if ("multiple".equals(getSelectMode())) {
			if (selectedItems.size() > 0) {
				setCurrent(selectedItems.get(0));
			} else {
				current = Optional.empty();
			}
		}
		if (current != null && current.isPresent()) {
			PrimeFaces.current().dialog().closeDynamic(current.get());
			// RequestContext.getCurrentInstance().closeDialog(current.get());
		}
	}

	protected Class<?> getDomainClass() {
		Class<?> result = null;
		if (current != null && current.get() != null) {
			result = current.get().getClass();
		} else {
			result = getService().getDomainClass();
		}
		return result;
	}

	public String getDialogMode() {
		return dialogMode;
	}

	public void setDialogMode(String dialogMode) {
		this.dialogMode = dialogMode;
	}

	public boolean openedInDialogMode() {
		return "selectOne".equalsIgnoreCase(dialogMode);
	}

	public boolean getSelectEnabled() {
		return selectEnabled;
	}

	protected abstract Logger getLogger();

	protected abstract BasicService<T> getService();

	protected abstract Object getDetailFieldValue(LookupFieldModel model);

	protected abstract Locale getLocale();

	public abstract boolean checkListRight() throws ActionAccessDeniedException;

	public abstract boolean checkSaveRight() throws ActionAccessDeniedException;

	public abstract boolean checkDeleteRight() throws ActionAccessDeniedException;

	public abstract boolean checkDeleteRight(T entity) throws ActionAccessDeniedException;

	public abstract boolean checkEditableRights(T entity) throws ActionAccessDeniedException;
	
	public abstract boolean checkDetailsInTable();

	public abstract Object postProcess(FieldModel field, Object value);

	public abstract Object preProcess(FieldModel field, Object value);

}
