package it.eng.myportal.aop;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class JamonInterceptor {

	protected static Log log = LogFactory.getLog(JamonInterceptor.class);

	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws Exception {
		Monitor mon = null;
		String label = null;

		try {
			label = ctx.getMethod().getDeclaringClass().getSimpleName()+"."
					+ ctx.getMethod().getName();
			mon = MonitorFactory.start(label);
			
			return ctx.proceed();
		} catch (Exception e) {
			//log.error(e.getMessage());
			throw e;
		} finally {
			mon.stop();
		}

	}

}
