/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author sistemas
 */
public class ConnectionP extends Thread
{
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    Servidor servidor;

    public ConnectionP(Socket aClientSocket, Servidor s) 
    {
        try 
        {
            servidor = s;
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
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
            out.writeUTF("hola");
            /*String tipo = in.readUTF();
            
            if(tipo.equals("FuenteInfo"))
            {
                System.out.println("Registrado: Fuente de Información");
                String m = in.readUTF();
                String[] split = m.split("-");
                broker.mensaje = "1-" + split[1];
                System.out.println("Recivido de Fuente de Información: " + broker.mensaje);
                broker.hayMensaje = true;
                
                while(true)
                {
                    System.out.print("");
                    if(broker.ack == broker.clientes)
                    {
                        System.out.println("Enviando ack a Fuente de Información");
                        out.writeUTF("3-ACK");
                        System.out.println("Ack enviado");
                        break;
                    }
                }
            }
            else if(tipo.equals("Cliente"))
            {
                broker.clientes ++;
                System.out.println("Registrado: Cliente " + (broker.clientes));
                
                while(true)
                {
                    System.out.print("");
                    if(broker.hayMensaje)
                    {
                        out.writeUTF(broker.mensaje);
                        System.out.println("Mensaje enviado, esperando respuesta...");
                        System.out.println("Respuesta recivida: " + in.readUTF());
                        broker.ack++;
                        break;
                    }
                }  
            } */
            
        } 
        catch (EOFException e) 
        {
            System.out.println("EOF:" + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.out.println("readline:" + e.getMessage());
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

}
