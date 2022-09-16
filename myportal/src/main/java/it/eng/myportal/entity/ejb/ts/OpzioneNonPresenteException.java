package it.eng.myportal.entity.ejb.ts;

import javax.ejb.ApplicationException;

import it.eng.myportal.entity.ts.TsOpzioneEnum;

@ApplicationException
public class OpzioneNonPresenteException extends Exception {

	public OpzioneNonPresenteException(TsOpzioneEnum which) {
		// TODO Auto-generated constructor stub
	}

}
