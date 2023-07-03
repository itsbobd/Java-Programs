package Client_Side;
//============================================

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

//============================================
public class ConnectionDialog extends JDialog
                        implements ActionListener, WindowListener
{
    JButton goButton;
    JButton exitButton;
    JLabel addressLabel;
    JLabel portLabel;
    JTextField addressField;
    JTextField portField;
    String port;
    String address;
    Boolean startUp;

//============================================Constructor
ConnectionDialog(String p, String a, boolean b)
{
    port = p;
    address = a;
    startUp = b;

   JPanel fieldPanel;
   JPanel buttonPanel;

   //============================================Buttons
   goButton = new JButton("Go");
   goButton.addActionListener(this);
   goButton.setActionCommand("Go");

   exitButton = new JButton("Exit");
   exitButton.addActionListener(this);
   exitButton.setActionCommand("Exit");

   //============================================configuring fields/labels
   addressLabel = new JLabel("Server Address: ");
   addressField = new JTextField(12);
   addressField.setText(address);

   portLabel = new JLabel("Server Port: ");
   portField = new JTextField(12);
   portField.setText(port);

    //============================================configuring panels
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2));
    buttonPanel.add(goButton);
    buttonPanel.add(exitButton);

    fieldPanel = new JPanel();
    GroupLayout layout = new GroupLayout(fieldPanel);
    fieldPanel.setLayout(layout);

    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

    hGroup.addGroup(layout.createParallelGroup().
    addComponent(addressLabel).addComponent(portLabel));

    hGroup.addGroup(layout.createParallelGroup().
    addComponent(addressField).addComponent(portField));

    layout.setHorizontalGroup(hGroup);

    GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

    vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
		addComponent(addressLabel).addComponent(addressField));

	vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
		addComponent(portLabel).addComponent(portField));

    layout.setVerticalGroup(vGroup);   
    
    //============================================adding panels
    add(fieldPanel, BorderLayout.NORTH);
	add(buttonPanel, BorderLayout.SOUTH);

    addWindowListener(this);

    setupDialog();

} //end of constructor

void setupDialog()
{
    Toolkit tk;
	Dimension d;

	setTitle("Connection Configuration");

	tk = Toolkit.getDefaultToolkit();
	d = tk.getScreenSize();
	setSize(d.width/3, d.height/5);
	setLocation(d.width/2, d.height/2);

	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

	setVisible(true);

} //end setupDialog

//============================================actionPerformed
public void actionPerformed(ActionEvent e)
{
    if (e.getActionCommand().equals("Go"))
    {
        //if statement to check connectios and such, like a try catch and constructing a socket

        

        address = addressField.getText();
        port = portField.getText();

        if (!address.isEmpty() && !port.isEmpty())
            dispose();

        else
            JOptionPane.showMessageDialog(this, "One or both of the fields are empty");
        
    }

    if (e.getActionCommand().equals("Exit"))
    {
       if (startUp) //close application
        System.exit(0);

       else //keep application running
        dispose();
    }
}

//============================================Window Listener methods
public void windowOpened(WindowEvent e) {
}

public void windowClosed(WindowEvent e) {
}

public void windowClosing(WindowEvent e) 
{
    if(startUp) //if startUp is true then the application closes
        System.exit(0);
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
