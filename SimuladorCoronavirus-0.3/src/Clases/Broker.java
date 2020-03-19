package Clases;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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

    
    
    public static final String nameFile = "brokerFile.txt";
    public ArrayList<String> vecinosBrokers;
    public HashMap <String, String> paises;
    public int puertoBrokers;
    public int puertoPaises;
    public long maximaCarga;
    public long cargaActual;
    public ServerSocket serverS;
    public int paisesIniciales;
    public int paisesConectados;
    
    public ObjectOutputStream out;
    public ObjectInputStream in;
    
    
    
    public Broker() {
        vecinosBrokers = new ArrayList<>();
        paisesConectados = 0;
        leerArchivo();
        paises = new HashMap<>();
        crearHiloEscucha();
        this.start();
    }
    
    public static void main(String[] args) throws IOException {
        Broker br = new Broker();
//        if (br.addPais(new Pais("Colombia", 100000000))){
//            System.out.println("Pais agregado correctamente");
//        } else{
//            System.out.println("El sistema alcanzó la carga maxima");
//        }

    }
    
    public void run() 
    {
        iniciarOK();
    }
    
    private void iniciarOK() 
    {
        boolean  bandera = false;
        for (String v : vecinosBrokers) 
        {
            bandera = false;
            while(bandera == false)
            {
                try 
                {
                    Socket s = new Socket(v, puertoBrokers);
                    out = new ObjectOutputStream(s.getOutputStream());
                    out.writeObject(new Mensaje(Tipo.OkRequest, null));
                    in = new ObjectInputStream(s.getInputStream());
                    Mensaje m = (Mensaje) in.readObject();
                    if(m.tipo == Tipo.OkReply)
                    {
                        bandera = true;
                        System.out.println("Ip: " + v + " - Registrado ...");
                    }
                } 
                catch (IOException e) 
                {
                    System.out.println("Ip: " + v + " - Esperando ...");
                } 
                catch (ClassNotFoundException ex) {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }           
        }
        System.out.println("Todos los brokers estan en linea ...");
        
    }
    
    private void crearHiloEscucha()
    {
        Broker b = this;
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() 
            {
                try 
                {
                    serverS = new ServerSocket(puertoBrokers);
                    System.out.println("Broker Escuchando");
                    
                    while(true)
                    {
                        Socket clientSocket = serverS.accept();
                        ConnectionB c = new ConnectionB(clientSocket, b);
                    }
 
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        });
        hiloEscucha.start();
    }

    private void leerArchivo() {
        try {
            Scanner input = new Scanner(new File(nameFile));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.equals("maximaCarga:")) {
                    maximaCarga = input.nextLong();
                }else if (line.equals("puertoS:")) {
                    puertoBrokers = input.nextInt();
                } else if (line.equals("puertoP:")) {
                    puertoPaises = input.nextInt();
                }
                else if(line.equals("vecinosB:"))
                {
                    line = input.nextLine();
                    while (!line.equals("paises:"))
                    {
                         System.out.println(line);
                         vecinosBrokers.add(line);
                         line = input.nextLine();
                    }
                }
                else if(line.equals("paises:"))
                {
                    line = input.nextLine();
                    paisesIniciales = Integer.parseInt(line);
                }
            }
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean addPais(Pais pais) {
        //TODO
        return false;

    }
    
    

    private boolean balanceoCarga(Pais pais) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}//end Broker
