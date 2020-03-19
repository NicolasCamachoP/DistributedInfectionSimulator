package Clases;

import static Clases.Broker.nameFile;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @version 1.0
 * @created 07-mar.-2020 9:30:50
 */
public class Pais extends Thread {

    private String nomPais;
    private long poblacion;
    private float porcentAislamiento;
    private float porcentajePoblaInfec;
    private float porcentPoblaVulne;
    private List<String> vecinosAereos;
    private List<String> vecinosTerrestres;

    public static void main(String[] args) throws IOException {
        String nFile = new String();
        Scanner scan = new Scanner(System.in);
        System.out.println("Ingrese el nombre del archivo para inicializar el país junto con la extension");
        nFile = scan.nextLine();
        System.out.println("Nombre " + nFile);
        Pais me = new Pais(nFile);
        

    }
    public Pais(String nFile) {
        vecinosAereos = new ArrayList();
        vecinosTerrestres = new ArrayList();
        leerArchivo(nFile);
        System.out.println("Vecinos terrestres: "+vecinosTerrestres.size());
        System.out.println("Vecinos aereos: "+vecinosAereos.size());
        crearHiloEscucha();
        this.start();
    }
    
     public void run() 
    {
        iniciarAgentReg();
    }

    private void leerArchivo(String nFile) {
        try {
            Scanner input = new Scanner(new File(nFile));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.equals("nombre:")) {
                    nomPais = input.nextLine();
                } else if (line.equals("poblacion:")) {
                    poblacion = input.nextLong();
                } else if (line.equals("porcentajeaislamiento:")) {
                    porcentAislamiento = input.nextFloat();
                }else if (line.equals("porcentajepoblacioninfectada:")) {
                    porcentajePoblaInfec = input.nextFloat();
                }else if (line.equals("porcentajepoblacionvulnerable:")) {
                    porcentPoblaVulne = input.nextFloat();
                } 
                else if (line.equals("vecinosaereo:")) {
                    line = input.nextLine();
                    while (!line.equals("vecinosterrestres:")) {
                        //System.out.println(line);
                        vecinosAereos.add(line);
                        line = input.nextLine();
                    }
                    while (input.hasNextLine()) {
                        line = input.nextLine();
                        //System.out.println(line);
                        vecinosTerrestres.add(line);
                    }
                }  
            }
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        return porcentAislamiento;
    }

    public void setPorcentAislamiento(float PorcentAislamiento) {
        this.porcentAislamiento = PorcentAislamiento;
    }

    public float getPorcentajePoblaInfec() {
        return porcentajePoblaInfec;
    }

    public void setPorcentajePoblaInfec(float PorcentajePoblaInfec) {
        this.porcentajePoblaInfec = PorcentajePoblaInfec;
    }

    public float getPorcentPoblaVulne() {
        return porcentPoblaVulne;
    }

    public void setPorcentPoblaVulne(float PorcentPoblaVulne) {
        this.porcentPoblaVulne = PorcentPoblaVulne;
    }

    public List<String> getVecinosAereos() {
        return vecinosAereos;
    }

    public void setVecinosAereos(List<String> VecinosAereos) {
        this.vecinosAereos = VecinosAereos;
    }

    public List<String> getVecinosTerrestres() {
        return vecinosTerrestres;
    }

    public void setVecinosTerrestres(List<String> VecinosTerrestres) {
        this.vecinosTerrestres = VecinosTerrestres;
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

    private void crearHiloEscucha() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void iniciarAgentReg() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}//end Pais
