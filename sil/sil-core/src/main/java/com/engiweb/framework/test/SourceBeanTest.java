package com.engiweb.framework.test;

import com.engiweb.framework.base.BaseContainer;

public class SourceBeanTest {
	public SourceBeanTest() {
	}

	public static void main(String[] args) {
		try {
			BaseContainer cont = new BaseContainer();
			cont.setAttribute("attr", "val");
			System.out.println("attr = " + cont.getAttribute("attr"));
			cont.delAttribute("attr");
			System.out.println("attr = " + cont.getAttribute("attr"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
