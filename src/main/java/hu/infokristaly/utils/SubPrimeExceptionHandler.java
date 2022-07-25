package hu.infokristaly.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.FacesContext;

//import org.omnifaces.config.WebXml;
import org.primefaces.application.exceptionhandler.ExceptionInfo;
import org.primefaces.application.exceptionhandler.PrimeExceptionHandler;
import org.primefaces.component.ajaxexceptionhandler.AjaxExceptionHandler;
import org.primefaces.util.ComponentUtils;

public class SubPrimeExceptionHandler extends PrimeExceptionHandler {

	public static Map<String, Object> getRedirectedMap(FacesContext context) {
		Map<String, Object> map = context.getExternalContext().getSessionMap();
		return map;
	}

	public SubPrimeExceptionHandler(ExceptionHandler wrapped) {
		super(wrapped);
	}

	protected String findErrorPageLocation(FacesContext context,
			Throwable exception) {
//		return WebXml.INSTANCE.findErrorPageLocation(exception);
	    return null;
	}

	@Override
	protected AjaxExceptionHandler findHandlerComponent(FacesContext context,
			final Throwable throwable) {

		final List<AjaxExceptionHandler> handlers = new ArrayList<>();

		VisitCallback visitCallback = new VisitCallback() {

			@Override
			public VisitResult visit(VisitContext context, UIComponent target) {
				if (target instanceof AjaxExceptionHandler) {
					AjaxExceptionHandler currentHandler = (AjaxExceptionHandler) target;

					if (currentHandler.getType().isEmpty()) {
						handlers.add(currentHandler);
					}

					if (throwable.getClass().getName()
							.equals(currentHandler.getType())) {
						handlers.add(currentHandler);
						return VisitResult.COMPLETE;
					}
				}

				return VisitResult.ACCEPT;
			}
		};

		context.getViewRoot().visitTree(
				VisitContext.createVisitContext(context), visitCallback);

		return handlers.isEmpty() ? null : handlers.get(handlers.size() - 1);
	}

	@Override
	protected void handleAjaxException(FacesContext context,
			Throwable rootCause, ExceptionInfo info) throws Exception {
		getRedirectedMap(context).put(ExceptionInfo.ATTRIBUTE_NAME, info);
		super.handleAjaxException(context, rootCause, info);
	}

	@Override
	protected void handleRedirect(FacesContext context, Throwable rootCause,
			ExceptionInfo info, boolean responseResetted) throws IOException {

		getRedirectedMap(context).put(ExceptionInfo.ATTRIBUTE_NAME, info);

		String errorPage = findErrorPageLocation(context, rootCause);

		if (errorPage == null) {
			throw new IllegalArgumentException(
					"No default error page (Status 500 or java.lang.Throwable) and no error page for type \""
							+ rootCause.getClass() + "\" defined!");
		}

		context.getExternalContext().redirect(
				context.getExternalContext().getRequestContextPath()
						+ errorPage);
		context.responseComplete();
	}
}
