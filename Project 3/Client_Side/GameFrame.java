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
public class GameFrame extends JFrame
                        implements ActionListener, MouseListener
{
    JButton exitButton;
    JButton clearButton;
    Vector<JPanel> panelVector;
    Random rand;
    Boolean pressed = false;

//============================================= Constructor
    GameFrame()
    {
        JPanel buttonPanel;
        JPanel gamePanel;
        panelVector = new Vector<JPanel>();
        
        rand = new Random(); //random variable for color

        //===================================== Button(s)
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        exitButton.setActionCommand("Exit");

        clearButton = new JButton("Clear Screen");
        clearButton.addActionListener(this);
        clearButton.setActionCommand("Clear");

        //===================================== Button(s) Panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(exitButton);
        buttonPanel.add(clearButton);

        //===================================== Game Panel
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(100, 100));
        for (int i = 0; i < 10000; i++)
        {   
            panelVector.add(new JPanel());
            panelVector.elementAt(i).addMouseListener(this);
            gamePanel.add(panelVector.elementAt(i));
        }   

        //===================================== Placing Panels
        add(buttonPanel, BorderLayout.SOUTH);
        add(gamePanel, BorderLayout.CENTER);


        setupMainFrame();

        clearButton.doClick();

    } //end of GameFrame Constructor

    //============================================= setting up main frame
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;

        setTitle("Color!!");

        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width/2, d.height/2);
        setLocation(d.width/3, d.height/3);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

    } //end setupMainFrame

    //============================================= start of actionPerformed + Mouse Events
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("Exit"))
        {
            System.exit(0);
        }

        if (e.getActionCommand().equals("Clear"))
        {
            for (int j = 0; j < panelVector.size(); j++)
            {
                panelVector.elementAt(j).setBackground(Color.WHITE);
            }
        }

    } //end of actionPerformed

    public void mousePressed(MouseEvent e)
    {
       if (e.getButton() == MouseEvent.BUTTON1) {
           pressed = true;
       }
    }

    public void mouseReleased(MouseEvent e)
    {
       if (e.getButton() == MouseEvent.BUTTON1) {
       pressed = false;
      }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
       if (pressed)
       {
           JPanel panel = (JPanel)e.getSource();
           panel.setBackground(getRandomColor());    
       }   
        
    }

    public void mouseExited(MouseEvent e) {
    }

    public Color getRandomColor()
    {  
        int red = rand.nextInt(256);
        int green = rand.nextInt(256);
        int blue = rand.nextInt(256);

        return new Color(red, green, blue);
    }

    


} //end of class GameFrame
