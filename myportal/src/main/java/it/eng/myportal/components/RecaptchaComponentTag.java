package it.eng.myportal.components;


import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

public class RecaptchaComponentTag extends UIComponentELTag {
  private ValueExpression publicKey;
  private ValueExpression privateKey;

  public void setPublicKey(ValueExpression s) {
    publicKey = s;
  }

  public void setPrivateKey(ValueExpression s) {
    privateKey = s;
  }

  @Override
  public String getComponentType() {
    return RecaptchaComponent.RECAPTCHA_COMPONENT_FAMILY;
  }

  @Override
  public String getRendererType() {
    return RecaptchaRenderer.RENDERERTYPE;
  }

  @Override
  protected void setProperties(UIComponent component) {
    super.setProperties(component);
  
    if (component instanceof RecaptchaComponent) {
      RecaptchaComponent c = (RecaptchaComponent) component;

      if (publicKey != null) {
        if (publicKey.isLiteralText()) {
          c.setPublicKey(publicKey.getExpressionString());
        } else {
          c.setValueExpression("publicKey", publicKey);
        }
      }

      if (privateKey != null) {
        if (privateKey.isLiteralText()) {                
          c.setPrivateKey(privateKey.getExpressionString());
        } else {
          c.setValueExpression("privateKey", privateKey);
        }
      }
    }
  }
}
