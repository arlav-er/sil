package it.eng.sil.util;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

import com.engiweb.framework.base.AbstractXMLObject;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

public final class TraceWrapper {

	public static void debug(org.apache.log4j.Logger logger, String msg, Object obj) {

		if (logger.isDebugEnabled()) {
			if (obj instanceof AbstractXMLObject) {
				msg = msg + "\r\n" + ((AbstractXMLObject) obj).toXML(false);
				logger.debug(msg);
				return;
			}

			if (obj instanceof Throwable) {
				logger.debug(msg, (Throwable) obj);
				return;
			}
		}
	}

	public static void warn(org.apache.log4j.Logger logger, String msg, Object obj) {

		if (logger.isEnabledFor(Level.WARN)) {
			if (obj instanceof SourceBean) {
				msg = msg + "\r\n" + ((SourceBean) obj).toXML(false);
				logger.warn(msg);
				return;
			}

			if (obj instanceof Throwable) {
				logger.warn(msg, (Throwable) obj);
				return;
			}
		}
	}

	public static void error(org.apache.log4j.Logger logger, String msg, Object obj) {

		if (logger.isEnabledFor(Level.ERROR)) {

			if (obj instanceof SourceBean) {
				msg = msg + "\r\n" + ((SourceBean) obj).toXML(false);
				logger.error(msg);
				if (logger.isDebugEnabled())
					logDump(logger, Level.ERROR);
				return;
			}

			if (obj instanceof Throwable) {
				logger.error(msg, (Throwable) obj);
				if (logger.isDebugEnabled())
					logDump(logger, Level.ERROR);
				return;
			}
		}
	}

	public static void fatal(org.apache.log4j.Logger logger, String msg, Object obj) {

		if (logger.isEnabledFor(Level.FATAL)) {

			if (obj instanceof SourceBean) {
				msg = msg + "\r\n" + ((SourceBean) obj).toXML(false);
				logger.fatal(msg);
				if (logger.isDebugEnabled())
					logDump(logger, Level.FATAL);
				return;
			}

			if (obj instanceof Throwable) {
				logger.fatal(msg, (Throwable) obj);
				if (logger.isDebugEnabled())
					logDump(logger, Level.FATAL);
				return;
			}
		}
	}

	private static void logDump(org.apache.log4j.Logger logger, Priority priority) {
		RequestContainer rc = RequestContainer.getRequestContainer();
		if (rc != null) {
			StringBuffer buf = new StringBuffer();

			buf.append("\r\n ***************************");
			buf.append("\r\n DUMP DEL REQUEST_CONTAINER (SOLO IN DEBUG)");
			buf.append("\r\n **************************");
			buf.append("\r\n");
			buf.append(rc.toXML(false));
			buf.append("\r\n");

			SessionContainer sc = rc.getSessionContainer();
			if (sc != null) {
				buf.append("\r\n **************************");
				buf.append("\r\n DUMP DEL SESSION_CONTAINER (SOLO IN DEBUG)");
				buf.append("\r\n **************************");
				buf.append("\r\n");
				buf.append(sc.toXML(false));
				buf.append("\r\n");
			}

			logger.log(priority, buf.toString());
		}
	}

}
