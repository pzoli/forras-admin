/*
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Zoltan Papp
 * 
 */
package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.Doctor;
import hu.infokristaly.middle.service.DoctorService;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DoctorManager class.
 */
@SessionScoped
@Named
public class DoctorManager implements Serializable {

    private static final long serialVersionUID = -3562322678573374746L;

    /** The log. */
    @Inject
    private Logger log;

    @Inject
    private DoctorService doctorService;

    private Doctor newDoctor = new Doctor();

    private Doctor currentDoctor = new Doctor();

    /** The lazy data model. */
    private LazyDataModel<Doctor> lazyDataModel;

    private Integer count = null;

    private Doctor[] selectedDoctors = {};

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<Doctor> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<Doctor>() {

                private static final long serialVersionUID = 8639585886856147512L;

                private Map<String, Object> actualfilters;
                private String actualOrderField;
                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyDoctorDataModel] constructor finished.");
                }

                @Override
                public Doctor getRowData(String rowKey) {
                    Integer primaryKey = Integer.valueOf(rowKey);
                    return doctorService.find(primaryKey);
                }

                @Override
                public Object getRowKey(Doctor doctor) {
                    return doctor.getId();
                }

                @Override
                public List<Doctor> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    count = null;
                    this.setPageSize(pageSize);
                    this.actualfilters = filters;
                    if (sortField != null) {
                        this.actualOrderField = sortField;
                    }
                    if (sortOrder != null) {
                        this.actualSortOrder = sortOrder;
                    }
                    List<Doctor> result = (List<Doctor>) doctorService.findRange(first, pageSize, this.actualOrderField, this.actualSortOrder, filters);
                    log.log(Level.INFO, "[LazyDoctorDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (count == null) {
                        count = doctorService.count(actualfilters);
                    }
                    return count;
                }

            };
        }
        return lazyDataModel;
    }

    public void createCurrentDoctor() {
        setCurrentDoctor(new Doctor());
    }

    public void createNewDoctor() {
        setNewDoctor(new Doctor());
    }

    public void onEdit(RowEditEvent event) {
        Doctor doctor = (Doctor) event.getObject();
        FacesMessage msg = new FacesMessage("Foglalkozás adatai átszerkesztve", doctor.getName());
        doctorService.persist(doctor);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Doktor módosítása visszavonva", ((Doctor) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Doctor getCurrentDoctor() {
        return currentDoctor;
    }

    public void setCurrentDoctor(Doctor doctor) {
        this.currentDoctor = doctor;
    }

    public void prepareNewDoctor(Doctor doctor) {
        this.currentDoctor = doctor;
    }

    public void persistNew() {
        doctorService.persist(newDoctor);
        createNewDoctor();
    }

    public void persistCurrent() {
        doctorService.persist(currentDoctor);
        createCurrentDoctor();
    }

    public Doctor[] getSelectedDoctors() {
        return selectedDoctors;
    }

    public void setSelectedDoctors(Doctor[] selectedDoctors) {
        this.selectedDoctors = selectedDoctors;
    }

    public void deleteDoctors() {
        for (Doctor item : selectedDoctors) {
            try {
                doctorService.delete(item);
            } catch (EJBTransactionRolledbackException ex) {
                FacesMessage msg = new FacesMessage("Sikertelen törlés", "[" + item.getName() + "] hivatkozás miatt nem törölhető!");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        }
    }

    public Doctor getNewDoctor() {
        return newDoctor;
    }

    public void setNewDoctor(Doctor newDoctor) {
        this.newDoctor = newDoctor;
    }

}
