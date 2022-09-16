package it.eng.myportal.renderers;

import java.io.IOException;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Resource;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.config.ConfigContainer;

import org.primefaces.context.RequestContext;

public class HeadRenderer extends org.primefaces.renderkit.HeadRenderer {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		ConfigContainer cc = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
		writer.startElement("head", component);

		// First facet
		UIComponent first = component.getFacet("first");
		if (first != null) {
			first.encodeAll(context);
		}

		// Theme
		String theme;
		String themeParamValue = RequestContext.getCurrentInstance().getApplicationContext().getConfig().getTheme();

		if (themeParamValue != null) {
			ELContext elContext = context.getELContext();
			ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
			ValueExpression ve = expressionFactory.createValueExpression(elContext, themeParamValue, String.class);

			theme = (String) ve.getValue(elContext);
		} else {
			theme = "aristo"; // default
		}

		if (theme != null && !theme.equals("none") && component.getAttributes().get("notPrimeFaces") == null) {
			encodeCSS(context, "primefaces-" + theme, "theme.css");
		}

		// Middle facet
		UIComponent middle = component.getFacet("middle");
		if (middle != null) {
			middle.encodeAll(context);
		}

		// Registered Resources
		UIViewRoot viewRoot = context.getViewRoot();
		for (UIComponent resource : viewRoot.getComponentResources(context, "head")) {
			resource.encodeAll(context);
		}

		if (cc.isClientSideValidationEnabled()) {
			encodeValidationResources(context, cc.isBeanValidationAvailable());

			writer.startElement("script", null);
			writer.writeAttribute("type", "text/javascript", null);
			writer.write("PrimeFaces.settings.locale = '" + context.getViewRoot().getLocale() + "';");
			writer.write("PrimeFaces.settings.validateEmptyFields = " + cc.isValidateEmptyFields() + ";");
			writer.write("PrimeFaces.settings.considerEmptyStringNull = " + cc.isInterpretEmptyStringAsNull() + ";");
			writer.endElement("script");
		}

		if (cc.isLegacyWidgetNamespace()) {
			writer.startElement("script", null);
			writer.writeAttribute("type", "text/javascript", null);
			writer.write("PrimeFaces.settings.legacyWidgetNamespace = true;");
			writer.endElement("script");
		}
	}
	
	protected void encodeCSS(FacesContext context, String library, String resource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        Resource cssResource = context.getApplication().getResourceHandler().createResource(resource, library);
        if (cssResource == null) {
            throw new FacesException("Error loading css, cannot find \"" + resource + "\" resource of \"" + library + "\" library");
        } 
        else {
            writer.startElement("link", null);
            writer.writeAttribute("type", "text/css", null);
            writer.writeAttribute("rel", "stylesheet", null);
            writer.writeAttribute("href", cssResource.getRequestPath(), null);
            writer.endElement("link");
        }
    }
	
	protected void encodeValidationResources(FacesContext context, boolean beanValidationEnabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Resource resource = context.getApplication().getResourceHandler().createResource("validation/validation.js", "primefaces");
        
        if(resource != null) {
            writer.startElement("script", null);
            writer.writeAttribute("type", "text/javascript", null);
            writer.writeAttribute("src", resource.getRequestPath(), null);
            writer.endElement("script");
        }
        
        if(beanValidationEnabled) {
            resource = context.getApplication().getResourceHandler().createResource("validation/beanvalidation.js", "primefaces");
        
            if(resource != null) {
                writer.startElement("script", null);
                writer.writeAttribute("type", "text/javascript", null);
                writer.writeAttribute("src", resource.getRequestPath(), null);
                writer.endElement("script");
            }
        }
    }
}
