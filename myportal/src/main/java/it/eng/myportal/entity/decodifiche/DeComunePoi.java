package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_comune_poi", schema = "mycas")
@NamedQueries({ @NamedQuery(name = "findComuneByCodComune", query = "select d from DeComunePoi d where d.deComune = (:input)") })
public class DeComunePoi extends AbstractDecodeEntity implements java.io.Serializable {


     private Integer idDeComunePoi;
     private DeComune deComune;
     private Float lat;
     private Float lon;
     private Integer numPopolazione;
     private String altitudine;
     private String codeIstat;

    public DeComunePoi() {

    }

    @Id
    @Column(name = "id_de_comune_poi", unique = true, nullable = false)
    public Integer getIdDeComunePoi() {
        return idDeComunePoi;
    }
    public void setIdDeComunePoi(Integer idDeComunePoi) {
        this.idDeComunePoi = idDeComunePoi;
    }

    @Column(name = "lat", nullable = false, length = 17)
    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    @Column(name = "lon", nullable = false, length = 17)
    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    @Column(name = "num_popolazione", length = 10)
    public Integer getNumPopolazione() {
        return numPopolazione;
    }

    public void setNumPopolazione(Integer numPopolazione) {
        this.numPopolazione = numPopolazione;
    }

    @Column(name = "altitude", length = 10)
    public String getAltitudine() {
        return altitudine;
    }

    public void setAltitudine(String altitudine) {
        this.altitudine = altitudine;
    }

    @Column(name = "code_istat", length = 6)
    public String getCodeIstat() {
        return codeIstat;
    }

    public void setCodeIstat(String codeIstat) {
        this.codeIstat = codeIstat;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_comune_poi", unique = true, nullable = false)
    public DeComune getDeComune() {
        return deComune;
    }

    public void setDeComune(DeComune deComune) {
        this.deComune = deComune;
    }
}