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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aulasingenieria
 */
class ConnectionB extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    Broker broker;

    public ConnectionB(Socket aClientSocket, Broker b) 
    {
        try 
        {
            broker = b;
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
            if(m.tipo == Tipo.OkRequest)
            {
                m = new Mensaje(Tipo.OkReply, null);
                out.writeObject(m);
            }
            else if(m.tipo == Tipo.BalanceRequest)
            {
                m = new Mensaje(Tipo.BalanceReply, broker.cargaActual);
                out.writeObject(m);
            }
            else if(m.tipo == Tipo.BalanceLoad)
            {
                //TO DO
            }
            
        } 
        catch (EOFException e) 
        {
            System.out.println("EOF:" + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.out.println("readline:" + e.getMessage());
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
