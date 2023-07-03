package Server_Side;
//============================================

import java.io.*;
import javax.net.ssl.SSLSocket;

//============================================
public class ConnectionToClient 
                            implements Runnable
{
    boolean logOff = false;
    SSLSocket sslNormalSocket;
    ServerTalker talker;
    String incomingString;
    ProfileTable profiles;
    DataOutputStream fileOutputStream;
    Profile temp;
    File file;
    String thisUsername;

    OutputStream            outStream;
    DataOutputStream        dos;

    InputStream             inStream;
    BufferedReader          reader; 

    //============================================= Constructor
    ConnectionToClient(SSLSocket s, ServerTalker t, ProfileTable p, File f) throws FileNotFoundException
    {
        
        talker = t;
        sslNormalSocket = s;
        profiles = p;
        file = f;

    } //end of Constructor

    public void run()
    {
        try
        {

            outStream = sslNormalSocket.getOutputStream();
            dos = new DataOutputStream(outStream);
        
            inStream = sslNormalSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inStream));

            incomingString = this.receive(reader);

            if (incomingString.startsWith("REGISTER"))
            {
                String[] info = incomingString.split(" "); //splits the incoming string into username and password

                if(!(profiles.containsKey(info[1]))) //if the username is NOT in the hastable
                {
                    thisUsername = info[1];

                    profiles.put(thisUsername, new Profile(thisUsername, info[2], this)); //creates new profile and puts it into the hashtable
                    fileOutputStream = new DataOutputStream(new FileOutputStream(file, false)); //creates file stream
                    profiles.store(fileOutputStream); //rewrites file with new user

                    send("GOOD");

                    chat(); //starts communicating with client
                }

                else
                {
                    send("BAD");
                    //send that the user needs to try another username
                }
            }

            else if (incomingString.startsWith("LOGIN"))
            {
                String[] info = incomingString.split(" "); //splits the incoming string into username and password

                if(profiles.containsKey(info[1])) //if the username is in the hastable
                {
                    if (profiles.get(info[1]).password.equals(info[2])) //if the password matches the one in the hashtable
                    {
                        if(profiles.get(info[1]).CTC == null) //if the CTC is null (meaning the user isn't already online)
                        {
                            thisUsername = info[1];

                            profiles.get(thisUsername).CTC = this; //sets this user's ctc to the one just opened for the login

                            send("GOOD");

                            send("FRIENDS_LIST " + profiles.get(thisUsername).sendFriends()); //send user's friendlist

                            updateFriendsList(true); //for user logging in, updates their friends list
                            
                            chat(); //starts communicating with client
                        }

                        else
                        {
                            send("ONLINE");
                        }       
                    }

                    else
                    {
                        send("BAD_CRED");
                    }                       
                }

                else
                {
                    send("BAD_CRED");
                }
                    
            }

            else
            {
                //send back some sort of error code
            }
            
        }

        catch (IOException ex)
        {
            System.out.println("Problem with connecting to client");
            
            //somehow remove profile from list
        }
    } 

    public void send(String s) throws IOException
    {
        this.talker.send(s, dos);
    }

    public String receive(BufferedReader inputStream) throws IOException
    {
        return this.talker.receive(reader);
    }

    //updates friends friendsLists' when said user is logging on
    public void updateFriendsList(Boolean isLoggingOn) throws IOException
    {
        if (isLoggingOn) //update own friendsList and update others' friendsLists
        {
            if (profiles.get(thisUsername).friendsList.size() > 0) //if list size is greater than 0
            {
                for (int i = 0; i < profiles.get(thisUsername).friendsList.size(); i++) //loop through friends list
                {
                    if (profiles.get(profiles.get(thisUsername).friendsList.elementAt(i)).CTC != null) //when friend is online
                    {
                        send ("FRIEND_UPDATE " + profiles.get(thisUsername).friendsList.elementAt(i) + " true"); //update own friends list (of this CTC, logged on)
                        profiles.get(profiles.get(thisUsername).friendsList.elementAt(i)).CTC.send ("FRIEND_UPDATE " + thisUsername + " true"); //update friend's friends list (logging on)
                    }

                    else
                    {
                        send ("FRIEND_UPDATE " + profiles.get(thisUsername).friendsList.elementAt(i) + " false"); //update own friends list (of this CTC, logged off)
                    }
                }
            }
        }

        else //update others' friendsLists
        {
            if (profiles.get(thisUsername).friendsList.size() > 0) //if list size is greater than 0
            {
                for (int i = 0; i < profiles.get(thisUsername).friendsList.size(); i++) //loop through friends list
                {
                    if (profiles.get(profiles.get(thisUsername).friendsList.elementAt(i)).CTC != null) //when friend is online
                    {
                        profiles.get(profiles.get(thisUsername).friendsList.elementAt(i)).CTC.send ("FRIEND_UPDATE " + thisUsername + " false"); //update friend's friends list (logging off)
                    }

                    //no need to update anything if the user is offline
                }
            }                
        }
    }

    public void chat() throws IOException
    {

        while (!logOff)
        {
            String msg = this.receive(reader);

            if (msg.startsWith("ADD_FRIEND"))
            {
                String info[] = msg.split(" ");

                String friendToBe = info[1];

                if (profiles.containsKey(friendToBe))
                {
                    if (profiles.get(friendToBe).CTC != null)
                    {
                        profiles.get(friendToBe).CTC.send("FRIEND_REQUEST_SENDING " + thisUsername);
                        //send friend request with the user who sent it's username
                    }

                    else
                    {
                        //add to thing to send to them later
                    }
                }
            
                else
                {
                    send("NO_FRIEND_EXISTS " + friendToBe);
                    //sends message back to sender that their requested friend doesn't exist
                }
            }    

            else if (msg.startsWith("FRIEND_REQUEST_ACCEPTED"))
            {
                String info[] = msg.split(" ");

                String friendWhoSentRequest = info[1];

                //now add each other to their friends lists
                profiles.get(thisUsername).friendsList.addElement(friendWhoSentRequest);
                profiles.get(friendWhoSentRequest).friendsList.addElement(thisUsername);

                fileOutputStream = new DataOutputStream(new FileOutputStream(file, false)); //creates file stream
                profiles.store(fileOutputStream); //saves changes to friend lists' to server file

                //send message telling the one who sent the message that they were accepted
                if (profiles.get(friendWhoSentRequest).CTC != null)
                {
                    profiles.get(friendWhoSentRequest).CTC.send("FRIEND_REQUEST_ACCEPTED " + thisUsername);
                    //send message telling original sender that their request was accepted
                }

                else
                {
                    //add to thing to send to them later
                }
            }

            else if (msg.startsWith("FRIEND_REQUEST_DECLINED"))
            {
                String info[] = msg.split(" ");

                String friendWhoSentRequest = info[1];

                //send message telling one who sent the message that they were declined
                if (profiles.get(friendWhoSentRequest).CTC != null)
                {
                    profiles.get(friendWhoSentRequest).CTC.send("FRIEND_REQUEST_DECLINED " + thisUsername);
                    //send message telling original sender that their request was accepted
                }
            }

            else if (msg.startsWith("USER_LOGGING_OFF"))
            {
                updateFriendsList(false);

                //ctc set to null
                logOff = true;

                profiles.get(thisUsername).CTC = null;
            }

            else if (msg.startsWith("SEND_MESSAGE"))
            {
                String info[] = msg.split(" ", 3); //splits  message into 3 pieces, protocol; username; message

                if (profiles.get(info[1]).CTC != null)
                {
                    profiles.get(info[1]).CTC.send ("RECEIVE_MESSAGE " + thisUsername + " " + info[2]); //send message to recipient
                }

                else
                {
                    //add to thing to send to them later
                }
            }

            else if (msg.startsWith("SEND_FILE"))
            {
                String info[] = msg.split(" ", 4);

                if (profiles.get(info[1]).CTC != null)
                {
                    profiles.get(info[1]).CTC.send ("RECEIVE_FILE " + thisUsername + " " + info[2] + " " + info[3]); //sends file size and name to recipient 
                }
            }

            else if (msg.startsWith("SET_UP_BABY_SERVER"))
            {
                String info[] = msg.split(" ", 5);

                profiles.get(info[1]).CTC.send("BABY_SERVER_INFO " + this.sslNormalSocket.getInetAddress().getHostAddress() + " " + info[3] + " " + info[2] + " " + info[4]); 
                //sends file sender: baby server IP, port, filesize, and filename

            }

            else if (msg.startsWith("FILE_DECLINED"))
            {
                String info[] = msg.split(" ");

                profiles.get(info[1]).CTC.send("FILE_DECLINED " + info[2]);
            }

            else
            {
                System.out.println("Lost message?");
            }
        }  
    }
}    