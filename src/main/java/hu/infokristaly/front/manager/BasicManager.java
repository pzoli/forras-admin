package hu.infokristaly.front.manager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.PrimeFaces.Dialog;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import hu.infokristaly.front.annotations.EntityInfo;
import hu.infokristaly.front.annotations.LookupFieldInfo;
import hu.infokristaly.front.exceptions.ActionAccessDeniedException;
import hu.infokristaly.middle.service.BasicService;
import hu.infokristaly.utils.I18n;
import hu.infokristaly.utils.ColumnModel;
import hu.infokristaly.utils.FieldModel;
import hu.infokristaly.utils.LookupColumnModel;
import hu.infokristaly.utils.LookupFieldModel;

public abstract class BasicManager<T> {

	private Class<T> clazz;
	
    protected Optional<T> current;

    protected List<ColumnModel> columns;

    protected LazyDataModel<T> lazyDataModel;

    protected DynaFormModel formModel;

    protected String dialogMode;

    protected List<FieldModel> properties;

    protected List<T> selectedItems = new ArrayList<T>();

    private String selectMode = "multiple";

    private Locale initLocale;
    
    public void initModel() {
        columns = new ArrayList<ColumnModel>();
        formModel = new DynaFormModel();
        DynaFormRow row = formModel.createRegularRow();
        DynaFormControl hidden = row.addControl(new FieldModel("-", "hidden", false), "hidden");
        initLocale = getLocale();
        Class<?> clazz = getDomainClass();
        do {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(EntityInfo.class)) {
                    EntityInfo entityInfo = field.getAnnotation(EntityInfo.class);
                    String i18nLabel = entityInfo.info();
                    if (i18nLabel.startsWith("#{'")) {
                        String key = i18nLabel.substring(3, i18nLabel.indexOf("'", 3));
                        i18nLabel = I18n.getString(key, initLocale);
                    }
                    if (field.isAnnotationPresent(LookupFieldInfo.class)) {
                        LookupFieldInfo lookupInfo = field.getAnnotation(LookupFieldInfo.class);
                        columns.add(new LookupColumnModel(i18nLabel, field.getName(), lookupInfo.labelField()));
                    } else {
                        columns.add(new ColumnModel(i18nLabel, field.getName()));
                    }
                    row = formModel.createRegularRow();
                    DynaFormLabel label = row.addLabel(i18nLabel);
                    FieldModel model;
                    if (field.isAnnotationPresent(LookupFieldInfo.class)) {
                        LookupFieldInfo lookupFieldInfo = field.getAnnotation(LookupFieldInfo.class);
                        model = new LookupFieldModel(field.getName(), i18nLabel, entityInfo.required());
                        ((LookupFieldModel) model).setLookupKeyfield(lookupFieldInfo.keyField());
                        ((LookupFieldModel) model).setLookupLabelfield(lookupFieldInfo.labelField());
                        ((LookupFieldModel) model).setDetailDialogFile(lookupFieldInfo.detailDialogFile());
                    } else {
                        model = new FieldModel(field.getName(), i18nLabel, entityInfo.required());
                    }

                    DynaFormControl control = row.addControl(model, entityInfo.editor());
                    label.setForControl(control);
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
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
                    this.actualfilters = filters;
                    if (sortField != null) {
                        this.actualOrderField = sortField;
                    }

                    if (sortOrder != null) {
                        this.actualSortOrder = sortOrder;
                    }

                    List<T> result = (List<T>) getService().findRange(first, pageSize, this.actualOrderField, hu.infokristaly.middle.service.SortOrder.valueOf(this.actualSortOrder.name()), filters);
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
            if (!"-".equals(((FieldModel) dynaFormControl.getData()).getPropertyName())) {
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
        for (DynaFormControl f : formModel.getControls()) {
            if (!((FieldModel) f.getData()).getPropertyName().equals("-")) {
                try {
                    Class<?> clazz = selected.getClass();
                    if (f.getData() instanceof LookupFieldModel) {
                        do {
                            try {
                                Field field = selected.getClass().getDeclaredField(((LookupFieldModel) f.getData()).getPropertyName());
                                field.setAccessible(true);
                                Object value = field.get(selected);
                                if (value != null) {
                                    Object keyValue = BeanUtils.getProperty(value, ((LookupFieldModel) f.getData()).getLookupKeyfield());
                                    setDetailFieldValue((LookupFieldModel) f.getData(), keyValue);
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
    }

    public void setCurrentBeanProperties() {
        properties = getProperties();
        properties.forEach(f -> {
            try {
                if (f instanceof LookupFieldModel) {
                    Object value = getDetailFieldValue((LookupFieldModel) f);
                    BeanUtils.setProperty(current.get(), f.getPropertyName(), value);
                } else if (f instanceof FieldModel) {
                    BeanUtils.setProperty(current.get(), f.getPropertyName(), f.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteSelected() throws ActionAccessDeniedException, PersistenceException {
        checkDeleteRight();
        if (getSelectMode().equals("single")) {
            if (current.get() != null) {
                try {
                    getService().remove(current.get());
                    current = Optional.empty();
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (selectedItems != null) {
                for (T c : selectedItems) {
                    try {
                        getService().remove(c);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                selectedItems.clear();
            }
        }
    }

    public void showDialog(String dialogFile) {
    	if (dialogFile != null && !dialogFile.isEmpty()) {
	        Map<String, Object> options = new HashMap<String, Object>();
	        options.put("modal", true);
	        options.put("draggable", false);
	        options.put("resizable", false);
	        options.put("contentHeight", 320);
	        Map<String, List<String>> params = new HashMap<String, List<String>>();
	        List<String> values = new ArrayList<String>();
	        values.add("single");
	        params.put("selectMode", values);
	        values = new ArrayList<String>();
	        values.add("selectOne");
	        params.put("dialogMode", values);
	        PrimeFaces.current().dialog().openDynamic(dialogFile, options, params);
    	}
    }

    public void setCurrent(T current) {
        this.current = Optional.of(current);
    }

    public T getCurrent() {
        return current.isPresent() ? current.get() : null;
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
        if (current.isPresent()) {
        	PrimeFaces.current().dialog().closeDynamic(current.get());
        }
    }
    
    public T getInstanceOfT(Class<T> aClass) throws InstantiationException, IllegalAccessException {
        return aClass.newInstance();
     }
    
    protected Class<?> getDomainClass() {
    	Class<?> result;
    	if (current.isPresent()) {
    		result= current.get().getClass();
    	} else {
    		result = clazz;
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

    protected abstract Logger getLogger();
    
    protected abstract BasicService<T> getService();

    protected abstract Object getDetailFieldValue(LookupFieldModel model);

    protected abstract Locale getLocale();

    public abstract boolean checkListRight() throws ActionAccessDeniedException;

    public abstract boolean checkSaveRight() throws ActionAccessDeniedException;

    public abstract boolean checkDeleteRight() throws ActionAccessDeniedException;

}
