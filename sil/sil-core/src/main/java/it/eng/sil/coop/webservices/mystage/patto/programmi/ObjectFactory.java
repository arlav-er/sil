//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.11.20 at 04:04:51 PM CET 
//

package it.eng.sil.coop.webservices.mystage.patto.programmi;

import javax.xml.bind.annotation.XmlRegistry;

import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti.ProgrammaAperto;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti.ProgrammaAperto.PoliticheAttive;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti.ProgrammaAperto.PoliticheAttive.PoliticaAttiva;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.SchedaPartecipantePatto;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.SchedaPartecipantePatto.SvantaggiFse;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.SchedaPartecipante;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.SchedaPartecipante.Svantaggi;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * it.eng.sil.coop.webservices.mystage.patto.programmi package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
	 * it.eng.sil.coop.webservices.mystage.patto.programmi
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link PoliticaAttiva }
	 * 
	 */
	public PoliticaAttiva createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiApertiProgrammaApertoPoliticheAttivePoliticaAttiva() {
		return new PoliticaAttiva();
	}

	/**
	 * Create an instance of {@link ProgrammiApertiPattoLavoratore }
	 * 
	 */
	public ProgrammiApertiPattoLavoratore createProgrammiApertiPattoLavoratore() {
		return new ProgrammiApertiPattoLavoratore();
	}

	/**
	 * Create an instance of {@link SchedaPartecipantePatto }
	 * 
	 */
	public SchedaPartecipantePatto createProgrammiApertiPattoLavoratoreProfilingPattoSchedaPartecipantePatto() {
		return new SchedaPartecipantePatto();
	}

	/**
	 * Create an instance of {@link ProfilingPatto }
	 * 
	 */
	public ProfilingPatto createProgrammiApertiPattoLavoratoreProfilingPatto() {
		return new ProfilingPatto();
	}

	/**
	 * Create an instance of {@link PoliticheAttive }
	 * 
	 */
	public PoliticheAttive createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiApertiProgrammaApertoPoliticheAttive() {
		return new PoliticheAttive();
	}

	/**
	 * Create an instance of {@link Svantaggi }
	 * 
	 */
	public Svantaggi createProgrammiApertiPattoLavoratoreSchedaPartecipanteSvantaggi() {
		return new Svantaggi();
	}

	/**
	 * Create an instance of {@link ProgrammiAperti }
	 * 
	 */
	public ProgrammiAperti createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiAperti() {
		return new ProgrammiAperti();
	}

	/**
	 * Create an instance of {@link ProgrammaAperto }
	 * 
	 */
	public ProgrammaAperto createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiApertiProgrammaAperto() {
		return new ProgrammaAperto();
	}

	/**
	 * Create an instance of {@link SvantaggiFse }
	 * 
	 */
	public SvantaggiFse createProgrammiApertiPattoLavoratoreProfilingPattoSchedaPartecipantePattoSvantaggiFse() {
		return new SvantaggiFse();
	}

	/**
	 * Create an instance of {@link SchedaPartecipante }
	 * 
	 */
	public SchedaPartecipante createProgrammiApertiPattoLavoratoreSchedaPartecipante() {
		return new SchedaPartecipante();
	}

}
