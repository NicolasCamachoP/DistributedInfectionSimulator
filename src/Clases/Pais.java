package Clases;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
public class Pais extends Thread implements Serializable {

    private String nomPais;
    private long poblacion;
    private float porcentAislamiento;
    private float porcentajePoblaInfec;
    private float porcentPoblaVulne;
    public ArrayList<Pais> vecinosAereos;
    public ArrayList<Pais> vecinosTerrestres;
    public HashMap<String, Integer> puertosVecinos;
    public HashMap<String, String> ipVecinos;
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
        System.out.println("El país " + nomPais + " va a contactarse con sus vecinos...");
        System.out.println("Vecinos terrestres: " + vecinosTerrestres.size());
        System.out.println("Vecinos aereos: " + vecinosAereos.size());
        crearHiloEscucha();
        if (vecinosOk()) {
            System.out.println("El país " + nomPais + " esta corriendo...");
            cargar();
            cargar();
        } else {
            System.out.println("PaisOk fallido en " + this.nomPais);
        }

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
                        vecinosAereos.get(vecinosAereos.size() - 1).setNomPais(split[0]);
                        ipVecinos.put(split[0], split[1]);
                        puertosVecinos.put(split[0], Integer.valueOf(split[2]));
                        line = input.nextLine();
                    }
                    while (input.hasNextLine()) {
                        line = input.nextLine();
                        String[] split = line.split(";");
                        vecinosTerrestres.add(new Pais());
                        vecinosTerrestres.get(vecinosTerrestres.size() - 1).setNomPais(split[0]);
                        ipVecinos.put(split[0], split[1]);
                        puertosVecinos.put(split[0], Integer.valueOf(split[2]));
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
        System.out.println("Estado actualizado con " + this.getNomPais());
    }

    private boolean vecinosOk() {
        boolean vecinosAListos = false;
        boolean vecinosTListos = false;
        Pais auxVec;
        if (vecinosAereos.size() > 0) {
            for (Pais vecino : vecinosAereos) {
                vecinosAListos = false;
                while (!vecinosAListos) {
                    try {
                        Socket s = new Socket(ipVecinos.get(vecino.getNomPais()), puertosVecinos.get(vecino.getNomPais()));
                        out = new ObjectOutputStream(s.getOutputStream());
                        out.writeObject(new Mensaje(Tipo.OkRequestPais, null));
                        in = new ObjectInputStream(s.getInputStream());
                        Mensaje m = (Mensaje) in.readObject();
                        if (m.tipo == Tipo.OkreplyPais) {
                            vecinosAListos = true;
                            auxVec = (Pais) m.contenido;
                            System.out.println("El vecino terrestre: " + auxVec.getNomPais() + " - Registrado en " + nomPais);
                            vecino.setPoblacion(auxVec.poblacion);
                            vecino.setPorcentAislamiento(auxVec.porcentAislamiento);
                            vecino.setPorcentPoblaVulne(auxVec.porcentPoblaVulne);
                            vecino.setPorcentajePoblaInfec(auxVec.porcentajePoblaInfec);
                        }
                    } catch (IOException e) {
                        System.out.println("Vecino terrestre: " + vecino.nomPais + " - Esperando desde " + this.nomPais);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } else {
            vecinosAListos = true;
        }
        if (vecinosTerrestres.size() > 0) {
            for (Pais vecino : vecinosTerrestres) {
                vecinosTListos = false;
                while (!vecinosTListos) {
                    try {
                        Socket s = new Socket(ipVecinos.get(vecino.getNomPais()), puertosVecinos.get(vecino.getNomPais()));
                        out = new ObjectOutputStream(s.getOutputStream());
                        out.writeObject(new Mensaje(Tipo.OkRequestPais, null));
                        in = new ObjectInputStream(s.getInputStream());
                        Mensaje m = (Mensaje) in.readObject();
                        if (m.tipo == Tipo.OkreplyPais) {
                            vecinosTListos = true;
                            auxVec = (Pais) m.contenido;
                            System.out.println("El vecino terrestre: " + auxVec.getNomPais() + " - Registrado en " + nomPais);
                            vecino.setPoblacion(auxVec.poblacion);
                            vecino.setPorcentAislamiento(auxVec.porcentAislamiento);
                            vecino.setPorcentPoblaVulne(auxVec.porcentPoblaVulne);
                            vecino.setPorcentajePoblaInfec(auxVec.porcentajePoblaInfec);
                        }
                    } catch (IOException e) {
                        System.out.println("Vecino terrestre: " + vecino.nomPais + " - Esperando ...");
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } else
            vecinosTListos = true;

        if (vecinosAListos && vecinosTListos) {
            return true;
        }
        return false;
    }

    private boolean crearHiloEscucha() {
        Pais p = this;
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverS = new ServerSocket(puertoPaises);
                    System.out.println("Pais " + p.getNomPais() + " esuchando - CrearHiloEscucha");

                    while (true) {
                        try {
                            Socket clientSocket = serverS.accept();
                            System.out.println("Solicitud recibida - en Pais: " + p.getNomPais());
                            ConnectionP conP = new ConnectionP(clientSocket, p);
                        } catch (SocketTimeoutException e) {
                            System.out.println("Esuchando solicitudes");
                        }

                    }

                } catch (IOException ex) {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        });
        hiloEscucha.start();
        return true;
    }

}//end Pais

