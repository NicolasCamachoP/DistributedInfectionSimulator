package Clases;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sistemas
 * @version 1.0
 * @created 07-mar.-2020 9:26:29
 */
public class Broker extends Thread {

    public static final String NAMEFILE = "brokerFile.txt";
    public ArrayList<String> vecinosBrokers;

    /**
     *
     */
    public HashMap<String, ConnPais> paises;
    public ArrayList<String> archivosPaises;
    public ArrayList<Integer> puertosPaises;
    public ArrayList<Pais> paisesRegistrados;
    public int puertoBrokers;
    public int puertoPaises;
    public long maximaCarga;
    public long cargaActual;
    public ServerSocket serverS;
    public ServerSocket serverSP;
    public int paisesNecesarios;
    public int paisesConectados;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    public String ip;
    boolean siBrokersListos = false;
    boolean siPaisesListos = false;
    boolean balanceoCompletado = true;
    boolean ocupado = false;

    public Broker() {
        paises = new HashMap<>();
        archivosPaises = new ArrayList<>();
        puertosPaises = new ArrayList<>();
        paisesRegistrados = new ArrayList<>();
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
        cargaActual = 0;
        vecinosBrokers = new ArrayList<>();
        paisesConectados = 0;
        
        
        
        leerArchivo();
        this.start();
        //iniciarPReg();
        crearHiloEscucha();
        //balanceoCarga();
        //System.out.println("Puerto Brokers: " + this.puertoBrokers);
        //System.out.println("Puerto Paises: " + this.puertoPaises);
    }

    public static void main(String[] args) throws IOException {
        Broker br = new Broker();
    }

    public void run() {
        if(iniciarOK()){
            inicializarPaises();
        }

    }
    /*
    private void iniciarPReg() {
        Broker b = this;
        System.out.println(this.paises.size());
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSP = new ServerSocket(puertoPaises);
                    System.out.println("Broker Escuchando Paises ...");
                    int numPaises = 0;
                    while (numPaises < paisesNecesarios) {
                        try {
                            //serverSP.setSoTimeout(500);
                            Socket clientSocket = serverSP.accept();

                            System.out.println("Solicitud recibida - País");
                            ConnectionB_P c = new ConnectionB_P(clientSocket, b);
                            numPaises++;
                        } catch (SocketTimeoutException e) {
                            System.out.println("Esperando países");
                        }
                    }
                    siPaisesListos = true;
                    System.out.println("Todos los paises conectados ...");
                    System.out.println(b.paises.size());

                } catch (IOException ex) {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        });
        hiloEscucha.start();
    }*/

    public void sumarPoblacionTotal() {
        for (ConnPais value : paises.values()) {
            this.cargaActual += value.pais.getPoblacion();
        }
    }

    private boolean iniciarOK() {
        boolean bandera = false;
        for (String v : vecinosBrokers) {
            bandera = false;
            while (bandera == false) {
                try {
                    Socket s = new Socket(v, puertoBrokers);
                    out = new ObjectOutputStream(s.getOutputStream());
                    out.writeObject(new Mensaje(Tipo.OkRequest, null));
                    in = new ObjectInputStream(s.getInputStream());
                    Mensaje m = (Mensaje) in.readObject();
                    if (m.tipo == Tipo.OkReply) {
                        bandera = true;
                        System.out.println("Ip: " + v + " - Registrado ...");
                    }
                } catch (IOException e) {
                    System.out.println("Ip: " + v + " - Esperando ...");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        siBrokersListos = true;
        System.out.println("Todos los brokers estan en linea ...");
        return true;

    }

    private boolean crearHiloEscucha() {
        Broker b = this;
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverS = new ServerSocket(puertoBrokers);
                    System.out.println("Broker Escuchando - CrearHiloEscucha");

                    while (true) {
                        try {
                            //serverS.setSoTimeout(500);
                            Socket clientSocket = serverS.accept();
                            System.out.println("Solicitud recibida - Broker");
                            ConnectionB c = new ConnectionB(clientSocket, b);
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

    private void leerArchivo() {
        try {
            Scanner input = new Scanner(new File(NAMEFILE));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.equals("maximaCarga:")) {
                    maximaCarga = input.nextLong();
                } else if (line.equals("puertoB:")) {
                    puertoBrokers = input.nextInt();
                } else if (line.equals("puertoP:")) {
                    puertoPaises = input.nextInt();
                } else if (line.equals("vecinosB:")) {
                    line = input.nextLine();
                    while (!line.equals("paises:")) {
                        System.out.println(line);
                        vecinosBrokers.add(line);
                        line = input.nextLine();
                    }
                    if (line.equals("paises:")) {
                        while (input.hasNext()) {
                            line = input.nextLine();
                            archivosPaises.add(line);
                        }
                    }
                }
            }
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void balanceoCarga() {
        System.out.println("1");
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(siBrokersListos && siPaisesListos)) {
                    System.out.print("");
                }
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
                }
                sumarPoblacionTotal();
                System.out.println("Salió" + "-" + cargaActual + "-" + maximaCarga);
                balanceoCompletado = false;
                if (cargaActual > maximaCarga) {
                    System.out.println("2");
                    ArrayList<ConnPais> aux = new ArrayList<>();
                    aux.addAll(paises.values());
                    Comparator<ConnPais> comparador = new Comparator<ConnPais>() {
                        @Override
                        public int compare(ConnPais t, ConnPais t1) {
                            return (int) (t.pais.getPoblacion() - t1.pais.getPoblacion());
                        }
                    };
                    Collections.sort(aux, comparador);

                    Pais pesado = aux.get(aux.size() - 1).pais;

                    protocoloBalanceo(pesado);

                }
            }
        });

        hilo.start();

    }

    public void protocoloBalanceo(Pais p) {
        System.out.println("3");
        boolean bandera = false;
        Pais aux = null;
        for (String v : vecinosBrokers) {
            bandera = false;
            while (bandera == false) {
                try {
                    Socket s = new Socket(v, puertoBrokers);
                    out = new ObjectOutputStream(s.getOutputStream());
                    out.writeObject(new Mensaje(Tipo.BalanceRequest, p.getNomPais() + ";" + p.getPoblacion() + ";" + cargaActual + ";" + maximaCarga));
                    in = new ObjectInputStream(s.getInputStream());
                    Mensaje m = (Mensaje) in.readObject();
                    bandera = true;
                    System.out.println("Recibí país liviano...");
                    if (m.tipo == Tipo.BalanceReply && m.contenido != null) {
                        balanceoCompletado = true;
                        aux = (Pais) m.contenido;
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("readline:" + e.getMessage());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            if (balanceoCompletado) {
                try {
                    System.out.println("Voy a actualizar estados...");
                    Socket s = new Socket(v, puertoBrokers);
                    out = new ObjectOutputStream(s.getOutputStream());
                    Mensaje m = new Mensaje(Tipo.BalanceLoad, new DTOPaises(aux, p));
                    intercambiar(p, aux);
                    out.writeObject(m);
                    System.out.println("Protocolo balanceo en broker completado...");
                    break;
                } catch (IOException e) {
                    System.out.println("readline:" + e.getMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        }

        for (String v : vecinosBrokers) {
            try {
                Socket s = new Socket(v, puertoBrokers);
                out = new ObjectOutputStream(s.getOutputStream());
                Mensaje m = new Mensaje(Tipo.BalanceFinished, null);
                out.writeObject(m);

            } catch (IOException e) {
                System.out.println("readline:" + e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public Pais verificacionBalanceRequest(String cadena) {

        String[] split = cadena.split(";");
        String nomPais = split[0];
        Long poblacionPais = Long.parseLong(split[1]);
        Long cargaActualSol = Long.parseLong(split[2]);
        Long maximaCargaSol = Long.parseLong(split[3]);
        ArrayList<ConnPais> aux = new ArrayList<>();
        aux.addAll(paises.values());
        Comparator<ConnPais> comparador = new Comparator<ConnPais>() {
            @Override
            public int compare(ConnPais t, ConnPais t1) {
                return (int) (t.pais.getPoblacion() - t1.pais.getPoblacion());
            }
        };
        Collections.sort(aux, comparador);
        Pais liviano = aux.get(0).pais;
        Long min = liviano.getPoblacion();

        if ((cargaActual - min + poblacionPais) > maximaCarga) {
            System.out.println("No puedo aceptar el balanceo...");
            return null;
        } else {
            if ((cargaActualSol - poblacionPais + min) > maximaCargaSol) {
                System.out.println("El puedo aceptar el balanceo...");
                return null;
            } else {
                System.out.println("Si puedo aceptar el balanceo...");
                return liviano;
            }
        }
    }

    public void intercambiar(Pais pv, Pais pn) {

        try {
            ConnectionB_P connect = paises.get(pv.getNomPais()).connection;
            System.out.println("Actualizano estado en " + pv.getNomPais() + " a " + pn.getNomPais());
            connect.actualizarEstado(pn);
            paises.remove(pv.getNomPais());
            paises.put(pn.getName(), new ConnPais(pn, connect));
            System.out.println("Mapa con estado acutualizado...");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void inicializarPaises() {
        Pais auxP;
        for (String nomArchivo : archivosPaises) {
            auxP = new Pais(nomArchivo);
            paisesRegistrados.add(auxP);
            puertosPaises.add(auxP.getPuertoPaises());
        }
    }

}//end Broker
