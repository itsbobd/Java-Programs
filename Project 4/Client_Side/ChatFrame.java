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
public class ChatFrame extends JFrame
                        implements ActionListener
{
    JButton exitButton;
    JButton sendButton;
    JTextField sendTextField;
    JLabel broadcastLabel;
    ClientTalker talker;
    DataOutputStream stream;

    //============================================= Constructor
ChatFrame(ClientTalker t, DataOutputStream s)
{
    this.stream = s;
    this.talker = t;
    JPanel buttonPanel;
    JPanel chatPanel; 

    //============================================= Buttons
    exitButton = new JButton("Exit");
    exitButton.addActionListener(this);
    exitButton.setActionCommand("Exit");

    sendButton = new JButton("Send");
    sendButton.addActionListener(this);
    sendButton.setActionCommand("Send");

    //============================================= Label/TextField
    sendTextField = new JTextField(16);
    broadcastLabel = new JLabel();
    broadcastLabel.setText("this is where messages will appear...");

    //============================================= Configuring Panels
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2));
    buttonPanel.add(sendButton);
    buttonPanel.add(exitButton);

    chatPanel = new JPanel();
    chatPanel.setLayout(new GridLayout(1, 2));
    chatPanel.add(broadcastLabel);
    chatPanel.add(sendTextField);

    //============================================= Configuring Frame
    add(chatPanel, BorderLayout.NORTH);
    add(buttonPanel, BorderLayout.SOUTH);

    setupMainFrame();

} //End of Constructor

void setupMainFrame()
{
    Toolkit tk;
    Dimension d;

    setTitle("Chat Client");

    tk = Toolkit.getDefaultToolkit();
    d = tk.getScreenSize();
    setSize(d.width/3, d.height/4);
    setLocation(d.width/2, d.height/2);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    setVisible(true);

} //end setupMainFrame

public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("Exit"))
        {
            System.exit(0);
        }

        if (e.getActionCommand().equals("Send"))
        {
            if(!(sendTextField.getText().trim().isEmpty()))
            {
                try
                {
                    talker.send(sendTextField.getText(), stream);
                    sendTextField.setText(""); //sets the text back to blank
                }

                catch (IOException ex)
                {
                    System.out.println("Error sending message to server");
                    JOptionPane.showMessageDialog(null, "Error sending message to server");
                }            
            }
        }
    } //end of actionPerformed

} 