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

import hu.infokristaly.back.domain.Subject;
import hu.infokristaly.back.domain.SubjectType;
import hu.infokristaly.middle.service.SubjectService;

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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class SubjectManager implements Serializable {

    private static final long serialVersionUID = -3562322678573374746L;

    /** The log. */
    @Inject
    private Logger log;

    private static final int DEFAULT_SUBJECT_TYPE_ID = 5;
    /** The file info service. */
    @Inject
    private SubjectService subjectService;

    private Subject newSubject = new Subject();

    private Subject currentSubject = new Subject();

    private SubjectType currentSubjectType;

    private Integer currentSubjectTypeId;

    private List<SubjectType> subjectTypes;

    private Integer subjectsCount = null;

    /** The lazy data model. */
    private LazyDataModel<Subject> lazyDataModel;

    private Subject[] selectedSubjects = {};

    private Boolean deletedVisible = null;

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<Subject> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<Subject>() {

                private static final long serialVersionUID = 1678907483750487431L;

                private Map<String, Object> actualfilters;
                private String actualOrderField;
                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyFileInfoDataModel] constructor finished.");
                }

                @Override
                public Subject getRowData(String rowKey) {
                    Integer primaryKey = Integer.valueOf(rowKey);
                    return subjectService.findSubject(primaryKey);
                }

                @Override
                public Object getRowKey(Subject subject) {
                    return subject.getId();
                }

                @Override
                public List<Subject> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    subjectsCount = null;
                    this.setPageSize(pageSize);
                    if ((deletedVisible != null)) {
                        filters.put("deleteDate", deletedVisible);
                    }
                    this.actualfilters = filters;
                    if (sortField != null) {
                        this.actualOrderField = sortField;
                    }
                    if (sortOrder != null) {
                        this.actualSortOrder = sortOrder;
                    }
                    List<Subject> result = (List<Subject>) subjectService.findRange(first, pageSize, this.actualOrderField, this.actualSortOrder, filters);
                    log.log(Level.INFO, "[LazyFileInfoDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (subjectsCount == null) {
                        subjectsCount = subjectService.count(actualfilters);
                    }
                    return subjectsCount;
                }

            };
        }
        return lazyDataModel;
    }

    public List<SubjectType> getSubjectTypes() {
        if (subjectTypes == null) {
            subjectTypes = subjectService.findAllSubjectType();
        }
        return subjectTypes;
    }

    public void createCurrentSubject() {
        setCurrentSubject(new Subject());
    }

    public void createNewSubject() {
        setNewSubject(new Subject());
        newSubject.setLenghtInMinute(60);
    }

    public void onEdit(RowEditEvent event) {
        Subject subject = (Subject) event.getObject();
        FacesMessage msg = new FacesMessage("Foglalkozás adatai átszerkesztve", subject.getTitle());
        subjectService.persistSubject(subject);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Új kliens módosítása visszavonva", ((Subject) event.getObject()).getComment());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Subject getCurrentSubject() {
        return currentSubject;
    }

    public void setCurrentSubject(Subject subject) {
        this.currentSubject = subject;
    }

    public void prepareNewSubject(Subject subject) {
        this.currentSubject = subject;
        this.currentSubjectType = subject.getSubjectType();
        if (this.currentSubjectType != null) {
            this.currentSubjectTypeId = this.currentSubjectType.getId();
        } else {
            this.currentSubjectTypeId = null;
        }
    }

    public void persistNew() {
        newSubject.setUniqueMeeting(false);
        newSubject.setResetAlert(true);
        SubjectType defaultSubjectType = subjectService.findSubjectType(DEFAULT_SUBJECT_TYPE_ID);
        newSubject.setSubjectType(defaultSubjectType);
        subjectService.persistSubject(newSubject);
        createNewSubject();
    }

    public void persistCurrent() {
        currentSubjectType = subjectService.findSubjectType(currentSubjectTypeId);
        currentSubject.setSubjectType(currentSubjectType);
        subjectService.persistSubject(currentSubject);
        createCurrentSubject();
    }

    public Subject[] getSelectedSubjects() {
        return selectedSubjects;
    }

    public void setSelectedSubjects(Subject[] selectedSubjects) {
        this.selectedSubjects = selectedSubjects;
    }

    public void deleteSubjects() {
        Date deleteDate = new Date();
        for (Subject item : selectedSubjects) {
            try {
                subjectService.deleteSubject(item);
            } catch (EJBTransactionRolledbackException ex) {
                item.setDeleteDate(deleteDate);
                subjectService.merge(item);
                FacesMessage msg = new FacesMessage("Megjegyzés", "[" + item.getTitle() + "] hivatkozás miatt nem törölhető, ezért passziv állapotba került.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void restoreSubjects() {
        for (Subject item : selectedSubjects) {
            item.setDeleteDate(null);
            subjectService.merge(item);
        }
    }

    public SubjectType getCurrentSubjectType() {
        return currentSubjectType;
    }

    public void setCurrentSubjectType(SubjectType currentSubjectType) {
        this.currentSubjectType = currentSubjectType;
    }

    public Subject getNewSubject() {
        return newSubject;
    }

    public void setNewSubject(Subject newSubject) {
        this.newSubject = newSubject;
    }

    public Integer getCurrentSubjectTypeId() {
        return currentSubjectTypeId;
    }

    public void setCurrentSubjectTypeId(Integer currentSubjectTypeId) {
        this.currentSubjectTypeId = currentSubjectTypeId;
    }

    public Boolean getDeletedVisible() {
        return deletedVisible;
    }

    public void setDeletedVisible(Boolean deletedVisible) {
        this.deletedVisible = deletedVisible;
    }

}
