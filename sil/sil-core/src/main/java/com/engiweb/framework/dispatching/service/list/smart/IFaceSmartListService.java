package com.engiweb.framework.dispatching.service.list.smart;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.paginator.smart.IFaceListProvider;

public interface IFaceSmartListService {
	IFaceListProvider getList(SourceBean request, SourceBean response) throws Exception;

	IFaceListProvider getList();

	void setList(IFaceListProvider list);

	void callback(SourceBean request, SourceBean response, IFaceListProvider list, int page) throws Exception;

	boolean delete(SourceBean request, SourceBean response);
} // public interface IFaceSmartListService
