package Clases;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @created 07-mar.-2020 9:30:50
 */
public class Pais implements Serializable {

    private String nomPais;
    private long poblacion;
    private float PorcentAislamiento;
    private float PorcentajePoblaInfec;
    private float PorcentPoblaVulne;
    private List<Pais> VecinosAereos;
    private List<Pais> VecinosTerrestres;

    public Pais(String nombre, long poblacion) {
        this.nomPais = nombre;
        this.poblacion = poblacion;
    }

    public String getNomPais() {
        return nomPais;
    }

    public void setNomPais(String nomPais) {
        this.nomPais = nomPais;
    }

    public long getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(long Poblacion) {
        this.poblacion = Poblacion;
    }

    public float getPorcentAislamiento() {
        return PorcentAislamiento;
    }

    public void setPorcentAislamiento(float PorcentAislamiento) {
        this.PorcentAislamiento = PorcentAislamiento;
    }

    public float getPorcentajePoblaInfec() {
        return PorcentajePoblaInfec;
    }

    public void setPorcentajePoblaInfec(float PorcentajePoblaInfec) {
        this.PorcentajePoblaInfec = PorcentajePoblaInfec;
    }

    public float getPorcentPoblaVulne() {
        return PorcentPoblaVulne;
    }

    public void setPorcentPoblaVulne(float PorcentPoblaVulne) {
        this.PorcentPoblaVulne = PorcentPoblaVulne;
    }

    public List<Pais> getVecinosAereos() {
        return VecinosAereos;
    }

    public void setVecinosAereos(List<Pais> VecinosAereos) {
        this.VecinosAereos = VecinosAereos;
    }

    public List<Pais> getVecinosTerrestres() {
        return VecinosTerrestres;
    }

    public void setVecinosTerrestres(List<Pais> VecinosTerrestres) {
        this.VecinosTerrestres = VecinosTerrestres;
    }

    public void finalize() throws Throwable {

    }

    public void cargar() {

        long a = 123495872;
        long b = 1239852;
        long c = 0;
        // Call an expensive task, or sleep if you are monitoring a remote process
        for (double i = 0; i < 10000000; i++) {
            c += a / b;
            c += c * b;
        }

    }
}//end Pais
