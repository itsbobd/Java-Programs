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
    DataOutputStream outStream;
    BufferedReader inStream;

    ServerSide()
    {
        try
        {
            serverSocket = new ServerSocket(12345);      
        }

        catch(IOException ex)
        {
            System.out.println("Could not listen on port 12345");
        }

        try
        {
            normalSocket = serverSocket.accept();
            inStream = new BufferedReader(new InputStreamReader(normalSocket.getInputStream()));
            outStream = new DataOutputStream(normalSocket.getOutputStream());
        }

        catch(IOException ex)
        {
            System.out.println(".accept failed");
        }
        
        
    }
    
}
