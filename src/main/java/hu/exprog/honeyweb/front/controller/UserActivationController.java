package hu.exprog.honeyweb.front.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import hu.exprog.beecomposit.front.manager.UserRegisterManager;

@Named
@ViewScoped
public class UserActivationController implements Serializable {

	private static final long serialVersionUID = 2054178976457073545L;

	@Inject
	private UserRegisterManager userRegisterManager;

	private String register;

	@PostConstruct
	public void init() {

	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public void activateUser() {
		if (register != null) {
			userRegisterManager.activateUser(register);
		}
	}

}
