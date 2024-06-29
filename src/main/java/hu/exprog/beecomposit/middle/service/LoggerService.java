package hu.exprog.beecomposit.middle.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Named;

@Named
@Stateless
public class LoggerService {

	private Logger logger;
	
	@PostConstruct
	private void init() {
		logger = Logger.getGlobal();
	}
	
	public void createLog(Level level, String subject, String content) {
		logger.log(level, subject, content);
	}
	
	public Logger getLog() {
		return logger;
	}
}
