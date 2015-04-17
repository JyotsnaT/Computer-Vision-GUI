import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

import javax.swing.*;
import javax.swing.SwingUtilities.*;
import javax.swing.filechooser.*;

import matlabcontrol.*;

@SuppressWarnings("serial")
class filechoose extends JPanel implements ActionListener
{
	JButton Browse;
	JTextArea fileText;
	JFileChooser fc;
	
	public filechoose()
	{
		super(new BorderLayout());
		//creating the log to feed it to the action listeners
		fileText = new JTextArea(5,20);
		fileText.setMargin(new Insets(5,5,5,5));
		fileText.setEditable(false);
		JScrollPane scrPane = new JScrollPane(fileText);
		
		
		//creating a new file chooser
		fc = new JFileChooser();
		
		
		//create the browse button for the file selection
		Browse = new JButton("Browse");
		Browse.addActionListener(this);
		
		//create a separate panel - putting the open button and the text field in separate panel
		JPanel fcPanel = new JPanel();
		add(fcPanel,BorderLayout.PAGE_END);
		add(scrPane,BorderLayout.PAGE_START);
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		//handling the browse button action
		if(e.getSource() == Browse)
		{
			int returnFile = fc.showOpenDialog(filechoose.this);
			if(returnFile == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
			    fileText.append(file.getAbsolutePath());
			    
			}
			fileText.setCaretPosition(fileText.getDocument().getLength());
		}
	}
	
	// create the GUI and show it finally
	public static void creatGUI()
	{
		JFrame frame = new JFrame("Video Analysis");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//add content to the window
		frame.add(new filechoose());
		
		//display the window
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String args[]) throws MatlabConnectionException,MatlabInvocationException
	{
		//create proxy to connect to matlab
		
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).setHidden(true).build();
		RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory(options);
		RemoteMatlabProxy proxy = (RemoteMatlabProxy)factory.getProxy();
		
		//vid_gui v = new vid_gui();
//		final filechoose FC = new filechoose();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				creatGUI();
				
			}
		
		});
		proxy.disconnect();
	}

	
}
	
/*
public class comp_vision
{
	public static void main(String args[]) throws MatlabConnectionException,MatlabInvocationException
	{
		//create proxy to connect to matlab
		
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).setHidden(true).build();
		RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory(options);
		final RemoteMatlabProxy proxy = (RemoteMatlabProxy)factory.getProxy();
		
		//vid_gui v = new vid_gui();
		final filechoose FC = new filechoose();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				FC.creatGUI();
				proxy.disconnect();
			}
		
		});
	}

	

}
*/