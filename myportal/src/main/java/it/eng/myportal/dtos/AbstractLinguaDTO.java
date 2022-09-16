package it.eng.myportal.dtos;

/**
 * DTO astratto per le lingue
 * 
 * @author Rodi A.
 *
 */
public abstract class AbstractLinguaDTO extends AbstractUpdatablePkDTO implements IHasUniqueValue {
	private static final long serialVersionUID = 4525438726864856865L;

	private String codLingua;
	private String strLingua;
	private String codGradoLinguaLetto;
	private String strGradoLinguaLetto;
	private Integer numGradoLinguaLetto;
	private String codGradoLinguaScritto;
	private String strGradoLinguaScritto;
	private Integer numGradoLinguaScritto;
	private String codGradoLinguaParlato;
	private String strGradoLinguaParlato;
	private Integer numGradoLinguaParlato;
	protected Boolean madrelingua;

	public AbstractLinguaDTO() {
		super();
	}

	public String getCodGradoLinguaLetto() {
		return codGradoLinguaLetto;
	}

	public void setCodGradoLinguaLetto(String codGradoLingua) {
		this.codGradoLinguaLetto = codGradoLingua;
	}

	public String getStrGradoLinguaLetto() {
		return strGradoLinguaLetto;
	}

	public void setStrGradoLinguaLetto(String strGradoLingua) {
		this.strGradoLinguaLetto = strGradoLingua;
	}

	public String getCodLingua() {
		return codLingua;
	}

	public void setCodLingua(String codLingua) {
		this.codLingua = codLingua;
	}

	public String getStrLingua() {
		return strLingua;
	}

	public void setStrLingua(String strLingua) {
		this.strLingua = strLingua;
	}

	@Override
	public String getUniqueValue() {
		return codLingua;
	}

	/**
	 * @return the codGradoLinguaScritto
	 */
	public String getCodGradoLinguaScritto() {
		return codGradoLinguaScritto;
	}

	/**
	 * @param codGradoLinguaScritto
	 *            the codGradoLinguaScritto to set
	 */
	public void setCodGradoLinguaScritto(String codGradoLinguaScritto) {
		this.codGradoLinguaScritto = codGradoLinguaScritto;
	}

	/**
	 * @return the strGradoLinguaScritto
	 */
	public String getStrGradoLinguaScritto() {
		return strGradoLinguaScritto;
	}

	/**
	 * @param strGradoLinguaScritto
	 *            the strGradoLinguaScritto to set
	 */
	public void setStrGradoLinguaScritto(String strGradoLinguaScritto) {
		this.strGradoLinguaScritto = strGradoLinguaScritto;
	}

	public String getCodGradoLinguaParlato() {
		return codGradoLinguaParlato;
	}

	public void setCodGradoLinguaParlato(String codGradoLinguaParlato) {
		this.codGradoLinguaParlato = codGradoLinguaParlato;
	}

	public String getStrGradoLinguaParlato() {
		return strGradoLinguaParlato;
	}

	public void setStrGradoLinguaParlato(String strGradoLinguaParlato) {
		this.strGradoLinguaParlato = strGradoLinguaParlato;
	}

	/**
	 * @return the madrelingua
	 */
	public boolean isMadrelingua() {
		return madrelingua;
	}

	/**
	 * @param madrelingua
	 *            the madrelingua to set
	 */
	public void setMadrelingua(boolean madrelingua) {
		this.madrelingua = madrelingua;
	}

	public Integer getNumGradoLinguaLetto() {
		return numGradoLinguaLetto;
	}

	public void setNumGradoLinguaLetto(Integer numGradoLinguaLetto) {
		this.numGradoLinguaLetto = numGradoLinguaLetto;
	}

	public Integer getNumGradoLinguaScritto() {
		return numGradoLinguaScritto;
	}

	public void setNumGradoLinguaScritto(Integer numGradoLinguaScritto) {
		this.numGradoLinguaScritto = numGradoLinguaScritto;
	}

	public Integer getNumGradoLinguaParlato() {
		return numGradoLinguaParlato;
	}

	public void setNumGradoLinguaParlato(Integer numGradoLinguaParlato) {
		this.numGradoLinguaParlato = numGradoLinguaParlato;
	}

	@Override
	public String toString() {
		return String
				.format("VaLinguaDTO [codGradoLinguaLetto=%s, strGradoLinguaLetto=%s, codGradoLinguaScritto=%s, strGradoLinguaScritto=%s, codGradoLinguaParlato=%s, strGradoLinguaParlato=%s, madrelingua=%s, codLingua=%s, strLingua=%s]",
						codGradoLinguaLetto, strGradoLinguaLetto, codGradoLinguaScritto, strGradoLinguaScritto,
						codGradoLinguaParlato, strGradoLinguaParlato, isMadrelingua(), codLingua, strLingua);
	}

}