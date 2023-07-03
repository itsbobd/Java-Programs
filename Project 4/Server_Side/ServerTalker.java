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
public class ServerTalker 
{
    ServerTalker()
    {

    } 
     
     void send(String msg, DataOutputStream outStream) throws IOException
     {
        outStream.writeBytes(msg + '\n');
        System.out.println(">>" + msg);
     }

     String receive(BufferedReader inStream) throws IOException
     {
        String str;
        System.out.println("waiting to receive (ServerSide)");
        str = inStream.readLine();
        System.out.println("<<" + str);
        return str;
     }

   
    }
