package Client_Side;
//============================================

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

//============================================
public class UserFrame extends JFrame
                        implements ActionListener, MouseListener, WindowListener
{
    JButton exitButton;
    JButton connectButton;
    JButton ipButton;
    JButton logOffButton;
    JButton addFriendButton;
    JTextField userTextField;
    JLabel userLabel;
    Properties prop;
    Socket socket;
    String port;
    String address;
    ConnectionDialog dialog;
    JLabel onlineIndicator;
    File file;
    FileInputStream instream;
    FileOutputStream outstream;
    Boolean justCreated = false;
    JScrollPane scrollPane;
    DefaultListModel<String> listModel;
    JList<String> list;
    LoginDialog loginDialog;
    Hashtable<String, ChatDialog> chatDialogTable;
    Vector<File> fileVector;

//============================================= Constructor
UserFrame()
{
    JPanel topPanel;
    JPanel indicatorPanel;
    JPanel friendPanel;
    JPanel buttonPanel;
    JPanel subPanel;
    JPanel subPanel2;
    JPanel subPanel3;

    //============================================= Buttons
    exitButton = new JButton("Exit");
    exitButton.addActionListener(this);
    exitButton.setActionCommand("Exit");

    connectButton = new JButton("Connect");
    connectButton.addActionListener(this);
    connectButton.setActionCommand("Connect");

    ipButton = new JButton("Change Connection");
    ipButton.addActionListener(this);
    ipButton.setActionCommand("Change Connection");

    logOffButton = new JButton("Log Off");
    logOffButton.addActionListener(this);
    logOffButton.setActionCommand("Log Off");
    logOffButton.setVisible(false); //starts this button off as invisible

    addFriendButton = new JButton("Add Friend");
    addFriendButton.addActionListener(this);
    addFriendButton.setActionCommand("Add Friend");
    addFriendButton.setVisible(false); //starts this button off as invisible

    //============================================= Label
    onlineIndicator = new JLabel();
    onlineIndicator.setText("Offline");
    onlineIndicator.setForeground(Color.RED);

    //============================================= setting up panels
    subPanel = new JPanel(); //two buttons in same postion
    subPanel.setBackground(Color.WHITE);
    subPanel.add(connectButton);
    subPanel.add(logOffButton);

    subPanel2 = new JPanel();
    subPanel2.setBackground(Color.WHITE);
    subPanel2.add(ipButton);

    subPanel3 = new JPanel();
    subPanel3.setBackground(Color.WHITE);
    subPanel3.add(exitButton);

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(subPanel);
    buttonPanel.add(subPanel2);
    buttonPanel.add(subPanel3);

    indicatorPanel = new JPanel();
    indicatorPanel.setBackground(Color.WHITE);
    indicatorPanel.add(onlineIndicator);

    topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
    topPanel.add(indicatorPanel);
    topPanel.add(buttonPanel);

    indicatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
 
    //============================================= DLM and scrollpane
    friendPanel = new JPanel();

    listModel = new DefaultListModel<>();
    list = new JList<>(listModel);
    list.setPreferredSize(new Dimension(265, 125));
    list.addMouseListener(this);
    scrollPane = new JScrollPane(list);

    friendPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    friendPanel.add(scrollPane);
    friendPanel.add(addFriendButton);

    //============================================= creating table for storing multiple chatDialogs
    chatDialogTable = new Hashtable<String, ChatDialog>();

    //============================================= creating vector for temp storage of files for file transfer
    fileVector = new Vector<File>();

    //============================================= adding to frame
    add(topPanel, BorderLayout.NORTH);
    add(friendPanel, BorderLayout.CENTER);

    dialog = new ConnectionDialog("", "", true); //opens up the connection dialog with empty fields

    //retreiving fields from user
    address = dialog.address;
    port = dialog.port;

    //============================================= creating properties file
    prop = new Properties();
    prop.setProperty("username", ""); //properties get loaded with default "empty" values
    prop.setProperty("password", "");
    
    try
    {
        file = new File("localUser.properties");
        
        if (!file.exists()) //if file doesn't exist
        {
            file.createNewFile();
            System.out.println("Created new user file: " + file.getName());
        }

        instream = new FileInputStream(file); //creating input stream for properties file
        outstream = new FileOutputStream(file, true); //creating output stream for properties file

        if (file.length() == 0) //if file is empty, load in default "empty" properties
        {
            prop.store(outstream, "loading in file first time");
        }

        prop.load(instream); //now prop either had empty values or one from previous file
    }

    catch(Exception ex)
    {
        System.out.println("An error occurred, when trying to access localUser.properties");
    }
    
    setupMainFrame();

} //end of constructor

//============================================= setting up Frame
void setupMainFrame()
{
    Toolkit tk;
    Dimension d;

    setTitle("Chat with your Friends");

    tk = Toolkit.getDefaultToolkit();
    d = tk.getScreenSize();
    setSize(d.width/3, d.height/3);
    setLocation(d.width/2, d.height/2);
    getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 10));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    addWindowListener(this);

    setVisible(true);

} //end setupMainFrame

public void actionPerformed(ActionEvent e)
{
    if (e.getActionCommand().equals("Exit"))
    {
        System.exit(0);
    }

    if (e.getActionCommand().equals("Connect"))
    {
        loginDialog = new LoginDialog("", "", address, port, this);
    }

    if (e.getActionCommand().equals("Change Connection"))
    {
        new ConnectionDialog(port, address, false); //fills the fields the user originally entered
        
        //retreiving new fields from user
        address = dialog.address;
        port = dialog.port;
    }    

    if (e.getActionCommand().equals("Log Off"))
    {
        try
        {
            loginDialog.cts.send("USER_LOGGING_OFF");
        }

        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(this, "Log off error...");
        }
        
        //empty list
        listModel.clear();

        connectButton.setVisible(true); //the user can now see the connect button again
        logOffButton.setVisible(false); //the user can no longer see the log in button
        addFriendButton.setVisible(false); //the user can no longer see the add friend button

        //lets the frame know its back to being offline
        onlineIndicator.setText("Offline"); 
        onlineIndicator.setForeground(Color.RED);

        loginDialog.cts.logOff = true; //terminates while loop in cts
    }

    if (e.getActionCommand().equals("Add Friend"))
    {
        String newFriend = JOptionPane.showInputDialog (this, "Enter Username:");

        if (!newFriend.isEmpty())
        {
            newFriend = newFriend.trim().toLowerCase();
            
            if (checkCred(newFriend))
            {
                try
                {
                    loginDialog.cts.send("ADD_FRIEND " + newFriend);
                }

                catch(IOException ex)
                {
                    JOptionPane.showMessageDialog(this, "Friend request failed... Could not send");
                }
            }

            else
            {
                JOptionPane.showMessageDialog(this, "Invalid format for username; cannot contaion embedded spaces");
            }
        }
    }
}

public Boolean checkCred(String s)
{
    if(s.contains(" ") || s.contains("\t") || s.contains("\n")) //if the string contains a space/new line/indented space
        return false;

    else
        return true;    
}

//============================================= Mouse Listener Methods
public void mouseClicked(MouseEvent e) 
{
    if (e.getClickCount() == 2)
    {
        String friend = list.getSelectedValue().split(" ")[0];
        chatDialogTable.put(friend, new ChatDialog(friend, this)); //opens chat window, and adds to to chatDilaog table
    }
}

public void mousePressed(MouseEvent e) {
}

public void mouseReleased(MouseEvent e) {
}

public void mouseEntered(MouseEvent e) { 
}

public void mouseExited(MouseEvent e) {
}

//============================================= Window Listener Methods
public void windowOpened(WindowEvent e) {
}

public void windowClosing(WindowEvent e) 
{
    try
    {
        loginDialog.cts.send("USER_LOGGING_OFF");
    }

    catch(IOException ex)
    {
        JOptionPane.showMessageDialog(this, "Error logging off");
    }
    
}

public void windowClosed(WindowEvent e) {
}

public void windowIconified(WindowEvent e) {
}

public void windowDeiconified(WindowEvent e) {
}

public void windowActivated(WindowEvent e) {
}

public void windowDeactivated(WindowEvent e) {
}

}
