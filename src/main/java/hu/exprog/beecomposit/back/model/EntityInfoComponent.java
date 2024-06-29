package hu.exprog.beecomposit.back.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class EntityInfoComponent {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String entityName;
	private String fieldType;
	private String info;
    private String alternateName;
    private int weight;
    private String editor;    	
    private boolean required;
    private boolean listable;
    private Class<?> expected;
    private boolean preProcess;
	private boolean postProcess;
	private String converter;
	private String format;
	@Version
	private Long version;

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getAlternateName() {
		return alternateName;
	}
	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isListable() {
		return listable;
	}
	public void setListable(boolean listable) {
		this.listable = listable;
	}
	public Class<?> getExpected() {
		return expected;
	}
	public void setExpected(Class<?> expected) {
		this.expected = expected;
	}
	public boolean isPreProcess() {
		return preProcess;
	}
	public void setPreProcess(boolean preProcess) {
		this.preProcess = preProcess;
	}
	public boolean isPostProcess() {
		return postProcess;
	}
	public void setPostProcess(boolean postProcess) {
		this.postProcess = postProcess;
	}
	public String getConverter() {
		return converter;
	}
	public void setConverter(String converter) {
		this.converter = converter;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

}
