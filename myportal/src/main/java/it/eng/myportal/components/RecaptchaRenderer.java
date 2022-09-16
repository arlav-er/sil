package it.eng.myportal.components;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaImpl;

@FacesRenderer(componentFamily = "RecaptchaComponentFamily", rendererType = "RecaptchaComponentRenderer")
public class RecaptchaRenderer extends Renderer {

  static final String RENDERERTYPE = "RecaptchaComponentRenderer";

  @Override
  public void decode(FacesContext context,
    UIComponent component) {
    if (component instanceof UIInput) {
      UIInput input = (UIInput) component;
      String clientId = input.getClientId(context);

      Map requestMap =
        context.getExternalContext().getRequestParameterMap();
      String newValue = (String) requestMap.get(clientId);
      if (null != newValue) {
        input.setSubmittedValue(newValue);
      }
    }
  }

  @Override
  public void encodeBegin(FacesContext ctx,
    UIComponent component) throws IOException {
  }

  @Override
  public void encodeEnd(FacesContext ctx,
    UIComponent component)
    throws IOException {
   
    if (component instanceof RecaptchaComponent) {       
      RecaptchaComponent rc = (RecaptchaComponent) component;
      String publicKey = rc.getPublicKey();
      String privateKey = rc.getPrivateKey();
      if (publicKey == null || privateKey == null) {
        throw new IllegalArgumentException("reCaptcha keys cannot be null. This is probably a component bug.");
      }
      
      ReCaptchaImpl recaptcha = new ReCaptchaImpl();
      recaptcha.setIncludeNoscript(false);
      recaptcha.setPrivateKey(privateKey);
      recaptcha.setPublicKey(publicKey);
      recaptcha.setRecaptchaServer("https://www.google.com/recaptcha/api");
      
      ReCaptcha c = recaptcha;
      String createRecaptchaHtml = c.createRecaptchaHtml(null, null);
      ResponseWriter writer = ctx.getResponseWriter();
      writer.write(createRecaptchaHtml);
      
    }
  }
}