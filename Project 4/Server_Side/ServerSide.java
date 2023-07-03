package Server_Side;
//============================================

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.*;

//============================================
public class ServerSide 
{
    ServerSocket serverSocket;
    Socket normalSocket;
    Vector<ConnectionToClient> clients;

    ServerSide()
    {
        clients = new Vector<ConnectionToClient>(); //constructing client list

        try
        {
            serverSocket = new ServerSocket(12345);
            System.out.println("Server started on port 12345");
            
            while (true)
            {
                normalSocket = serverSocket.accept();
                System.out.println("A new client has connected");

                ConnectionToClient CTC = new ConnectionToClient(normalSocket, new ServerTalker(), clients, this);
                new Thread(CTC).start();
            }
        }

        catch(IOException ex)
        {
            System.out.println("Could not listen on port 12345");
        }
        
    }

    public void broadcast(String message, ConnectionToClient client, DataOutputStream outStream) throws IOException
    {
        for (int i = 0; i < clients.size(); i++)
        {
            if(clients.elementAt(i).clientUsername != client.clientUsername)
            {
                client.talker.send(message, clients.elementAt(i).outStream);
            }
        }
    }

    public void removeUser(ConnectionToClient CTC) throws IOException
    {
        clients.remove(CTC);
        broadcast(CTC.clientUsername + " has left the group", CTC, CTC.outStream);
    }
    
}
