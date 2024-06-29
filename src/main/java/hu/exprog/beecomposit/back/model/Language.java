package hu.exprog.beecomposit.back.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;

@Entity
public class Language {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;    
    @EntityFieldInfo(info="#{msg['locale-code']}", weight=1, required=true, editor="txt")
    private String localeCode;
    @EntityFieldInfo(info="#{msg['language']}", weight=2, required=true, editor="txt")
    private String language;
	@Version
	private Long version;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLocaleCode() {
		return localeCode;
	}
	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((localeCode == null) ? 0 : localeCode.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Language other = (Language) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (localeCode == null) {
			if (other.localeCode != null)
				return false;
		} else if (!localeCode.equals(other.localeCode))
			return false;
		return true;
	}    
    
}
