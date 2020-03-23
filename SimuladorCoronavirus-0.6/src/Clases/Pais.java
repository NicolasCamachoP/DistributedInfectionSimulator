package Clases;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    private List<String> vecinosAereos;
    private List<String> vecinosTerrestres;
    private int puertoPais_Broker;
    private int puertoPaises;
    private ServerSocket serverS;
    private ServerSocket serverSB;
    private String ipBroker = "localhost";  
    public ObjectOutputStream out;
    public ObjectInputStream in;

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
        System.out.println("Vecinos terrestres: " + vecinosTerrestres.size());
        System.out.println("Vecinos aereos: " + vecinosAereos.size());
        this.start();
        
//        crearHiloEscuchaPaises();
        
    }

    private Pais(Pais p) {
        this.vecinosAereos = new ArrayList<>();
        this.vecinosTerrestres = new ArrayList<>();
        this.nomPais = p.getNomPais();
        this.poblacion = p.getPoblacion();
        this.porcentAislamiento = p.porcentAislamiento;
        this.porcentPoblaVulne = p.porcentPoblaVulne;
        this.porcentajePoblaInfec = p.porcentajePoblaInfec;
        this.vecinosAereos.addAll(p.getVecinosAereos());
        this.vecinosTerrestres.addAll(p.getVecinosTerrestres());
    }
    /*
    private void crearHiloEscuchaPaises() {
        Pais p = this;
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverS = new ServerSocket(puertoPaises);
                    System.out.println("Pais escuchando a los demas paises ...");

                    while (true) {
                        Socket clientSocket = serverS.accept();
                        ConnectionP c = new ConnectionP(clientSocket, p);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        hiloEscucha.start();
    }
    
    private void crearHiloEscuchaBrokers() 
    {
        Pais p = this;
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSB = new ServerSocket(puertoPais_Broker);
                    System.out.println("Pais escuchando a los demas paises ...");
                    
                    while (true) {
                        Socket clientSocket = serverSB.accept();
                        ConnectionP_B c = new ConnectionP_B(clientSocket, p);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        hiloEscucha.start();
    }
*/
    private boolean iniciarAgentReg(){
        Pais p = new Pais(this);
        boolean bandera = false;
        Socket s;
        System.out.println("Puerto pais - broker: " + puertoPais_Broker);
        while (bandera == false) 
        {
            try {
                s = new Socket(ipBroker, 6666);//puertoPais_Broker);
                out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject(new Mensaje(Tipo.agentRegistry, p));
//                s.setSoTimeout(1000);
                in = new ObjectInputStream(s.getInputStream());
                Mensaje m = (Mensaje) in.readObject();
//                sleep(100);
                if (m.tipo == Tipo.agentRConfirm)
                {
                    bandera = true;
                    System.out.println("Broker con IP: " + m.contenido + ", hizo agentConfirm");
                }
                in = new ObjectInputStream(s.getInputStream());
                m = (Mensaje) in.readObject();
                if (m.tipo == Tipo.ChangePais){
                    System.out.println("Estado actualizado con "+ this.getNomPais());
                    Pais pn = (Pais)m.contenido;
                    actualizarEstado(pn);
                }

            } catch (IOException e) {
                Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, e);
                System.out.println("Ip: " + ipBroker + " - Esperando ...");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, ex);
            } 
//            catch (InterruptedException ex) {
//                Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        return false;

    }

    public void run() {
        /*if(iniciarAgentReg())
            crearHiloEscuchaBrokers();*/
        iniciarAgentReg();
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
        this.vecinosAereos.addAll(VecinosAereos);
    }

    public List<String> getVecinosTerrestres() {
        return vecinosTerrestres;
    }

    public void setVecinosTerrestres(List<String> VecinosTerrestres) {
        this.vecinosTerrestres.addAll(VecinosTerrestres);
    }

    public void cargar() {

        long a = 123495872;
        long b = 1239852;
        long c = 0;
        // Call an expensive task, or sleep if you are monitoring a remote process
        for (double i = 0; i < 2000000000; i++) {
            /*try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Pais.class.getName()).log(Level.SEVERE, null, ex);
            }*/
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
