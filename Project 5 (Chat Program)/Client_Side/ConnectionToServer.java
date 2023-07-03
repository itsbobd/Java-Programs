package Client_Side;
//============================================

import java.awt.*;
import javax.net.ssl.SSLSocket;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

//============================================
public class ConnectionToServer
                    implements Runnable
{
    boolean logOff = false;
    BabyServer babyServer;
    ClientTalker talker;
    String username;
    String password;
    Socket socket;
    Boolean isRegister;
    LoginDialog dialog;
    FileInputStream fileInput;
    FileOutputStream fileOutput;
    Properties properties;
    File file;
    String loginMessage;
    String friendsListMessage;
    UserFrame frame;
    String online = new String(Character.toChars(0x2705));
    String offline = new String(Character.toChars(0x274C));
    FileInputStream inFileStream;
    DataOutputStream netOutStream;
    final int BLOCKSIZE = 64;
    int port;

    SSLSocket sslSocket;

    private OutputStream        outStream;
    private DataOutputStream    dos;

    private InputStream         inStream;
    private BufferedReader      reader;

    //============================================ Constructor
    ConnectionToServer(ClientTalker t, String u, String p, SSLSocket s, Boolean ir, LoginDialog log)
    {                                            
        talker = t;
        username = u;
        password = p;
        sslSocket = s;
        isRegister = ir;
        dialog = log;
        file = dialog.file;
        frame = dialog.frame;
    }    

    public void run()
    {
        try 
        {
            sslSocket.startHandshake();
            System.out.println("Have gotten a SSLSocket...");


            outStream = sslSocket.getOutputStream();
            dos = new DataOutputStream(outStream);

            inStream = sslSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inStream));

            System.out.println("Have gotten secure streams...");

            if (isRegister)
            {
                send("REGISTER " + username + " " + password);

                if (!(receive(reader).startsWith("BAD")))
                {
                    if (dialog.rememberMeBox.isSelected()) //if the remember box is selected
                    {
                        dialog.prop.setProperty("username", username); // sets the new property defaults for user login
                        dialog.prop.setProperty("password", password);

                        dialog.outstream = new FileOutputStream("localUser.properties", false); //empties file
                        
                        dialog.prop.store(dialog.outstream, ""); //stores them into the file
                    }
                    
                    chat(); //starts communicating with client
                }

                else
                    JOptionPane.showMessageDialog(dialog, "Either your username or password is not useable");

            }

            else //probably a login
            {
                send("LOGIN " + username + " " + password);

                if ((loginMessage = receive(reader)).startsWith("GOOD"))
                {
                    if (dialog.rememberMeBox.isSelected()) //if the remember box is selected
                    {
                        dialog.prop.setProperty("username", username); // sets the new property defaults for user login
                        dialog.prop.setProperty("password", password);

                        dialog.outstream = new FileOutputStream("localUser.properties", false); //empties file
                        
                        dialog.prop.store(dialog.outstream, ""); //stores them into the file
                    }

                    if ((friendsListMessage = receive(reader)).startsWith("FRIENDS_LIST"))
                    {
                        if (!friendsListMessage.isEmpty())
                        {
                            parseFriendsList(friendsListMessage); //retreives friends list from server
                        }
                    }

                    else
                    {
                        JOptionPane.showMessageDialog(dialog, "There was an error while trying to obtain your friends list");
                    }
                    
                    chat(); //starts communicating with client
                }
                
                else if (loginMessage.startsWith("BAD_CRED"))
                {
                    JOptionPane.showMessageDialog(dialog, "Either your username or password is incorrect");
                }
                        
                else if (loginMessage.startsWith("ONLINE"))
                {
                    JOptionPane.showMessageDialog(dialog, "This user is already logged in on another client");
                }
                        
                else
                {
                    JOptionPane.showMessageDialog(dialog, "Something went wrong when trying to log in"); 
                }           
            }    
        }

        catch (IOException ex)
        {
            System.out.println("Error connecting to server");
            JOptionPane.showMessageDialog(null, "Error connecting to server!");
        }
    }
        
    public void chat() throws IOException
    {

        //fixing visiblity of buttons
        frame.connectButton.setVisible(false);
        frame.logOffButton.setVisible(true);
        frame.addFriendButton.setVisible(true);

        //changing offline to online + username
        frame.onlineIndicator.setText("Online: " + username);
        frame.onlineIndicator.setForeground(Color.GREEN);

        dialog.dispose(); //close dialog

        while (!logOff)
        {
            String msg = receive(reader);

            //accepts incoming friend request and askes user to accept or decline
            if (msg.startsWith("FRIEND_REQUEST_SENDING"))
            {
                String info[] = msg.split(" ");

                int option = JOptionPane.showConfirmDialog(dialog, info[1] + " wants to be your friend, do you accept?"); //ask user to add friend

                if (option == JOptionPane.YES_OPTION)
                {
                    send("FRIEND_REQUEST_ACCEPTED " + info[1]); //accepted request sent back to user who sent it
                                
                    frame.listModel.addElement(info[1]); //adding friend to friends list
                }

                else
                {      
                    send("FRIEND_REQUEST_DECLINED " + info[1]); //delcines request sent back to user who sent it
                }
            }

            //your sent friend request has been declined
            else if (msg.startsWith("FRIEND_REQUEST_ACCEPTED"))
            {
                String info[] = msg.split(" ");

                JOptionPane.showMessageDialog(frame, info[1] + " has accepted your friend request!");

                frame.listModel.addElement(info[1]); //adding friend to friends lsit
            }

            //your sent friend request has been declined
            else if (msg.startsWith("FRIEND_REQUEST_DECLINED"))
            {
                String info[] = msg.split(" ");

                JOptionPane.showMessageDialog(frame, info[1] + " has declined your friend request :(");
            }

            //lets the user know the user they tried to send a request to doesn't exist
            else if (msg.startsWith("NO_FRIEND_EXISTS"))
            {
                String info[] = msg.split(" ");

                JOptionPane.showMessageDialog(frame, "The user, " + info[1] + ", does not exist");
            }
            
            else if (msg.startsWith("RECEIVE_MESSAGE")) 
            {
                String info[] = msg.split(" ", 3);

                if (!frame.chatDialogTable.containsKey(info[1])) //if key doesn't exist in hastable, then...
                {
                    frame.chatDialogTable.put(info[1], new ChatDialog(info[1], frame)); //create new chatDialog in hashtable
                }

                frame.chatDialogTable.get(info[1]).chatViewer.addTextMessage(info[2], "green", "left"); //add text to chatViewer
            }

            else if (msg.startsWith("RECEIVE_FILE"))
            {
                String info[] = msg.split(" ", 4);
                
                if (!frame.chatDialogTable.containsKey(info[1])) //if key doesn't exist in hastable, then...
                {
                    frame.chatDialogTable.put(info[1], new ChatDialog(info[1], frame)); //create new chatDialog in hashtable
                }

                int result = JOptionPane.showConfirmDialog(frame.chatDialogTable.get(info[1]), info[1] + " is trying to send you a message: " + info[3] + " size: " + info[2] + " bytes, do you accept?", msg, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION)
                {
                    frame.chatDialogTable.get(info[1]).chatViewer.addTextMessage(info[3], "green", "left"); //add file name to text dialog

                    if (babyServer == null)
                    {
                        babyServer = new BabyServer(port = createRandomPort());
                        new Thread(babyServer).start();
                        send("SET_UP_BABY_SERVER " + info[1] + " " + info[2] + " " + port + " " + info[3]); //sends username, filesize, port, and filename
                    }

                    else
                    {
                        send("SET_UP_BABY_SERVER " + info[1] + " " + info[2] + " " + port + " " + info[3]); //sends username, filesize, port, and filesname
                    }

                }

                else //file transfer declined
                {
                    send("FILE_DECLINED " + info[1] + " " + info[2]); //send username, and filename
                }
            }

            else if (msg.startsWith("FRIEND_UPDATE"))
            {
                String info[] = msg.split(" ");
 
                for (int i = 0; i < frame.listModel.getSize(); i++)
                {
                    if (frame.listModel.elementAt(i).startsWith(info[1]))
                    {
                        if (info[2].startsWith("true"))
                        {
                            frame.listModel.setElementAt(info[1] + " " + online, i); //logged on emoji
                        }

                        else
                        {
                            frame.listModel.setElementAt(info[1] + " " + offline, i); //logged off emoji
                        } 
                    }
                }
            }

            else if (msg.startsWith("BABY_SERVER_INFO"))
            {
                String info[] = msg.split(" ", 5);

                ClientHandlerFileSend clientHandlerFileSend = new ClientHandlerFileSend(frame.fileVector, info[1], info[2], info[4], frame);
                new Thread(clientHandlerFileSend).start();
            }

            else if (msg.startsWith("FILE_DECLINED"))
            {
                String info[] = msg.split(" ");

                for (int i = 0; i < frame.fileVector.size(); i++)
                {
                    if (frame.fileVector.elementAt(i).getName().startsWith(info[1]))
                    {
                        frame.fileVector.removeElementAt(i); //remove file from temp vector
                    }
                }
            }

            else
            {
                System.out.println("Lost message?");
            }
        }
    }

    public void parseFriendsList(String friends) throws IOException
    {
        String friendsArray[] = friends.split(" ");

        System.out.println(friendsArray[1]);

        int listSize = Integer.parseInt(friendsArray[1]);

        for (int i = 0; i < listSize; i++)
        {
            frame.listModel.addElement(friendsArray[i + 2]); //adds friend to list
        }

    }

    public int createRandomPort()
    {
        Random rand = new Random();

        return rand.nextInt((65535 - 1024) + 1) + 1024;
    }

    public void send(String s) throws IOException
    {
        this.talker.send(s, dos);
    }

    public String receive(BufferedReader inputStream) throws IOException
    {
        return this.talker.receive(reader);
    }

   
}