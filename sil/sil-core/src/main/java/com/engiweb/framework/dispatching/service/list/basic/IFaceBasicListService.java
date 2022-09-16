package com.engiweb.framework.dispatching.service.list.basic;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.paginator.basic.ListIFace;

public interface IFaceBasicListService {
	ListIFace getList(SourceBean request, SourceBean response) throws Exception;

	ListIFace getList();

	void setList(ListIFace list);

	void callback(SourceBean request, SourceBean response, ListIFace list, int page) throws Exception;

	boolean delete(SourceBean request, SourceBean response);
} // public interface IFaceBasicListService
