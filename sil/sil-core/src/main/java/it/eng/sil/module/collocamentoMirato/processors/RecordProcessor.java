package it.eng.sil.module.collocamentoMirato.processors;

import java.util.Map;

import com.engiweb.framework.base.SourceBean;

public interface RecordProcessor {
	public SourceBean processRecord(Map record) throws Exception;
}