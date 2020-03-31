package Clases;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version 1.0
 * @created 07-mar.-2020 9:30:50
 */
public class Pais extends Thread implements Serializable{

    private String nomPais;
    private long poblacion;
    private float porcentAislamiento;
    private float porcentajePoblaInfec;
    private float porcentPoblaVulne;
    public ArrayList<Pais> vecinosAereos;
    public ArrayList<Pais> vecinosTerrestres;
    public HashMap<String, Integer> puertosVecinos;
    public HashMap<String,String> ipVecinos;
    private int puertoPais_Broker;
    private int puertoPaises;
    private ServerSocket serverS;
    private ServerSocket serverSB;
    private String ipBroker = "localhost";  
    public ObjectOutputStream out;
    public ObjectInputStream in;

    public Pais(String nFile) {
        vecinosAereos = new ArrayList();
        vecinosTerrestres = new ArrayList();
        puertosVecinos = new HashMap<>();
        ipVecinos = new HashMap<>();
        leerArchivo(nFile);
        this.start();
    }

    private Pais(Pais p) {
        this.vecinosAereos = new ArrayList();
        this.vecinosTerrestres = new ArrayList();
        this.puertosVecinos = new HashMap<>();
        this.ipVecinos = new HashMap<>();
        this.nomPais = p.getNomPais();
        this.poblacion = p.getPoblacion();
        this.porcentAislamiento = p.porcentAislamiento;
        this.porcentPoblaVulne = p.porcentPoblaVulne;
        this.porcentajePoblaInfec = p.porcentajePoblaInfec;
        this.vecinosAereos.addAll(p.getVecinosAereos());
        this.vecinosTerrestres.addAll(p.getVecinosTerrestres());
        this.ipVecinos.putAll(p.ipVecinos);
        this.puertosVecinos.putAll(p.puertosVecinos);
    }

    Pais() {
        
    }
    /*
    private boolean iniciarAgentReg(){
        Pais p = new Pais(this);
        boolean bandera = false;
        Socket s;
        System.out.println("Puerto pais - broker: " + puertoPais_Broker);
        while (bandera == false) 
        {
            try {
                s = new Socket(ipBroker,puertoPais_Broker);
                out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject(new Mensaje(Tipo.agentRegistry, p));
                in = new ObjectInputStream(s.getInputStream());
                Mensaje m = (Mensaje) in.readObject();
                if (m.tipo == Tipo.agentRConfirm)
                {
                    bandera = true;
                    System.out.println("Broker con IP: " + m.contenido + ", hizo agentConfirm");
                }
                System.out.println("Esperando siguiente mensaje");
                m = (Mensaje) in.readObject();
                System.out.println("Recibi esto "+ m.tipo);
                if (m.tipo == Tipo.ChangePais){
                    System.out.println("Actualizando estado de "+ this.getNomPais());
                    Pais pn = (Pais)m.contenido;
                    actualizarEstado(pn);
                }

            } catch (IOException e) {
                Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, e);
                System.out.println("Ip: " + ipBroker + " - Esperando ...");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        return false;

    }*/

    public void run() {
        //iniciarAgentReg();
        System.out.println("El país "+ nomPais + " esta corriendo...");
        System.out.println("Vecinos terrestres: " + vecinosTerrestres.size());
        System.out.println("Vecinos aereos: " + vecinosAereos.size());
        cargar();
        cargar();
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
                } else if (line.equals("porcentajepoblacioninfectada:")) {
                    porcentajePoblaInfec = input.nextFloat();
                } else if (line.equals("porcentajepoblacionvulnerable:")) {
                    porcentPoblaVulne = input.nextFloat();
                } else if (line.equals("puertopaisBroker:")) {
                    puertoPais_Broker = input.nextInt();
                } else if (line.equals("puertopaises:")) {
                    puertoPaises = input.nextInt();
                } else if (line.equals("vecinosaereo:")) {
                    line = input.nextLine();
                    while (!line.equals("vecinosterrestres:")) {
                        String[] split = line.split(";");
                        vecinosAereos.add(new Pais());
                        vecinosAereos.get(vecinosAereos.size()-1).setNomPais(split[0]);
                        ipVecinos.put(split[0], split[1]);
                        puertosVecinos.put(split[0],Integer.valueOf(split[2]));
                        line = input.nextLine();
                    }
                    while (input.hasNextLine()) {
                        line = input.nextLine();
                        String[] split = line.split(";");
                        vecinosTerrestres.add(new Pais());
                        vecinosTerrestres.get(vecinosTerrestres.size()-1).setNomPais(split[0]);
                        ipVecinos.put(split[0], split[1]);
                        puertosVecinos.put(split[0],Integer.valueOf(split[2]));
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

    public int getPuertoPaises() {
        return puertoPaises;
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

    public ArrayList<Pais> getVecinosAereos() {
        return vecinosAereos;
    }

    public void setVecinosAereos(ArrayList<Pais> VecinosAereos) {
        this.vecinosAereos.addAll(VecinosAereos);
    }

    public ArrayList<Pais> getVecinosTerrestres() {
        return vecinosTerrestres;
    }

    public void setVecinosTerrestres(ArrayList<Pais> VecinosTerrestres) {
        this.vecinosTerrestres.addAll(VecinosTerrestres);
    }

    public void cargar() {

        long a = 123495872;
        long b = 1239852;
        long c = 0;
        // Call an expensive task, or sleep if you are monitoring a remote process
        for (double i = 0; i < 2000000000; i++) {
           
            c += a / b;
            c += c * b;
        }

    }

    private void actualizarEstado(Pais p) {
        this.nomPais = p.getNomPais();
        this.poblacion = p.getPoblacion();
        this.porcentAislamiento = p.porcentAislamiento;
        this.porcentPoblaVulne = p.porcentPoblaVulne;
        this.porcentajePoblaInfec = p.porcentajePoblaInfec;
        this.vecinosAereos.addAll(p.getVecinosAereos());
        this.vecinosTerrestres.addAll(p.getVecinosTerrestres());
        System.out.println("Estado actualizado con "+ this.getNomPais());
    }

}//end Pais
