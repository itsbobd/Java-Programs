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
public class GetUsernameFrame extends JFrame
                        implements ActionListener
{
    JButton exitButton;
    JButton goButton;
    JTextField userTextField;
    JLabel userLabel;
    ClientTalker talker;
    Socket socket;

//============================================= Constructor
GetUsernameFrame()
{
    JPanel buttonPanel;
    JPanel usernamePanel; 

    //============================================= Buttons
    exitButton = new JButton("Exit");
    exitButton.addActionListener(this);
    exitButton.setActionCommand("Exit");

    goButton = new JButton("Go");
    goButton.addActionListener(this);
    goButton.setActionCommand("Go");

    //============================================= Label/TextField
    userTextField = new JTextField(16);
    userLabel = new JLabel();
    userLabel.setText("Enter Username:");

    //============================================= Configuring Panels
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2));
    buttonPanel.add(goButton);
    buttonPanel.add(exitButton);

    usernamePanel = new JPanel();
    usernamePanel.setLayout(new GridLayout(1, 2));
    usernamePanel.add(userLabel);
    usernamePanel.add(userTextField);

    //============================================= Configuring Frame
    add(usernamePanel, BorderLayout.NORTH);
    add(buttonPanel, BorderLayout.SOUTH);

    setupMainFrame();

} //end of GetUsernameFrame Constructor

//============================================= setting up Frame
void setupMainFrame()
{
    Toolkit tk;
    Dimension d;

    setTitle("Login");

    tk = Toolkit.getDefaultToolkit();
    d = tk.getScreenSize();
    setSize(d.width/5, d.height/5);
    setLocation(d.width/2, d.height/2);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    setVisible(true);

} //end setupMainFrame

//============================================= Action Performed
public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("Exit"))
        {
            System.exit(0);
        }

        if (e.getActionCommand().equals("Go"))
        {
            if(!(userTextField.getText().trim().isEmpty()))
            {
                try
                {
                    socket = new Socket("127.0.0.1", 12345);
                    System.out.println("Connected to server");
                    new ConnectionToServer(new ClientTalker(), userTextField.getText(), socket);
                    dispose();
                }
            
                catch(IOException ex)
                {
                    System.out.println("Couldn't connect to server");
                    JOptionPane.showMessageDialog(null, "Couldn't connect to server");
                }
            }
            
            
        }

    } //end of actionPerformed

}
