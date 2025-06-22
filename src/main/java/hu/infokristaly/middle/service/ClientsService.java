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
package hu.infokristaly.middle.service;

import hu.infokristaly.back.domain.Accessible;
import hu.infokristaly.back.domain.AccessibleType;
import hu.infokristaly.back.domain.CaseManagerForClients;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientChanges;
import hu.infokristaly.back.domain.ClientType;
import hu.infokristaly.back.domain.Doctor;
import hu.infokristaly.back.domain.GroupForClients;
import hu.infokristaly.back.domain.SubjectType;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.back.resources.QRCodeGenerator;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import com.google.zxing.WriterException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * The FileInfoService implements Serializable, Serializable class. Used for
 * manage fileInfo entities.
 * 
 * @author pzoli
 * @param <FilterOptions>
 * @param <Restrictor>
 * 
 */

@Stateless
@Named
public class ClientsService implements Serializable {

	private static final long serialVersionUID = 4296408007529101008L;

	/** The logger. */
	@Inject
	private Logger log;

	@Inject
	private LogService logService;

	/** The entity manager. */
	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private UserService userService;

	private static final long CLIENT = 2L;

	private Map<String, Object> actualFilters;

	@Inject
	private GroupForClientsService groupForClientsService;

	public ClientsService() {

	}

	private char[] alpha;

	@PostConstruct
	public void init() {
	}

	/**
	 * Find the fileInfo.
	 * 
	 * @param fileInfo the file info
	 * @return the file info
	 */
	public Client find(Client client) {
		Client result = em.find(Client.class, client.getId());
		/*
		 * if (result != null) { log.info("Found [" + client.getId() + "] = " +
		 * result.getNeve()); } else { log.info("Found [" + client.getId() +
		 * "] = null"); }
		 */
		return result;
	}

	private String buildQuery(boolean alive, Date endDate, String active, String[] types) {
		StringBuffer queryBuff = new StringBuffer();
		queryBuff.append("from ");
		if ((actualFilters != null) && actualFilters.containsKey("clientgroup")) {
			queryBuff.append(" GroupForClients g join g.clients c ");
		} else {
			queryBuff.append("Client c ");
		}

		if (actualFilters != null) {
			if (actualFilters.containsKey("currentManager.username")) {
				queryBuff.append(" left join c.currentManager m ");
			}
			if (actualFilters.containsKey("currentDoctor.name")) {
				queryBuff.append(" left join c.currentDoctor d ");
			}
		}

		String megszDatum;
		if (alive) {
			megszDatum = "c.megszDatum is null";
			if (endDate != null) {
				megszDatum = "((" + megszDatum + ") or c.megszDatum >= :endDate) and";
			} else {
				megszDatum = "((" + megszDatum + ") or c.megszDatum >= CURRENT_TIMESTAMP()) and";
			}
		} else {
			if (endDate != null) {
				megszDatum = "c.megszDatum < :endDate and";
			} else {
				megszDatum = "c.megszDatum < CURRENT_TIMESTAMP() and";
			}
		}

		queryBuff.append(" where " + megszDatum + " c.deletedDate is null ");
		if ((actualFilters != null) && actualFilters.containsKey("clientgroup")) {
			GroupForClients group = (GroupForClients) actualFilters.get("clientgroup");
			queryBuff.append(" and g.id = " + group.getId());
		}
		if ((active != null) && !active.isEmpty()) {
			queryBuff.append(" and c.active = " + active);
		}

		boolean cTypeNullContent = nullContent(types);
		if ((types != null) && (types.length > 0)) {
			if (cTypeNullContent) {
				if (types.length == 1) {
					queryBuff.append(" and c.clientType.id is null");
				} else {
					queryBuff.append(" and (c.clientType.id in (:types) or c.clientType.id is null)");
				}
			} else {
				queryBuff.append(" and c.clientType.id in (:types)");
			}
		}

		if (actualFilters != null && !actualFilters.isEmpty()) {
			Set<Entry<String, Object>> es = actualFilters.entrySet();
			for (Entry<String, Object> filter : es) {
				String field = filter.getKey();
				String value = String.valueOf(filter.getValue());
				String likeValue = field;
				if (!("".equals(value)) && !("clientgroup".equalsIgnoreCase(field))) {
					if ("currentManager.username".equalsIgnoreCase(field)) {
						field = "username";
						likeValue = "username";
					} else if ("currentDoctor.name".equalsIgnoreCase(field)) {
						field = "d.name";
						likeValue = "dname";
					} else {
						field = "c." + field;
					}
					queryBuff.append(" and ").append("lower(").append(field).append(")").append(" like :")
							.append(likeValue);
				}
			}
		}
		return queryBuff.toString();
	}

	/**
	 * Find filtered range.
	 * 
	 * @param first    the first
	 * @param pageSize the page size
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<Client> findRange(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters, boolean alive, Date endDate, String active, String[] types) {
		actualFilters = filters;
		String fields = "c";
		if (filters.containsKey("currentManager.username")) {
			fields += ",m.username";
		}
		if (filters.containsKey("currentDoctor.name")) {
			fields += ",d.name";
		}
		String query = "select " + fields + " " + buildQuery(alive, endDate, active, types);

		String sort = "";
		if (sortField != null) {
			if (sortOrder.equals(SortOrder.ASCENDING)) {
				sort = sortField + " asc";
			} else if (sortOrder.equals(SortOrder.DESCENDING)) {
				sort = sortField + " desc";
			}
			query += " order by c." + sort;
		} else {
			query += " order by c.neve";
		}

		Query q = em.createQuery(query);
		if (!filters.isEmpty()) {
			Set<Entry<String, Object>> es = filters.entrySet();
			for (Entry<String, Object> filter : es) {
				String field = filter.getKey();
				if ("currentManager.username".equalsIgnoreCase(field)) {
					field = "username";
				}
				if ("currentDoctor.name".equalsIgnoreCase(field)) {
					field = "dname";
				}
				if (!"clientgroup".equalsIgnoreCase(field)) {
					String pattern = "%" + filter.getValue().toString().toLowerCase() + "%";
					q.setParameter(field, pattern);
				}
			}
		}

		if ((types != null) && (types.length > 0)) {
			List<Long> typeIds = new LinkedList<Long>();
			for (String type : types) {
				if ((type != null) && !("".equals(type))) {
					typeIds.add(Long.valueOf(type));
				}
			}
			if (typeIds.size() > 0) {
				q.setParameter("types", typeIds);
			}
		}

		if (endDate != null) {
			q.setParameter("endDate", endDate);
		}

		q.setMaxResults(pageSize);
		q.setFirstResult(first);
		List<Client> clients = q.getResultList();

		if (filters.containsKey("currentManager.username") || filters.containsKey("currentDoctor.name")) {
			Iterator<Client> iter = clients.iterator();
			LinkedList<Client> clientResults = new LinkedList<Client>();
			while (iter.hasNext()) {
				Object item = iter.next();
				if (item instanceof Object[]) {
					Object[] rowRes = (Object[]) item;
					if (rowRes[0] instanceof Client) {
						Client client = (Client) rowRes[0];
						Doctor doc = client.getCurrentDoctor();
						if (doc != null) {
							doc.getName();
						}
						List<Accessible> accs = client.getAccessibles();
						for (Accessible accessible : accs) {
							AccessibleType atype = accessible.getAccessible_type();
							atype.getTypename();
						}
						clientResults.add(client);
					}
				}
			}
			return clientResults;
		} else {
			return clients;
		}
	}

	/**
	 * Find All
	 * 
	 * @param nameLike
	 * 
	 * @param first    the first
	 * @param pageSize the page size
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<Client> findAll(boolean alive, String active, Date endDate, String nameLike) {
		StringBuffer queryBuff = new StringBuffer();
		queryBuff.append("select c from Client c");
		String megszDatum;
		if (alive) {
			megszDatum = "c.megszDatum is null";
			if (endDate != null) {
				megszDatum = "((" + megszDatum + ") or c.megszDatum >= :endDate) and";
			} else {
				megszDatum = "((" + megszDatum + ") or c.megszDatum >= CURRENT_TIMESTAMP()) and";
			}
		} else {
			if (endDate != null) {
				megszDatum = "c.megszDatum < :endDate and";
			} else {
				megszDatum = "c.megszDatum < CURRENT_TIMESTAMP() and";
			}
		}

		queryBuff.append(" where " + ((nameLike != null) && !nameLike.isEmpty() ? "neve like :neve and " : "")
				+ megszDatum + " c.deletedDate is null ");
		if ((active != null) && !active.isEmpty()) {
			queryBuff.append(" and c.active = " + active);
		}
		queryBuff.append(" order by neve asc");

		Query q = em.createQuery(queryBuff.toString());

		if (endDate != null) {
			q.setParameter("endDate", endDate);
		}
		if ((nameLike != null) && !nameLike.isEmpty()) {
			if (!nameLike.contains("%")) {
				nameLike += "%";
			}
			q.setParameter("neve", nameLike);
		}
		return q.getResultList();
	}

	public Timestamp findLastVisit(Client client, Boolean resetAlert) {
		String select = "select max(h.start_date) from event_client ec join eventhistory h on ec.event_id = h.eventid"
				+ (resetAlert != null ? " join subject s on s.id = h.subject_id" : "") + " where ec.client_id = :client"
				+ (resetAlert != null ? " and s.resetalert = :resetalert" : "");
		Query q = em.createNativeQuery(select);
		q.setParameter("client", client.getId());
		q.setParameter("resetalert", resetAlert);
		@SuppressWarnings("unchecked")
		List list = q.getResultList();
		Object lastVisit = null;
		if ((list != null) && (list.size() >= 0) && (list.get(0) != null)) {
			lastVisit = list.get(0);
		} else {
			if (client.getCreateDate() != null) {
				lastVisit = new Timestamp(client.getCreateDate().getTime());
			}
		}
		return (Timestamp) lastVisit;
	}

	public static long getDiff(Date now, Date startDate) {
		long result = now.getTime() - startDate.getTime();
		result = result / 1000L / 60L / 60L / 24L;
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<AccessibleType> findAllAccessibleTypes() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<AccessibleType> cq = builder.createQuery(AccessibleType.class);
		Root<AccessibleType> from = cq.from(AccessibleType.class);
		cq.select(from);
		Query q = em.createQuery(cq);
		return q.getResultList();
	}

	public AccessibleType findAccessibleType(Integer id) {
		AccessibleType result = em.find(AccessibleType.class, id);
		return result;
	}

	/**
	 * Count filtered file items.
	 * 
	 * @param filters the filters
	 * @return the int
	 */
	public int count(Map<String, Object> filters, boolean alive, Date endDate, String active, String[] types) {
		actualFilters = filters;
		String query = buildQuery(alive, endDate, active, types);
		query = "select count(c) " + query;
		Query q = em.createQuery(query);

		if ((actualFilters != null) && !actualFilters.isEmpty()) {
			Set<Entry<String, Object>> es = actualFilters.entrySet();
			for (Entry<String, Object> filter : es) {
				String field = filter.getKey();
				if ("currentManager.username".equalsIgnoreCase(field)) {
					field = "username";
				} else if ("currentDoctor.name".equalsIgnoreCase(field)) {
					field = "dname";
				}

				if (!"clientgroup".equalsIgnoreCase(field)) {
					String pattern = "%" + filter.getValue().toString().toLowerCase() + "%";
					q.setParameter(field, pattern);
				}
			}
		}

		if (endDate != null) {
			q.setParameter("endDate", endDate);
		}

		if (alive && (types != null) && (types.length > 0)) {
			List<Long> typeIds = new LinkedList<Long>();
			for (String type : types) {
				if ((type != null) && !("".equals(type))) {
					typeIds.add(Long.valueOf(type));
				}
			}
			if (typeIds.size() > 0) {
				q.setParameter("types", typeIds);
			}
		}

		int result = ((Long) q.getSingleResult()).intValue();
		return result;
	}

	private boolean nullContent(String[] types) {
		boolean result = false;
		if (types != null) {
			for (String type : types) {
				if ((type == null) || "".equals(type)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public Calendar resetPeriod(Calendar periodStart) {
		periodStart.set(Calendar.MILLISECOND, 0);
		periodStart.set(Calendar.SECOND, 0);
		periodStart.set(Calendar.MINUTE, 0);
		periodStart.set(Calendar.HOUR_OF_DAY, 0);
		return periodStart;
	}

	public Client persistClient(Client newClient) {
		Client result = newClient;
		if (newClient.getId() == null) {
			em.persist(newClient);

			ClientChanges change = new ClientChanges(newClient);
			change.setModifiedAt(new Date());
			if (newClient.getFelvetDatum() != null) {
				change.setPeriodStart(newClient.getFelvetDatum());
			} else {
				Calendar periodStart = new GregorianCalendar();
				change.setModifiedAt(periodStart.getTime());
				periodStart = resetPeriod(periodStart);
				change.setPeriodStart(periodStart.getTime());
				newClient.setFelvetDatum(periodStart.getTime());
			}
			change.setModifiedBy(newClient.getCreated_by());
			change.setChangeType((byte) 0);
			em.persist(change);
			logService.logUserActivity(newClient.getCreated_by(), CLIENT, LogService.CREATE, newClient);
		} else {
			Client preClient = em.find(Client.class, newClient.getId());
			if ((preClient.getCurrentManager() != null) && (newClient.getCurrentManager() != null)
					&& preClient.getCurrentManager().getId() != newClient.getCurrentManager().getId()) {
				updateCaseManagerHistory(preClient, newClient);
			}

			byte changeType = checkForChanges(preClient, newClient);
			if ((changeType > 0) || ((preClient.getFelvetDatum() == null) && (newClient.getFelvetDatum() != null))) {
				ClientChanges change = new ClientChanges();
				change.setClient(preClient);
				if ((preClient.getFelvetDatum() == null) && (newClient.getFelvetDatum() != null)) {
					change.setModifiedAt(newClient.getFelvetDatum());
					change.setPeriodStart(newClient.getFelvetDatum());
					change.setChangeType((byte) 0);
					change.setActive(true);
				} else {
					change.setModifiedAt(newClient.getModificationDate());
					change.setChangeType(changeType);
					Calendar periodStart = new GregorianCalendar();
					if (newClient.getActive() && (newClient.getMegszDatum() != null)
							&& !(newClient.getMegszDatum().equals(preClient.getMegszDatum()))) {
						periodStart.setTime(newClient.getMegszDatum());
						newClient.setActive(false);
						groupForClientsService.removeClient(preClient);
					}
					periodStart = resetPeriod(periodStart);

					if (!newClient.getActive() || !newClient.getClientType().equals(preClient.getClientType())) {
						periodStart.add(Calendar.DATE, 1);
					}
					change.setPeriodStart(periodStart.getTime());
				}
				change.setActive(newClient.getActive());
				change.setModifiedBy(newClient.getModified_by());
				change.setClientType(newClient.getClientType());

				em.persist(change);
			}

			try {
				result = em.merge(newClient);
				logService.logUserActivity(newClient.getModified_by(), CLIENT, LogService.MODIFY, result);
			} catch (javax.persistence.OptimisticLockException e) {
				FacesContext context = FacesContext.getCurrentInstance();
			    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Konkurens módosítás", "Valaki már módosította Ön előtt ezt a klienst!"));
			}
		}

		return result;
	}

	private ClientChanges findFirstLog(Client preClient) {
		String query = "select min(id) from ClientChanges where client = :client";
		Query q = em.createQuery(query);
		q.setParameter("client", preClient);
		Object o = q.getSingleResult();
		ClientChanges ch = null;
		if (o instanceof Integer) {
			String queryFirstChange = "from ClientChanges where id = :id";
			Query qFirstChange = em.createQuery(queryFirstChange);
			qFirstChange.setParameter("id", (Integer) o);
			ch = (ClientChanges) qFirstChange.getSingleResult();
		}
		return ch;
	}

	public byte checkForChanges(Client preClient, Client newClient) {
		byte result = (byte) ((newClient.getClientType() != null)
				&& !newClient.getClientType().equals(preClient.getClientType()) ? 1 : 0);
		result += (byte) ((newClient.getActive() && (preClient.getMegszDatum() == null)
				&& (newClient.getMegszDatum() != null)) || (preClient.getActive() != newClient.getActive()) ? 2 : 0);
		return result;
	}

	public void updateCaseManagerHistory(Client client, Client newClient) {
		if (client.getCurrentManager() != null) {
			List<CaseManagerForClients> findResult = findActiveManagerLog(client);
			if (findResult.size() > 0) {
				closeLastManagerConnect(client);
			}
			client = em.find(Client.class, client.getId());
			CaseManagerForClients manager = new CaseManagerForClients();
			manager.setClient(client);
			manager.setManager(newClient.getCurrentManager());
			manager.setValidFrom(new Date());
			em.persist(manager);
		}
	}

	@SuppressWarnings("unchecked")
	private List<CaseManagerForClients> findActiveManagerLog(Client client) {
		Query q = em.createQuery("from CaseManagerForClients where client = :client and validTo is null");
		q.setParameter("client", client);
		return (List<CaseManagerForClients>) q.getResultList();
	}

	private int closeLastManagerConnect(Client client) {
		int result = 0;
		Query q = em.createQuery(
				"update CaseManagerForClients set validTo = :validTo where client = :client and validTo is null");
		q.setParameter("client", client);
		q.setParameter("validTo", new Date());
		result = q.executeUpdate();
		return result;
	}

	public void mergeClient(Client client) {
		em.merge(client);
	}

	public Accessible persistAccessible(Accessible accessible) {
		if (accessible.getId() == null) {
			em.persist(accessible);
		} else {
			accessible = em.merge(accessible);
		}
		return accessible;
	}

	public Client deleteAccessibles(Client newClient, Accessible[] selectedAccessibles) {
		newClient = em.find(Client.class, newClient.getId());
		for (Accessible acc : selectedAccessibles) {
			boolean removedAcc = newClient.getAccessibles().remove(acc);
			if (!removedAcc) {
				System.out.print(acc.getAccessibleValue() + " not removed.");
			}
		}
		return em.merge(newClient);
	}

	public Accessible findAccessible(Integer key) {
		return em.find(Accessible.class, key);
	}

	public Client addAccessible(Client newClient, Accessible currentAccessible) {
		ClientChanges change = null;
		if (newClient.getId() != null) {
			newClient = find(newClient);
		} else {
			change = new ClientChanges();
			change.setChangeType((byte) 0);
			change.setActive(newClient.getActive());
			change.setClientType(newClient.getClientType());
			change.setModifiedAt(new Date());
			change.setModifiedBy(userService.getLoggedInSystemUser());
			change.setPeriodStart(newClient.getFelvetDatum());
		}
		persistAccessible(currentAccessible);
		newClient.getAccessibles().add(currentAccessible);
		newClient = em.merge(newClient);
		if (change != null) {
			change.setClient(newClient);
			persistClientChanges(change);
		}
		return newClient;
	}

	public void removeCaseManagers(Client client) {
		Query q = em.createQuery("delete CaseManagerForClients c where c.client = :client ");
		q.setParameter("client", client);
		q.executeUpdate();
	}

	public void removeClientChanges(Client client) {
		Query q = em.createQuery("delete ClientChanges c where c.client = :client ");
		q.setParameter("client", client);
		q.executeUpdate();
	}

	public void removeClientChanges(ClientChanges[] selectedChanges) {
		Query q = em.createQuery("delete ClientChanges c where c in (:clientChanges)");
		q.setParameter("clientChanges", Arrays.asList(selectedChanges));
		q.executeUpdate();
	}

	public void persistClientChanges(ClientChanges clientChange) {
		Client client = em.find(Client.class, clientChange.getClient().getId());
		clientChange.setClient(client);
		em.persist(clientChange);
	}

	public void remove(SystemUser user, Client client) {
		client = em.find(Client.class, client.getId());
		client.setCurrentManager(null);
		em.merge(client);
		removeCaseManagers(client);
		removeClientChanges(client);
		em.remove(client);
		logService.logUserActivity(client.getModified_by(), CLIENT, LogService.REMOVE, client);
	}

	public ClientType findClientType(ClientType clientType) {
		ClientType result = em.find(ClientType.class, clientType.getId());
		return result;
	}

	public Client findByNySzam(Client client) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Object> cq = builder.createQuery();
		Root<Client> from = cq.from(Client.class);
		cq.select(from);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		Expression<String> megszDatum = from.get("megszDatum");
		Predicate predicate = builder.isNull(megszDatum);
		predicateList.add(predicate);

		Expression<String> nySzam = from.get("nyilvantartasiSzam");
		predicate = builder.equal(nySzam, client.getNyilvantartasiSzam());
		predicateList.add(predicate);

		Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
		cq.where(predicates);

		Query q = em.createQuery(cq);
		@SuppressWarnings("unchecked")
		List<Client> list = q.getResultList();
		Client result = null;
		if (list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	public void generateQRCode(Client client, String oFileName) throws WriterException, IOException {
		String contents = "ENYSZ:" + client.getNyilvantartasiSzam() + "\tNAME:" + client.getNeve();
		QRCodeGenerator.generateFile(client.getNeve(), contents, oFileName);
	}

	@SuppressWarnings("unchecked")
	public List<ClientChanges> findClientAllChange(Client client, boolean orderAsc) {
		List<ClientChanges> changes = null;
		if ((client != null) && (client.getId() != null)) {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ClientChanges> cq = builder.createQuery(ClientChanges.class);
			Root<ClientChanges> from = cq.from(ClientChanges.class);
			cq.select(from);
			cq.orderBy(orderAsc ? builder.asc(from.get("periodStart")) : builder.desc(from.get("periodStart")));
			List<Predicate> predicateList = new ArrayList<Predicate>();

			Expression<String> x = from.get("client");
			Predicate predicate = builder.equal(x, client);
			predicateList.add(predicate);

			Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
			cq.where(predicates);

			Query q = em.createQuery(cq);

			changes = q.getResultList();
		}
		return changes;
	}

	public ClientChanges findClientChange(Client client, Date eventStartDate) {
		ClientChanges result = null;
		ClientChanges first = null;
		if ((client != null) && (eventStartDate != null)) {
			List<ClientChanges> changes = findClientAllChange(client, false);
			Iterator<ClientChanges> iter = changes.iterator();
			boolean found = false;
			while (iter.hasNext()) {
				ClientChanges item = iter.next();
				found = (item.getPeriodStart() != null) && eventStartDate.after(item.getPeriodStart());
				if (found) {
					result = item;
					break;
				} else if (item.getPeriodStart() == null) {
					first = new ClientChanges(item);
				}
			}
			if (result == null) {
				result = first;
			}
		}
		return result;
	}

	public ClientChanges findClientNextChange(Client client, Date eventStartDate) {
		ClientChanges result = null;
		ClientChanges first = null;
		if ((client != null) && (eventStartDate != null)) {
			List<ClientChanges> changes = findClientAllChange(client, true);
			Iterator<ClientChanges> iter = changes.iterator();
			boolean found = false;
			while (iter.hasNext()) {
				ClientChanges item = iter.next();
				found = (item.getPeriodStart() != null) && eventStartDate.before(item.getPeriodStart());
				if (found) {
					result = item;
					break;
				} else if (item.getPeriodStart() == null) {
					first = new ClientChanges(item);
				}
			}
			if (result == null) {
				result = first;
			}
		}
		return result;
	}

	public void mergeChange(ClientChanges clientChange) {
		em.merge(clientChange);
	}

	public ClientChanges getClientVersion(Client client, Date eventStartDate) throws Exception {
		ClientChanges result = new ClientChanges();
		if (((client.getModificationDate() != null))) {
			if (eventStartDate.before(client.getModificationDate())) {
				ClientChanges version = findClientChange(client, eventStartDate);
				if (version != null) {
					result = version;
				} else {
					// módosult, de nem az aktivitás vagy a típus miatt,
					// Ezért nincs naplózás, a client mezőit kell használni
					if (client.getFelvetDatum() != null) {
						if (eventStartDate.after(client.getFelvetDatum())) {
							result = new ClientChanges(client);
						} else {
							String msg = "Felhasználó nincs felvéve az aktuális dátumtól! (nyszám:"
									+ client.getNyilvantartasiSzam() + ", esemény kezdete:" + eventStartDate.toString()
									+ ", felvéve:" + client.getFelvetDatum() + ");";
							log.severe(msg);
							throw new Exception(msg);
						}
					}
				}
			} else {
				// Az utolsó módosított kliens ami az esemény előtti aktuális
				// állapotot mutatja
				result.setClient(client);
				result.setActive(client.getActive());
				result.setModifiedAt(client.getModificationDate());
				result.setModifiedBy(client.getModified_by());
				result.setClientType(client.getClientType());
			}
		} else if ((client.getFelvetDatum() != null) && eventStartDate.after(client.getFelvetDatum())) {
			// Az első felvett kliens, ami az esemény előtti aktuális állapotot
			// mutatja
			result = new ClientChanges(client);
		} else {
			// Jelzés és hiba naplózás
			String msg;
			if (client.getFelvetDatum() == null) {
				msg = "Nincs a felvétel dátuma kitöltve (nyszam: " + client.getNyilvantartasiSzam() + ")";
			} else {
				msg = "Felvétel dátuma későbbi, mint az esemény kezdődátuma (felv.dátum:"
						+ client.getFelvetDatum().toString() + ", nyszam: " + client.getNyilvantartasiSzam()
						+ ", esemény kezdete: " + eventStartDate.toString() + ")";
			}
			log.severe(msg);
			throw new Exception(msg);
		}
		return result;
	}

	public Map<String, Number> getCountByMonth(Date dateTo, ClientType clientType) {
		Map<String, Number> byMonth = new LinkedHashMap<String, Number>();

		String sql = "from Client c where c.felvetDatum between :startDate and :endDate and (c.deletedDate is null or c.deletedDate >:endDate)";

		int year;
		if (dateTo == null) {
			year = GregorianCalendar.getInstance().get(Calendar.YEAR);
		} else {
			Calendar calcYear = new GregorianCalendar();
			calcYear.setTime(dateTo);
			year = calcYear.get(Calendar.YEAR);
		}
		IntStream.range(0, 12).forEach(c -> {
			Calendar yearCal = new GregorianCalendar(year, c, 1);
			Calendar startCal = new GregorianCalendar();
			startCal.setTime(yearCal.getTime());
			int maxDay = yearCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			yearCal.set(Calendar.DAY_OF_MONTH, maxDay);
			Calendar endCal = new GregorianCalendar();
			endCal.setTime(yearCal.getTime());
			endCal.add(Calendar.DATE, 1);

			Query q = em.createQuery(sql);
			q.setParameter("startDate", startCal.getTime());
			q.setParameter("endDate", endCal.getTime());
			List<Client> result = q.getResultList();
			List<Client> filteredResult;
			if (clientType != null) {
				filteredResult = result.stream().filter(p -> {
					ClientChanges ch = null;
					try {
						// log.info("NySzam: " + p.getNyilvantartasiSzam() + ",
						// felvet.datum: " + p.getFelvetDatum() + ", " + "filter
						// date: "
						// +SimpleDateFormat.getDateInstance().format(endCal.getTime()));
						ch = getClientVersion(p, endCal.getTime());
						// log.info("change: " +
						// SimpleDateFormat.getDateInstance().format(ch.getPeriodStart()));
					} catch (Exception e) {
					}
					return (clientType == null) || ((clientType != null) && (ch != null) && (ch.getClientType() != null)
							&& ch.getClientType().equals(clientType));
				}).collect(Collectors.toList());
			} else {
				filteredResult = result;
			}
			Long countByMonth = new Long(filteredResult.size());
			log.info(startCal.getTime() + " - " + endCal.getTime() + " : " + countByMonth);
			byMonth.put(String.valueOf(c + 1), (Number) countByMonth);

		});
		return byMonth;
	}

	public Map<String, Number> getEventCountByMonth(Date dateTo, SubjectType type) {
		Map<String, Number> byMonth = new LinkedHashMap<String, Number>();

		String sqlMonth = "select count(h.id) as cnt from EventHistory h where h.startDate between :startDate and :endDate"
				+ (type == null ? "" : " and h.subject.subjectType = :type");

		int year;
		if (dateTo == null) {
			year = GregorianCalendar.getInstance().get(Calendar.YEAR);
		} else {
			Calendar calcYear = new GregorianCalendar();
			calcYear.setTime(dateTo);
			year = calcYear.get(Calendar.YEAR);
		}
		IntStream.range(0, 12).forEach(c -> {

			Calendar yearCal = new GregorianCalendar(year, c, 1);
			Calendar startCal = new GregorianCalendar();
			startCal.setTime(yearCal.getTime());
			int maxDay = yearCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			yearCal.set(Calendar.DAY_OF_MONTH, maxDay);
			Calendar endCal = new GregorianCalendar();
			endCal.setTime(yearCal.getTime());
			endCal.add(Calendar.DATE, 1);

			Query qCount = em.createQuery(sqlMonth);
			qCount.setParameter("startDate", startCal.getTime());
			qCount.setParameter("endDate", endCal.getTime());
			Long countByMonth = (Long) qCount.getSingleResult();
			if (countByMonth > 0) {
				log.info(startCal.getTime() + " - " + endCal.getTime() + " : " + countByMonth);
				byMonth.put(String.valueOf(c + 1), countByMonth);
			} else {
				log.info(startCal.getTime() + " - " + endCal.getTime() + " : 0");
				byMonth.put(String.valueOf(c + 1), 0);
			}
		});
		return byMonth;
	}

	public Map<String, Number> getCountByType(Date year) {
		Map<String, Number> byType = new LinkedHashMap<String, Number>();
		if (year == null) {
			Calendar yearCal = new GregorianCalendar(GregorianCalendar.getInstance().get(Calendar.YEAR), 11, 31);
			year = yearCal.getTime();
		}
		String sql = "select c from Client c where c.felvetDatum <= :felvetDatum and (c.deletedDate is null or c.deletedDate >:felvetDatum)";
		Query q = em.createQuery(sql);
		q.setParameter("felvetDatum", year);
		List<Client> result = q.getResultList();
		result.stream().forEach(c -> {
			if (c.getClientType() == null) {
				Number cnt = (Number) byType.get("nincs meghatározva");
				byType.put("nincs meghatározva", cnt == null ? 1 : cnt.longValue() + 1);
			} else {
				Number cnt = (Number) byType.get(c.getClientType().getTypename());
				byType.put(c.getClientType().getTypename(), cnt == null ? 1 : cnt.longValue() + 1);
			}

		});
		return byType;
	}

	/**
	 * @return the alpha
	 */
	public char[] getAlpha() {
		char[] a = "AÁBCDEÉFGHIÍJKLMNOÓÖŐPQRSTUÚÜŰVWXYZ".toCharArray();
		alpha = a;
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(char[] alpha) {
		this.alpha = alpha;
	}

}
