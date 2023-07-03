package Client_Side;
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
public class ConnectionToServer
{
    private DataOutputStream outStream;
    private BufferedReader inStream;
    ClientTalker talker;
    String username;
    Socket socket;
    ChatFrame frame;

    //============================================ Constructor
    ConnectionToServer(ClientTalker t, String u, Socket s)
    {
        this.talker = t;
        this.username = u;
        this.socket = s;

        try 
        {
            outStream = new DataOutputStream(socket.getOutputStream());
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            talker.send(username, outStream); //registers the user

            if(!(talker.receive(inStream).startsWith("REPEAT")))
            {
                frame = new ChatFrame(talker, outStream); //creates frame where users can start chatting

                Thread thread = new Thread(new Runnable() 
                {
                    public void run()
                    {
                        String incomingMessage = null;

                        try 
                        {
                            while ((incomingMessage = talker.receive(inStream)) != null)
                            {
                                frame.broadcastLabel.setText(incomingMessage); //sets the label to whatever is being broadcasted in
                            }
                        } 

                        catch (IOException ex) 
                        {
                            System.out.println("Error reading from server");
                        }
                    }
                });

                thread.start();
            }

            else
            {
                new GetUsernameFrame(); //ask the user for another username
                JOptionPane.showMessageDialog(null, "This username has already been taken!");
            }

        }

        catch (IOException ex)
        {
            System.out.println("Error connecting to server");
            JOptionPane.showMessageDialog(null, "Error connecting to server!");
        }





    } //End of Constructor
}