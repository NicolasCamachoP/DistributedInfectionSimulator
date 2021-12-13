/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aulasingenieria
 */
class ConnectionB_P extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    Broker broker;
    boolean siActualiza = false;
    Pais paisCambio = null;

    public ConnectionB_P(Socket aClientSocket, Broker b) {
        try {
            broker = b;
            clientSocket = aClientSocket;
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    } // end Connection

    public void run() {
        try {
            Mensaje m = (Mensaje) in.readObject();
            sleep(100);
            if (m.tipo == Tipo.agentRegistry) {
                Pais aux = (Pais) m.contenido;
                broker.paises.put(aux.getNomPais(), new ConnPais(aux, this));
                m = new Mensaje(Tipo.agentRConfirm, broker.ip);
                out.writeObject(m);
                out.flush();
                broker.paisesConectados++;
                System.out.println("Pais Registrado en broker local ");
                while (true) {
                    //Cambio de estado
                    System.out.print("");
                    if (siActualiza && paisCambio != null) {
                        out.writeObject(new Mensaje(Tipo.ChangePais, paisCambio));
                        out.flush();
                        System.out.println("Cambio de estado realizado...");
                        siActualiza = false;
                        paisCambio = null;
                    }
                }

            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("PBreadline:" + e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectionB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                /*close failed*/
            }
        }
    } // end run

    void actualizarEstado(Pais pn) {
        System.out.println("Atributos modificados por balanceo en ConnectionB_P...");
        System.out.println("pn es "+ pn.getNomPais());
        paisCambio = pn;
        siActualiza = true;
    }

} // end class Connection 

