package Client_Side;
//============================================

import java.awt.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.*;

//============================================
public class LoginDialog extends JDialog
                        implements ActionListener
{
    JButton loginButton;
    JButton changeUserButton;
    JButton exitButton;
    JButton registerButton;
    JLabel usernameLabel;
    JLabel passwordLabel;
    JTextField usernameField;
    JTextField passwordField;
    String password;
    String username;
    String address;
    String port;
    Socket socket;
    JCheckBox rememberMeBox;
    Properties prop;
    File file;
    FileInputStream instream;
    FileOutputStream outstream;
    UserFrame frame;
    ConnectionToServer cts;

    SSLSocketFactory    sslSocketFactory;
    SSLContext          sslContext;
    KeyManagerFactory   keyManagerFactory;
    KeyStore            keyStore;
    char[]              keyStorePassphrase;

SSLSocket           sslSocket;


//============================================Constructor
LoginDialog(String u, String p, String a, String po, UserFrame frame)
{   
    username = u;
    password = p;
    address = a;
    port = po;
    prop = frame.prop;
    instream = frame.instream;
    outstream = frame.outstream;
    file = frame.file;
    this.frame = frame;
    
   JPanel fieldPanel;
   JPanel buttonPanel;
   JPanel middlePanel;

   //============================================Buttons
   loginButton = new JButton("Login");
   loginButton.addActionListener(this);
   loginButton.setActionCommand("Login");

   changeUserButton = new JButton("Change User");
   changeUserButton.addActionListener(this);
   changeUserButton.setActionCommand("Change User");

   registerButton = new JButton("Register");
   registerButton.addActionListener(this);
   registerButton.setActionCommand("Register");

   exitButton = new JButton("Exit");
   exitButton.addActionListener(this);
   exitButton.setActionCommand("Exit");

   rememberMeBox = new JCheckBox("Remember me?");

   //============================================configuring fields/labels
   usernameLabel = new JLabel("Username: ");
   usernameField = new JTextField(12);
   usernameField.setText(username);

   passwordLabel = new JLabel("Password: ");
   passwordField = new JTextField(12);
   passwordField.setText(password);

    //============================================configuring panels
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 3));
    buttonPanel.add(loginButton);
    buttonPanel.add(registerButton);
    buttonPanel.add(exitButton);

    middlePanel = new JPanel();
    middlePanel.add(rememberMeBox);

    fieldPanel = new JPanel();
    GroupLayout layout = new GroupLayout(fieldPanel);
    fieldPanel.setLayout(layout);

    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

    hGroup.addGroup(layout.createParallelGroup().
    addComponent(usernameLabel).addComponent(passwordLabel));

    hGroup.addGroup(layout.createParallelGroup().
    addComponent(usernameField).addComponent(passwordField));

    layout.setHorizontalGroup(hGroup);

    GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

    vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
		addComponent(usernameLabel).addComponent(usernameField));

	vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
		addComponent(passwordLabel).addComponent(passwordField));

    layout.setVerticalGroup(vGroup);   
    
    //============================================adding panels
    add(fieldPanel, BorderLayout.NORTH);
    add(middlePanel, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);

    //============================================loading properties file
    if (!prop.getProperty("username").isEmpty() && !prop.getProperty("password").isEmpty()) //if properties aren't "" (empty)
    {
        usernameField.setText(prop.getProperty("username"));
        passwordField.setText(prop.getProperty("password"));
    }

    setupDialog();

} //end of constructor

void setupDialog()
{
    Toolkit tk;
	Dimension d;

	setTitle("Login/Register");

	tk = Toolkit.getDefaultToolkit();
	d = tk.getScreenSize();
	setSize(d.width/3, d.height/5);

    setLocationRelativeTo(frame);

	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

	setVisible(true);

} //end setupDialog

//============================================actionPerformed
public void actionPerformed(ActionEvent e)
{
    if (e.getActionCommand().equals("Login"))
    {
        username = usernameField.getText().trim().toLowerCase();
        password = passwordField.getText().trim();
        
        if (!username.isEmpty() && !password.isEmpty())
        {
            if (checkCred(username) && checkCred(password))
            {
                try
                {
                    sslSocket = setUpEncryptedStreams();

                    cts = new ConnectionToServer(new ClientTalker(), username, password, sslSocket, false, this);
                    new Thread(cts).start();
                }

                catch(Exception ex)
                {   
                    System.out.println("Error connecting to server");
                    JOptionPane.showMessageDialog(this, "Error connecting to server, your ip or port may be incorrect");
                }
            }

            else
                JOptionPane.showMessageDialog(this, "Invalid format for either username or password");
        }    

        else
            JOptionPane.showMessageDialog(this, "One or both of the fields are empty");

    }

    if (e.getActionCommand().equals("Change User"))
    {
        usernameField.setText("");
        passwordField.setText("");
    }

    if (e.getActionCommand().equals("Register"))
    {

        username = usernameField.getText().trim().toLowerCase();
        password = passwordField.getText().trim();
        
        if (!username.isEmpty() && !password.isEmpty())
        {
            if(checkCred(username) && checkCred(password))
            {
                try
                {
                    sslSocket = setUpEncryptedStreams();

                    cts = new ConnectionToServer(new ClientTalker(), username, password, sslSocket, true, this);
                    new Thread(cts).start();
                    
                }

                catch(Exception ex)
                {   
                    System.out.println("Error connecting to server");
                    JOptionPane.showMessageDialog(this, "Error connecting to server, your ip or port may be incorrect");
                }
            }

            else
                JOptionPane.showMessageDialog(this, "No embedded spaces in the username or password!");
        }    

        else
            JOptionPane.showMessageDialog(this, "One or both of the fields are empty");
            
    }

    if (e.getActionCommand().equals("Exit"))
    {
        dispose();
    }
}

public Boolean checkCred(String s)
{
    if(s.contains(" ") || s.contains("\t") || s.contains("\n")) //if the string contains a space/new line/indented space
        return false;

    else
        return true;    
}

public SSLSocket setUpEncryptedStreams() throws Exception
{
    System.setProperty("javax.net.ssl.trustStore","samplecacerts");
    System.setProperty("javax.net.ssl.trustStorePassword","changeit");

    sslContext          = SSLContext.getInstance("SSL");
    keyManagerFactory   = KeyManagerFactory.getInstance("SunX509");
    keyStore            = KeyStore.getInstance("JKS");

    keyStorePassphrase = "passphrase".toCharArray();
    keyStore.load(new FileInputStream("testkeys"), keyStorePassphrase);

    keyManagerFactory.init(keyStore, keyStorePassphrase);
    sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

    sslSocketFactory = sslContext.getSocketFactory();

    sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();

    return sslSocket = (SSLSocket)sslSocketFactory.createSocket(address, Integer.parseInt(port));
}

}
