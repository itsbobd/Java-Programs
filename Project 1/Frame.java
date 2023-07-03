//============================================

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;

//============================================
class Frame extends JFrame
            implements ActionListener
{
    JList<String> URLListBox;
    URLList list;
    JButton goButton;
    JButton exitButton;
    JTextField textField;
    URL url; 
    BufferedReader pageReader;
    String string;
    JOptionPane optionPane;

    //=========constructor====================
    Frame()
    {
      JScrollPane listBoxScrollPane;
      JPanel buttonPanel;
      JPanel textFieldPanel;
      optionPane = new JOptionPane();

      //creating buttons and adding them to panel
      goButton = new JButton("Go");
      exitButton = new JButton("Exit");

      goButton.addActionListener(this);
      exitButton.addActionListener(this);

      goButton.setActionCommand("Go");
      exitButton.setActionCommand("Exit");

      //create list and list box
      list = new URLList();
      URLListBox = new JList<String>(list);

      //initializing scroll pane and button panel
      listBoxScrollPane = new JScrollPane(URLListBox);
      buttonPanel = new JPanel();
      textFieldPanel = new JPanel();

      //adding buttons to the button panel
      buttonPanel.add(goButton);
      buttonPanel.add(exitButton);
      buttonPanel.setLayout(new GridLayout(2, 1));

      //initializing textfield
      textField = new JTextField(16);
      textFieldPanel.add(textField);

      //placing button panel and scroll pane inside of frame
      add(buttonPanel, BorderLayout.EAST);
      add(listBoxScrollPane, BorderLayout.CENTER);
      add(textFieldPanel, BorderLayout.NORTH);

      setupMainFrame();

    } //end of Frame constructor

    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;

        setTitle("Trip Records List");

        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width/2, d.height/2);
        setLocation(d.width/3, d.height/3);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

    } //end setupMainFrame

    public void actionPerformed (ActionEvent e)
    {
        if (e.getActionCommand().equals("Go"))
        {
            string = textField.getText();
            String textString;

            //clear previous html and textfield url
            list.clear();
            textField.setText("");
            
            //checks to see if the url is valid or not
            try
            {
                url = new URL(string);
                url.toURI();
                pageReader = new BufferedReader(new InputStreamReader(url.openStream()));
            }

            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "The entered url is invalid");
            }

            if (pageReader != null)
            {
                try
                {
                    textString = pageReader.readLine(); 
                    while (textString != null)
                    {
                        list.addElement(textString);
                        textString = pageReader.readLine();
                    }
                }

                catch (IOException ioex)
                {
                    JOptionPane.showMessageDialog(this, "IOException");
                }
            }
            

            
        }

        if (e.getActionCommand().equals("Exit"))
        {
            System.exit(0);
        }



    } //end of actionPerformed
}