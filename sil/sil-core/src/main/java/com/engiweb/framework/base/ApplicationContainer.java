package com.engiweb.framework.base;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class AttributesReaper extends Thread {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ApplicationContainer.class.getName());
	private int MIN_REAPING_TIME = 10;
	private ApplicationContainer _container = null;
	private long _reapingTime = 0;
	private boolean _exit = false;

	AttributesReaper(ApplicationContainer container, int reapingTime) {
		_container = container;
		if (reapingTime < MIN_REAPING_TIME)
			reapingTime = MIN_REAPING_TIME;
		_reapingTime = reapingTime * 1000;
		_exit = false;
	} // AttributesReaper(ApplicationContainer container, long reapingTime)

	public void run() {
		_logger.debug("AttributesReaper::run: thread partito");

		while (!_exit) {
			try {
				sleep(_reapingTime);
			} // try
			catch (InterruptedException e) {
			} // catch (InterruptedException e) try
			_container = null;
		} // while (!_exit)
	} // public void run()

	public void terminate() {
		_exit = true;
		this.interrupt();
	} // public void terminate()
} // class AttributesReaper extends Thread

public class ApplicationContainer extends BaseContainer {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ApplicationContainer.class.getName());
	private static int REAPING_TIME = 60;
	private static ApplicationContainer _instance = null;
	private AttributesReaper _attributesReaper = null;
	private ArrayList _attributesWithTimeout = null;

	private ApplicationContainer() {
		super();
		_attributesWithTimeout = new ArrayList();
		_attributesReaper = new AttributesReaper(this, REAPING_TIME);
		_attributesReaper.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				release();
			} // public void run()
		} // new Thread()
		);
	} // public ApplicationContainer()

	public synchronized CloneableObject cloneObject() {
		return null;
	} // public synchronized CloneableObject cloneObject()

	public static ApplicationContainer getInstance() {
		if (_instance == null) {
			synchronized (ApplicationContainer.class) {
				if (_instance == null) {
					_instance = new ApplicationContainer();
				} // if (_instance == null)
			} // synchronized(ApplicationContainer.class)
		} // if (_instance == null)
		return _instance;
	} // public static ApplicationContainer getInstance()

	protected Object oneStepGetAttribute(String key) {
		ApplicationContainerItem applicationContainerItem = getApplicationContainerItem(key);
		if (applicationContainerItem == null)
			return null;
		return applicationContainerItem.getValue();
	} // protected Object oneStepGetAttribute(String key)

	public synchronized Object getAttribute(String key) {
		return oneStepGetAttribute(key);
	} // public synchronized Object getAttribute(String key)

	public synchronized void setAttribute(String key, Object value) {
		ApplicationContainerItem applicationContainerItem = new ApplicationContainerItem(value,
				ApplicationContainerItem.INFINITE_TIMEOUT);
		super.setAttribute(key, applicationContainerItem);
	} // public synchronized void setAttribute(String key, Object value)

	public synchronized void setAttribute(String key, Object value, int timeout) {
		if (timeout <= 0) {
			setAttribute(key, value);
			return;
		} // if (timeout <= 0)
		ApplicationContainerItem applicationContainerItem = new ApplicationContainerItem(value, timeout);
		super.setAttribute(key, applicationContainerItem);
		_attributesWithTimeout.add(key);
	} // public synchronized void setAttribute(String key, Object value, long
		// timeout)

	public synchronized void delAttribute(String key) {
		super.delAttribute(key);
		_attributesWithTimeout.remove(key);
	} // public synchronized void delAttribute(String key)

	public void setContainer(ApplicationContainer container) {
		return;
	} // public void setContainer(ApplicationContainer container)

	public void setParent(ApplicationContainer container) {
		return;
	} // public void setParent(ApplicationContainer container)

	private ApplicationContainerItem getApplicationContainerItem(String key) {
		return (ApplicationContainerItem) super.oneStepGetAttribute(key);
	} // private ApplicationContainerItem getApplicationContainerItem(String
		// key)

	public synchronized void reapAttributes() {
		_logger.debug("ApplicationContainer::reapAttributes: invovato");

		long currentTimeMillis = System.currentTimeMillis();
		ArrayList attributesToRemove = new ArrayList();
		for (int i = 0; i < _attributesWithTimeout.size(); i++) {
			String key = (String) _attributesWithTimeout.get(i);
			ApplicationContainerItem applicationContainerItem = getApplicationContainerItem(key);
			long stale = currentTimeMillis - applicationContainerItem.getTimeout();
			if (applicationContainerItem.getLastUse() < stale) {
				_logger.debug("ApplicationContainer::reapAttributes: attributo [" + key + "] scaduto");

				attributesToRemove.add(key);
			} // if (applicationContainerItem.getLastUse() < stale)
		} // for (int i = 0; i < _attributesWithTimeout.size(); i++)
		for (int i = 0; i < attributesToRemove.size(); i++) {
			String key = (String) attributesToRemove.get(i);
			delAttribute(key);
		} // for (int i = 0; i < attributesToRemove.size(); i++)
	} // public synchronized void reapAttributes()

	public void release() {
		if (_attributesReaper != null) {
			_attributesReaper.terminate();
			_attributesReaper = null;
		} // if (_attributesReaper != null)
	} // public void release()

	public synchronized Element toElement(Document document) {
		String containerName = "APPLICATION_CONTAINER";
		Element applicationContainerElement = document.createElement(containerName);
		applicationContainerElement.appendChild(super.toElement(document));
		return applicationContainerElement;
	} // public synchronized Element toElement(Document document)
} // public class ApplicationContainer extends BaseContainer
