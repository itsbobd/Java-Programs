package Client_Side;
//============================================

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Vector;

//============================================
public class BabyServer 
                        implements Runnable
{
    ServerSocket serverSocket;
    Socket normalSocket;
    int port;
    Vector<ClientHandlerFileReceive> clientHandlers;
    UserFrame frame;

//============================================ Constructor

BabyServer(int port)
{
    this.port = port;
    clientHandlers = new Vector<ClientHandlerFileReceive>();
}

public void run()
{
    try
    {
        serverSocket = new ServerSocket(port);
        System.out.println("Baby Server started on port " + port);

        do 
        {
            normalSocket = serverSocket.accept();
            System.out.println("The sender is attempting to connect");

            ClientHandlerFileReceive clientHandlerFileReceive = new ClientHandlerFileReceive(normalSocket, clientHandlers);
            clientHandlers.add(clientHandlerFileReceive);
            new Thread(clientHandlerFileReceive).start();
        }
        while (!clientHandlers.isEmpty()); //stops waiting for clients when last client has left
    }

    catch(IOException ex)
    {
        System.out.println("Could not listen on port " + port);
    }

    try
    {
        serverSocket.close();
        System.out.println("Baby Server shut down");
    }
        
    catch(IOException ex)
    {
        System.out.println("Error closing Baby Server");
    }
}

public int createRandomPort()
{
    Random rand = new Random();

    return rand.nextInt((65535 - 1024) + 1) + 1024;
}


}