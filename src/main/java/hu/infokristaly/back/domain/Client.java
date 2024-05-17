package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import hu.infokristaly.back.model.SystemUser;

/**
 * The persistent class for the client database table.
 * 
 */
@Entity
@Cacheable(value=true)
@Table(name="client", indexes= {@Index(name="idx_neve",columnList="neve"), @Index(name="idx_taj", columnList="taj"),@Index(name="idx_active", columnList="active")})
@NamedQuery(name="Client.findAll", query="SELECT c FROM Client c")
public class Client implements Serializable, Comparable<Client> {
    
    private static final long serialVersionUID = 8642042154423794249L;

    private Long id;
	private String nyilvantartasiSzam;
	private String anyjaNeve;	
	private String neve;
	private String szuletesiHely;
	private Date szuletesiIdo;
	private String szuletesiNev;
	private Boolean jovedelemIgazolas;
	private Boolean orvosiJavaslat;
	private Date megszDatum;
	private Date felvetDatum;	
	private String taj;
	private Date kervenyBeerkezesDatuma;
	private Boolean megallapodas;
	private Boolean nyilatkozatalap;
	private Boolean nyilatkozatadat;
	private Date utolsoGondozasiTervDatuma;
	private String megjegyzes;
	private List<Accessible> accessibles = new ArrayList<Accessible>();
	private ClientType clientType;
	private Boolean active;

	private Date createDate;
	private Date modificationDate;
	private Date deletedDate;
	private SystemUser currentManager;

	private SystemUser created_by;
	private SystemUser modified_by;
	private Doctor currentDoctor;
	
	private Long version;

	public Client() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@Column(name="anyja_neve")
	@Basic
	public String getAnyjaNeve() {
		return this.anyjaNeve;
	}

	public void setAnyjaNeve(String anyjaNeve) {
		this.anyjaNeve = anyjaNeve;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="deleted_date")
	public Date getDeletedDate() {
		return this.deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="felvet_datum")
	@NotNull
	public Date getFelvetDatum() {
		return this.felvetDatum;
	}

	public void setFelvetDatum(Date felvetDatum) {
		this.felvetDatum = felvetDatum;
	}


	@Column(name="jovedelmigazolas")
	@Basic
	public Boolean getJovedelemIgazolas() {
		return this.jovedelemIgazolas;
	}

	public void setJovedelemIgazolas(Boolean jovedelmiIgazolas) {
		this.jovedelemIgazolas = jovedelmiIgazolas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="megsz_datum")
	public Date getMegszDatum() {
		return this.megszDatum;
	}

	public void setMegszDatum(Date megszDatum) {
		this.megszDatum = megszDatum;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modification_date")
	public Date getModificationDate() {
		return this.modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}


	@Column(nullable=false)
	@Basic
	@NotBlank	
	public String getNeve() {
		return this.neve;
	}

	public void setNeve(String neve) {
		this.neve = neve;
	}


	@Column(name="nyilvantartasiszam", nullable=false, unique=true)
	@Basic
	@NotBlank
	public String getNyilvantartasiSzam() {
		return this.nyilvantartasiSzam;
	}

	public void setNyilvantartasiSzam(String nyilvantartasiSzam) {
		this.nyilvantartasiSzam = nyilvantartasiSzam;
	}


	@Column(name="orvosijavaslat")
	@Basic
	public Boolean getOrvosiJavaslat() {
		return this.orvosiJavaslat;
	}

	public void setOrvosiJavaslat(Boolean orvosiIratok) {
		this.orvosiJavaslat = orvosiIratok;
	}


	@Column(name="szuletesi_hely")
	@Basic
	public String getSzuletesiHely() {
		return this.szuletesiHely;
	}

	public void setSzuletesiHely(String szuletesiHely) {
		this.szuletesiHely = szuletesiHely;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="szuletesi_ido")
	public Date getSzuletesiIdo() {
		return this.szuletesiIdo;
	}

	public void setSzuletesiIdo(Date szuletesiIdo) {
		this.szuletesiIdo = szuletesiIdo;
	}


	@Column(name="szuletesi_nev")
	@Basic
	public String getSzuletesiNev() {
		return this.szuletesiNev;
	}

	public void setSzuletesiNev(String szuletesiNev) {
		this.szuletesiNev = szuletesiNev;
	}


	@Basic
	@NotBlank
	public String getTaj() {
		return this.taj;
	}

	public void setTaj(String taj) {
		this.taj = taj;
	}

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY ,cascade = {CascadeType.REMOVE,CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name = "accessible_client", joinColumns = { @JoinColumn(name = "client_id", referencedColumnName="id") }, inverseJoinColumns = { @JoinColumn(name = "accessible_id", referencedColumnName="id") })
	public List<Accessible> getAccessibles() {
		return this.accessibles;
	}

	public void setAccessibles(List<Accessible> accessibles) {
		this.accessibles = accessibles;
	}

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="created_by")	
	public SystemUser getCreated_by() {
		return this.created_by;
	}

	public void setCreated_by(SystemUser created_by) {
		this.created_by = created_by;
	}


	//uni-directional many-to-one association to modifier
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="modified_by")	
	public SystemUser getModified_by() {
		return this.modified_by;
	}

	public void setModified_by(SystemUser modified_by) {
		this.modified_by = modified_by;
	}
	
	@Type(type = "org.hibernate.type.TextType")
	@Lob
	@Column(columnDefinition="text")
	public String getMegjegyzes() {
		return megjegyzes;
	}


	public void setMegjegyzes(String megjegyzes) {
		this.megjegyzes = megjegyzes;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="kervenybeerkezesdatuma")
	@Basic
	public Date getKervenyBeerkezesDatuma() {
		return kervenyBeerkezesDatuma;
	}


	public void setKervenyBeerkezesDatuma(Date ker) {
		this.kervenyBeerkezesDatuma = ker;
	}

	@Basic
	public Boolean getMegallapodas() {
		return megallapodas;
	}


	public void setMegallapodas(Boolean megallapodas) {
		this.megallapodas = megallapodas;
	}

	@Basic
	public Boolean getNyilatkozatalap() {
		return nyilatkozatalap;
	}


	public void setNyilatkozatalap(Boolean nyalap) {
		this.nyilatkozatalap = nyalap;
	}

	@Basic
	public Boolean getNyilatkozatadat() {
		return nyilatkozatadat;
	}


	public void setNyilatkozatadat(Boolean nyadat) {
		this.nyilatkozatadat = nyadat;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="current_manager")	
	public SystemUser getCurrentManager() {
        return currentManager;
    }

    public void setCurrentManager(SystemUser currentManager) {
        this.currentManager = currentManager;
    }


    @Temporal(TemporalType.DATE)
	@Column(name="utolsoGondozasiTervDatuma")
	public Date getUtolsoGondozasiTervDatuma() {
		return utolsoGondozasiTervDatuma;
	}


	public void setUtolsoGondozasiTervDatuma(Date gondtdatum) {
		this.utolsoGondozasiTervDatuma = gondtdatum;
	}


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="client_type")
    public ClientType getClientType() {
        return clientType;
    }


    /**
     * @param clientType the clientType to set
     */
    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }


    @Basic    
    public Boolean getActive() {
        return active;
    }


    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }


    /**
     * @return the currentDoctor
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="current_doctor")
    public Doctor getCurrentDoctor() {
        return currentDoctor;
    }


    /**
     * @param currentDoctor the currentDoctor to set
     */
    public void setCurrentDoctor(Doctor currentDoctor) {
        this.currentDoctor = currentDoctor;
    }

    @Version
    public Long getVersion() {
    	return version;
    }
    
    public void setVersion(Long version) {
    	this.version = version;
    }

    @Override
    public int compareTo(Client o) {
        return this.neve.compareTo(o.neve);
    }
    
    @Transient
    public Integer getAge() {
        if (szuletesiIdo == null) {
            return null;
        }
        Calendar newCDate = new GregorianCalendar();
        long period =  newCDate.getTime().getTime() - szuletesiIdo.getTime();
        Calendar epoch = new GregorianCalendar(); 
        epoch.setTimeInMillis(0L); //1970.01.01 0 msec
        Calendar age = new GregorianCalendar();
        age.setTime(new Date(period));
        Long ageYear = new Long(age.get(Calendar.YEAR) - epoch.get(Calendar.YEAR));
        return ageYear.intValue();
    }

    @Override
    public String toString() {
        return "Client{\nid:\n"+String.valueOf(id)+"\nnyilvantartasiSzam:\n"+nyilvantartasiSzam+"\n}";
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((anyjaNeve == null) ? 0 : anyjaNeve.hashCode());
		result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result + ((deletedDate == null) ? 0 : deletedDate.hashCode());
		result = prime * result + ((felvetDatum == null) ? 0 : felvetDatum.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((jovedelemIgazolas == null) ? 0 : jovedelemIgazolas.hashCode());
		result = prime * result + ((kervenyBeerkezesDatuma == null) ? 0 : kervenyBeerkezesDatuma.hashCode());
		result = prime * result + ((megallapodas == null) ? 0 : megallapodas.hashCode());
		result = prime * result + ((megjegyzes == null) ? 0 : megjegyzes.hashCode());
		result = prime * result + ((megszDatum == null) ? 0 : megszDatum.hashCode());
		result = prime * result + ((modificationDate == null) ? 0 : modificationDate.hashCode());
		result = prime * result + ((neve == null) ? 0 : neve.hashCode());
		result = prime * result + ((nyilatkozatadat == null) ? 0 : nyilatkozatadat.hashCode());
		result = prime * result + ((nyilatkozatalap == null) ? 0 : nyilatkozatalap.hashCode());
		result = prime * result + ((nyilvantartasiSzam == null) ? 0 : nyilvantartasiSzam.hashCode());
		result = prime * result + ((orvosiJavaslat == null) ? 0 : orvosiJavaslat.hashCode());
		result = prime * result + ((szuletesiHely == null) ? 0 : szuletesiHely.hashCode());
		result = prime * result + ((szuletesiIdo == null) ? 0 : szuletesiIdo.hashCode());
		result = prime * result + ((szuletesiNev == null) ? 0 : szuletesiNev.hashCode());
		result = prime * result + ((taj == null) ? 0 : taj.hashCode());
		result = prime * result + ((utolsoGondozasiTervDatuma == null) ? 0 : utolsoGondozasiTervDatuma.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Client other = (Client) obj;
		if (accessibles == null) {
			if (other.accessibles != null)
				return false;
		}
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (anyjaNeve == null) {
			if (other.anyjaNeve != null)
				return false;
		} else if (!anyjaNeve.equals(other.anyjaNeve))
			return false;
		if (clientType == null) {
			if (other.clientType != null)
				return false;
		}
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (created_by == null) {
			if (other.created_by != null)
				return false;
		}
		if (currentDoctor == null) {
			if (other.currentDoctor != null)
				return false;
		}
		if (currentManager == null) {
			if (other.currentManager != null)
				return false;
		}
		if (deletedDate == null) {
			if (other.deletedDate != null)
				return false;
		} else if (!deletedDate.equals(other.deletedDate))
			return false;
		if (felvetDatum == null) {
			if (other.felvetDatum != null)
				return false;
		} else if (!felvetDatum.equals(other.felvetDatum))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (jovedelemIgazolas == null) {
			if (other.jovedelemIgazolas != null)
				return false;
		} else if (!jovedelemIgazolas.equals(other.jovedelemIgazolas))
			return false;
		if (kervenyBeerkezesDatuma == null) {
			if (other.kervenyBeerkezesDatuma != null)
				return false;
		} else if (!kervenyBeerkezesDatuma.equals(other.kervenyBeerkezesDatuma))
			return false;
		if (megallapodas == null) {
			if (other.megallapodas != null)
				return false;
		} else if (!megallapodas.equals(other.megallapodas))
			return false;
		if (megjegyzes == null) {
			if (other.megjegyzes != null)
				return false;
		} else if (!megjegyzes.equals(other.megjegyzes))
			return false;
		if (megszDatum == null) {
			if (other.megszDatum != null)
				return false;
		} else if (!megszDatum.equals(other.megszDatum))
			return false;
		if (modificationDate == null) {
			if (other.modificationDate != null)
				return false;
		} else if (!modificationDate.equals(other.modificationDate))
			return false;
		if (modified_by == null) {
			if (other.modified_by != null)
				return false;
		}
		if (neve == null) {
			if (other.neve != null)
				return false;
		} else if (!neve.equals(other.neve))
			return false;
		if (nyilatkozatadat == null) {
			if (other.nyilatkozatadat != null)
				return false;
		} else if (!nyilatkozatadat.equals(other.nyilatkozatadat))
			return false;
		if (nyilatkozatalap == null) {
			if (other.nyilatkozatalap != null)
				return false;
		} else if (!nyilatkozatalap.equals(other.nyilatkozatalap))
			return false;
		if (nyilvantartasiSzam == null) {
			if (other.nyilvantartasiSzam != null)
				return false;
		} else if (!nyilvantartasiSzam.equals(other.nyilvantartasiSzam))
			return false;
		if (orvosiJavaslat == null) {
			if (other.orvosiJavaslat != null)
				return false;
		} else if (!orvosiJavaslat.equals(other.orvosiJavaslat))
			return false;
		if (szuletesiHely == null) {
			if (other.szuletesiHely != null)
				return false;
		} else if (!szuletesiHely.equals(other.szuletesiHely))
			return false;
		if (szuletesiIdo == null) {
			if (other.szuletesiIdo != null)
				return false;
		} else if (!szuletesiIdo.equals(other.szuletesiIdo))
			return false;
		if (szuletesiNev == null) {
			if (other.szuletesiNev != null)
				return false;
		} else if (!szuletesiNev.equals(other.szuletesiNev))
			return false;
		if (taj == null) {
			if (other.taj != null)
				return false;
		} else if (!taj.equals(other.taj))
			return false;
		if (utolsoGondozasiTervDatuma == null) {
			if (other.utolsoGondozasiTervDatuma != null)
				return false;
		} else if (!utolsoGondozasiTervDatuma.equals(other.utolsoGondozasiTervDatuma))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
       
}