package Client_Side;
//============================================

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

//============================================
public class ChatDialog extends JDialog
                        implements ActionListener, WindowListener, DropTargetListener
{
    JButton sendButton;
    JButton exitButton;
    JTextArea chatArea;
    ChatViewer chatViewer;
    String username;
    JScrollPane chatScrollPane;
    JScrollPane viewScrollPane;
    UserFrame frame;
    DropTarget dropTarget;

//============================================Constructor
ChatDialog(String username, UserFrame frame)
{
    this.username = username;
    this.frame = frame;
    JPanel inputPanel;
    JPanel buttonPanel;

    //============================================Buttons
    sendButton = new JButton("Send");
    sendButton.addActionListener(this);
    sendButton.setActionCommand("Send");

    exitButton = new JButton("Exit");
    exitButton.addActionListener(this);
    exitButton.setActionCommand("Exit");

    //============================================configuring panels
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2));
    buttonPanel.add(sendButton);
    buttonPanel.add(exitButton);

    chatArea = new JTextArea(1, 20); //3 rows tall, 20 columns wide
    chatArea.setLineWrap(true);

    chatScrollPane = new JScrollPane(chatArea);
    
    //============================================adding panels
    chatViewer = new ChatViewer(username);
    dropTarget = new DropTarget(chatViewer, this); //makes chatViewer a drop target
    viewScrollPane = new JScrollPane(chatViewer);

    inputPanel = new JPanel();
    inputPanel.setLayout(new FlowLayout());
    inputPanel.add(chatScrollPane);
    inputPanel.add(buttonPanel);

    add(inputPanel, BorderLayout.SOUTH);
	add(viewScrollPane, BorderLayout.CENTER);

    setupDialog();

} //end of constructor

void setupDialog()
{
    Toolkit tk;
	Dimension d;

	setTitle("Chat with " + username);

	tk = Toolkit.getDefaultToolkit();
	d = tk.getScreenSize();
	setSize(d.width/3, d.height/5);
	setLocationRelativeTo(frame);

	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    addWindowListener(this);

	setVisible(true);

} //end setupDialog

//============================================actionPerformed
public void actionPerformed(ActionEvent e) 
{
    if (e.getActionCommand().equals("Send"))
    {

        if (!chatArea.getText().trim().isEmpty())
        {
            String msg = "SEND_MESSAGE " + username + " " + chatArea.getText();

            try
            {
                frame.loginDialog.cts.send(msg); //sends the message
                chatViewer.addTextMessage(chatArea.getText(), "blue", "right"); //adds the sent message to the viewed messages

                chatArea.setText(""); //gets rid of what the user just said from the chat box
                chatArea.requestFocus(); //sets the focus back to the chat box
            }
        
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(this, "Error attempting to send message");
            }
        }
    }

    if (e.getActionCommand().equals("Exit"))
    {
        frame.chatDialogTable.remove(username); //removes the chat dialog from the table
        dispose();
    }
}

//============================================WindowEvents
public void windowOpened(WindowEvent e) {}

public void windowClosing(WindowEvent e) 
{
    frame.chatDialogTable.remove(username); //removes the chat dialog from the table  
}

public void windowClosed(WindowEvent e) {}

public void windowIconified(WindowEvent e) {}

public void windowDeiconified(WindowEvent e) {}

public void windowActivated(WindowEvent e) {}

public void windowDeactivated(WindowEvent e) {}

//============================================DropTargetDragEvents
public void dragEnter(DropTargetDragEvent dtde) 
{
    
}

public void dragOver(DropTargetDragEvent dtde) {}

public void dropActionChanged(DropTargetDragEvent dtde) {}

public void dragExit(DropTargetEvent dte) 
{

}

public void drop(DropTargetDropEvent dtde) 
{
    java.util.List<File> fileList;
    Transferable transferData;
    File file;

    transferData = dtde.getTransferable();

    try
    {
        if (transferData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);

            fileList = (java.util.List<File>)(transferData.getTransferData(DataFlavor.javaFileListFlavor));

		    file = fileList.get(0); // just grabs first file if it is a list

		    if(file != null)
		    {
                frame.loginDialog.cts.send("SEND_FILE " + username + " " + file.length() + " " + file.getName());
                frame.fileVector.addElement(file); //save file for later
            } 
        }    
    }  

    catch(UnsupportedFlavorException ufe)
    {
        JOptionPane.showMessageDialog(this, "Unsupported flavor found!", "Error!" , JOptionPane.ERROR_MESSAGE);
    }

    catch(IOException e)
    {
        JOptionPane.showMessageDialog(this, "IOException found getting transferable data!", "Error!" , JOptionPane.ERROR_MESSAGE);
    }

    
    
    
}

}