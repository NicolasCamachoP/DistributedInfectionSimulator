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
class ConnectionP extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    Pais pais;

    public ConnectionP(Socket aClientSocket, Pais p) 
    {
        try 
        {
            pais = p;
            clientSocket = aClientSocket;
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.start();
        } 
        catch (IOException e) 
        {
            System.out.println("Connection:" + e.getMessage());
        }
    } // end Connection

    public void run() 
    {
        try 
        {	       
            Mensaje m = (Mensaje) in.readObject();
            sleep(100);
            if(m.tipo == Tipo.OkRequestPais)
            {
                Pais auxP = new Pais();
                auxP.setNomPais(pais.getNomPais());
                auxP.setPoblacion(pais.getPoblacion());
                auxP.setPorcentAislamiento(pais.getPorcentAislamiento());
                auxP.setPorcentPoblaVulne(pais.getPorcentPoblaVulne());
                auxP.setPorcentajePoblaInfec(pais.getPorcentajePoblaInfec());
                System.out.println("Solicitud recibida ConnectionP " + m.tipo+ " en: "+pais.getNomPais());
                m=new Mensaje(Tipo.OkreplyPais, auxP);
                out.writeObject(m);
                System.out.println("Mensaje replay enviado desde "+pais.getNomPais());
            }
        } 
        catch (EOFException e) 
        {
            System.out.println("EOF:" + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.out.println("readline en ConnectionP de "+pais.getNomPais()+" readline:" + e.getMessage());
            Logger.getLogger(ConnectionP.class.getName()).log(Level.SEVERE, null, e);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectionP.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally 
        {
            try 
            {
                clientSocket.close();
            } 
            catch (IOException e) 
            {
                /*close failed*/
            }
        }
    } // end run

} // end class Connection 

