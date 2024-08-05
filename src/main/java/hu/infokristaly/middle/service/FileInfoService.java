package hu.infokristaly.middle.service;

import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import hu.exprog.honeyweb.middle.services.BasicService;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.FileInfo;

@Named
@Stateless
public class FileInfoService extends BasicService<FileInfo> {

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private Logger log;

	@Inject
	private LogService logService;

	private Map<String, Object> actualFilters;

	@Override
	protected String buildQueryString(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Query buildCountQuery(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Query buildQuery(String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setQueryParams(Query q, Map<String, Object> filters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntityManager getEm() {
		return em;
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public FileInfo find(FileInfo item) {
		FileInfo result = em.find(FileInfo.class, item.getId());
		return result;
	}

	@Override
	public Class<FileInfo> getDomainClass() {
		return FileInfo.class;
	}

}
