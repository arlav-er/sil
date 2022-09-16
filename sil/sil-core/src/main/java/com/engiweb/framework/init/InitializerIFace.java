package com.engiweb.framework.init;

import com.engiweb.framework.base.SourceBean;

public interface InitializerIFace {
	void init(SourceBean config);

	SourceBean getConfig();
} // public interface InitializerIFace
