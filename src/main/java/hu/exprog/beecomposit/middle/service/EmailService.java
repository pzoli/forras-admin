package hu.exprog.beecomposit.middle.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import hu.exprog.beecomposit.back.model.EmailImapParams;
import hu.exprog.beecomposit.back.model.EmailSmtpParams;

@Stateless
public class EmailService {

	@PersistenceContext(unitName = "primary")
	private EntityManager em;
	
	public EmailSmtpParams getMySmtpAccount(Long systemId) {
		Query q = em.createQuery("from EmailSmtpParams where id = :systemId");
		q.setParameter("systemId", systemId);
		List<EmailSmtpParams> resultList = q.getResultList();
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	public EmailImapParams getMyImapAccount(Long systemId) {
		Query q = em.createQuery("from EmailImapParams where id = :systemId");
		q.setParameter("systemId", systemId);
		List<EmailImapParams> resultList = q.getResultList();
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

}
