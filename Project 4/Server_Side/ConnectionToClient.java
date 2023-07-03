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
public class ConnectionToClient 
                            implements Runnable
{
    DataOutputStream outStream;
    BufferedReader inStream;
    Socket socket;
    ServerTalker talker;
    Vector<ConnectionToClient> clients;
    String clientUsername;
    ServerSide server;

    //============================================= Constructor
    ConnectionToClient(Socket s, ServerTalker t, Vector<ConnectionToClient> c, ServerSide server)
    {
        this.clients = c;
        this.talker = t;
        this.socket = s;
        this.server = server;

    } //end of Constructor

    public void run()
    {
        try
        {
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new DataOutputStream(socket.getOutputStream());

            //username check
            String incomingUsername = talker.receive(inStream);
            boolean notUsed = true;

            for (int i = 0; i < clients.size(); i++)
            {
                if (clients.elementAt(i).clientUsername.equals(incomingUsername))
                {
                    notUsed = false;
                }
            }

            if (notUsed)
            {
                clients.add(this); //add new client to list
                clientUsername = incomingUsername;
                talker.send("ALL_GOOD", outStream);

                server.broadcast(clientUsername + " has joined the group", this, outStream);
                
                String message;
                while (((message = talker.receive(inStream)) != null))
                {
                    server.broadcast(this.clientUsername + ": " + message, this, outStream);
                }

                server.removeUser(this);

                try
                {
                    socket.close();
                }
                
                catch(IOException ex)
                {
                    System.out.println("Error closing socket");
                }

            }

            else
            {
                talker.send("REPEAT", outStream);
            }

        }

        catch (IOException ex)
        {
            System.out.println("Problem with connecting to client");

            try
            {
                server.removeUser(this);
            }

            catch (IOException e)
            {
                System.out.println("telling other clients user has left");
            }

            
        }
    }

}