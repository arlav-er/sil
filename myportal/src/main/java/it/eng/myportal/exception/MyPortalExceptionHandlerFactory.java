package it.eng.myportal.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Factory per la creazione del nostro ExceptionHandler personalizzato
 * 
 * @author Rodi A.
 *
 */

public class MyPortalExceptionHandlerFactory extends ExceptionHandlerFactory {
	
  //pattern composite	
  private ExceptionHandlerFactory parent;
 

  public MyPortalExceptionHandlerFactory(ExceptionHandlerFactory parent) {
    this.parent = parent;
  }
 
  @Override
  public ExceptionHandler getExceptionHandler() {
    ExceptionHandler result =
        new MyPortalExceptionHandler(parent.getExceptionHandler());
    return result;
  }
}