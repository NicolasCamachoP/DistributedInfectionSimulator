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
class ConnectionP_B extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    Pais pais;

    public ConnectionP_B(Socket aClientSocket, Pais p) 
    {
        try 
        {
            this.pais = p;
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
            if(m.tipo == Tipo.QuestionPais)
            {
                if(pais.getNomPais().equals((String) m.contenido))
                {
                    m = new Mensaje(Tipo.ConfirmPais, null);
                    out.writeObject(m);
                    m = (Mensaje) in.readObject();
                    sleep(100);
                    if(m.tipo == Tipo.ChangePais)
                    {
                        Pais pn = (Pais) m.contenido;
                        pais.setNomPais(pn.getNomPais());
                        pais.setPoblacion(pn.getPoblacion());
                        pais.setPorcentAislamiento(pn.getPorcentAislamiento());
                        pais.setPorcentPoblaVulne(pn.getPorcentPoblaVulne());
                        pais.setPorcentajePoblaInfec(pn.getPorcentajePoblaInfec());
                        pais.setVecinosAereos(pn.getVecinosAereos());
                        pais.setVecinosTerrestres(pn.getVecinosTerrestres());
                    }
                }
            }
        } 
        catch (EOFException e) 
        {
            System.out.println("EOF:" + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.out.println("PBreadline:" + e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectionB.class.getName()).log(Level.SEVERE, null, ex);
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

