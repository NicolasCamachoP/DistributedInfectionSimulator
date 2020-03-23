/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author sistemas
 */
public class Servidor 
{
    static final int SERVER_PORT = 5498;
    
    public static void main (String args[]) 
    {
        Servidor s = new Servidor();   
    }
    
    private HashMap<ConnectionP, String > conexiones;
    
    public Servidor()
    {
        conexiones = new HashMap<>();
        
        try
        {
	      // the server port
	   ServerSocket listenSocket = new ServerSocket(SERVER_PORT);
           
            System.out.println("Servidor iniciado ...");

	   while(true) 
           {
                Socket clientSocket = listenSocket.accept();
              //  ConnectionP c = new ConnectionP(clientSocket, this);
                //conexiones.put(c, clientSocket.getInetAddress().getHostAddress());
	   }
	} 
        catch(IOException e) 
        {
            System.out.println("Listen socket:" +e.getMessage());
	}
    }
    
}
