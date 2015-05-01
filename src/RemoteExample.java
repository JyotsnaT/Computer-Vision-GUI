
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabConnectionListener;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxyFactoryOptions;
import matlabcontrol.RemoteMatlabProxy;
import matlabcontrol.RemoteMatlabProxyFactory;

/**
 * A simple demo of some of matlabcontrol's remote functionality.
 * 
 * @author Joshua Kaplan
 */
public class RemoteExample extends JFrame
{
	//Creates this class
	public static void main(String[] args)
	{
		new RemoteExample();
	}
	
	//Sizes of panels
	private static final int PANEL_WIDTH = 400;
	private static final Dimension CONNECTION_PANEL_SIZE = new Dimension(PANEL_WIDTH, 70),
								   COMMAND_PANEL_SIZE = new Dimension(PANEL_WIDTH, 60),
								   RETURN_PANEL_SIZE = new Dimension(PANEL_WIDTH, 300),
								   MAIN_PANEL_SIZE = new Dimension(PANEL_WIDTH, CONNECTION_PANEL_SIZE.height +
										   										COMMAND_PANEL_SIZE.height + 
										   										RETURN_PANEL_SIZE.height);
	//Status messages
	private static final String STATUS_DISCONNECTED = "Connection Status: Disconnected",
								STATUS_CONNECTING = "Connection Status: Connecting",
								STATUS_CONNECTED = "Connection Status: Connected";
	
	//Return messages
	private static final String RETURNED_DEFAULT = "Returned Object / Java Exception",
								RETURNED_OBJECT = "Returned Object",
								RETURNED_EXCEPTION = "Java Exception";
	
	//Command messages
	private static final String COMMAND_DEFAULT = "Object returningEval(String command, int returnCount)";
	
	//Proxy that communicates with Matlab
	
	private RemoteMatlabProxy _proxy;
	
	public RemoteExample()
	{
		//Frame title
		super("Remote Session Example");
		
		//Build the User Interface (UI)
		
		//Main panel to hold everything else
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setPreferredSize(MAIN_PANEL_SIZE);
		mainPanel.setSize(MAIN_PANEL_SIZE);
		this.add(mainPanel);
		
		//Connection panel, button to connect, progress bar
		final JPanel connectionPanel = new JPanel();
		connectionPanel.setBackground(mainPanel.getBackground());
		connectionPanel.setBorder(BorderFactory.createTitledBorder(STATUS_DISCONNECTED));
		connectionPanel.setPreferredSize(CONNECTION_PANEL_SIZE);
		connectionPanel.setSize(CONNECTION_PANEL_SIZE);
		mainPanel.add(connectionPanel, BorderLayout.NORTH);
		final JButton connectionButton = new JButton("Connect");
		connectionPanel.add(connectionButton);
		final JProgressBar connectionBar = new JProgressBar();
		connectionPanel.add(connectionBar);

		//Command panel to build eval statement
		JPanel commandPanel = new JPanel();
		commandPanel.setBackground(mainPanel.getBackground());
		commandPanel.setSize(COMMAND_PANEL_SIZE);
		commandPanel.setPreferredSize(COMMAND_PANEL_SIZE);
		commandPanel.setBorder(BorderFactory.createTitledBorder(COMMAND_DEFAULT));
		
		//Place connection and command panel into the same panel
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(connectionPanel, BorderLayout.NORTH);
		controlPanel.add(commandPanel, BorderLayout.SOUTH);
		mainPanel.add(controlPanel, BorderLayout.NORTH);
		
		//Command field
		final JTextField commandField = new JTextField();
		commandField.setColumns(16);
		commandField.setText("sqrt(5)");
		commandPanel.add(commandField);
		
		//returnCount field
		final JFormattedTextField returnCountField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		returnCountField.setColumns(2);
		returnCountField.setText("1");
		commandPanel.add(returnCountField, BorderLayout.CENTER);
		
		//Invoke button, when pressed sends the command to Matlab
		final JButton invokeButton = new JButton("Invoke");
		invokeButton.setEnabled(false);
		commandPanel.add(invokeButton);
		
		//Create a scroll pane for the results / exceptions
		final JTextArea returnArea = new JTextArea();
		returnArea.setEditable(false);
		final JScrollPane returnPane = new JScrollPane(returnArea);
		returnPane.setBackground(mainPanel.getBackground());
		returnPane.setPreferredSize(RETURN_PANEL_SIZE);
		returnPane.setSize(RETURN_PANEL_SIZE);
		returnPane.setBorder(BorderFactory.createTitledBorder(RETURNED_DEFAULT));
		mainPanel.add(returnPane, BorderLayout.CENTER);
		
		//Create proxy factory, receives proxy, and updates UI
		MatlabProxyFactoryOptions options= new MatlabProxyFactoryOptions.Builder()
			        .setMatlabLocation("/usr/local/MATLAB/R2014a/bin/matlab").build();
		final RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory(options);
		//RemoteMatlabProxy proxy = (RemoteMatlabProxy) factory.getProxy();
		factory.addConnectionListener(new MatlabConnectionListener()
		{
			//When the connection is established, store the proxy, update UI
			@SuppressWarnings("unused")
			public void connectionEstablished(RemoteMatlabProxy proxy)
			{
				_proxy = proxy;
				
				connectionPanel.setBorder(BorderFactory.createTitledBorder(STATUS_CONNECTED));
				connectionBar.setValue(100);
				connectionBar.setIndeterminate(false);
				invokeButton.setEnabled(true);
			}

			//When the connection is lost, null the proxy, update UI
			@SuppressWarnings("unused")
			public void connectionLost(RemoteMatlabProxy proxy)
			{
				_proxy = null;

				connectionPanel.setBorder(BorderFactory.createTitledBorder(STATUS_DISCONNECTED));
				returnPane.setBorder(BorderFactory.createTitledBorder(RETURNED_DEFAULT));
				returnArea.setText("");
				connectionBar.setValue(0);
				connectionButton.setEnabled(true);
				invokeButton.setEnabled(false);
			}
		});

		//Connect to Matlab when the Connect button is pressed
		connectionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					//Request a proxy, to be received by MatlabConnectionListener
					factory.requestProxy(null);
					
					//Update GUI
					connectionPanel.setBorder(BorderFactory.createTitledBorder(STATUS_CONNECTING));
					connectionBar.setIndeterminate(true);
					connectionButton.setEnabled(false);
				}
				catch (MatlabConnectionException exc)
				{
					returnPane.setBorder(BorderFactory.createTitledBorder(RETURNED_EXCEPTION));
					returnArea.setText(formatException(exc));
					returnArea.setCaretPosition(0);
				}
			}
		});

		//Submit request to Matlab when the Invoke button is pressed
		invokeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					//Parse out the returnCount number, default to 0
					int returnCount = 0;
					try
					{
						returnCount = Integer.parseInt(returnCountField.getText());
					}
					catch (NumberFormatException exc) { }
					
					//Submit request to Matlab, display result
					Object result = _proxy.returningEval(commandField.getText(), returnCount);
					returnPane.setBorder(BorderFactory.createTitledBorder(RETURNED_OBJECT));
					returnArea.setText(formatResult(result, 0));
					returnArea.setCaretPosition(0);
				}
				//If exception is encountered, display exception
				catch (MatlabInvocationException exc)
				{
					returnPane.setBorder(BorderFactory.createTitledBorder(RETURNED_EXCEPTION));
					returnArea.setText(formatException(exc));
					returnArea.setCaretPosition(0);
				}
			}
		});
		
		//On closing, exit Matlab
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if(_proxy != null)
				{
					try
					{
						_proxy.exit();
					}
					catch (MatlabInvocationException exc) { }
				}
			}
		});
		
		//Display this JFrame
		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	/**
	 * Format the exception into a string that can be displayed.
	 * 
	 * @param exc the exception
	 * @return the exception as a string
	 */
	private static String formatException(Exception exc)
	{
		StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exc.printStackTrace(printWriter);
        try
        {
        	stringWriter.close();
        }
        catch (IOException ex) { }

        return stringWriter.toString();
	}
	
	//Class signatures of base type arrays
	private static final Class BOOLEAN_ARRAY = new boolean[0].getClass(),
							   DOUBLE_ARRAY = new double[0].getClass(),
							   FLOAT_ARRAY = new float[0].getClass(),
							   BYTE_ARRAY = new byte[0].getClass(),
							   SHORT_ARRAY = new short[0].getClass(),
							   INT_ARRAY = new int[0].getClass(),
							   LONG_ARRAY = new long[0].getClass(),
							   CHAR_ARRAY = new char[0].getClass();
	
	/**
	 * Takes in the result from MATLAB and turns it into an easily readable format.
	 * 
	 * @param result
	 * @param level, pass in 0 to initialize, used recursively
	 * @return
	 */
	private static String formatResult(Object result, int level)
	{
		//Message to the built
		String msg = "";
		
		//Tab offset for levels
		String tab = "";
		for(int i = 0; i < level+1; i++)
		{
			tab += "  ";
		}
		
		//If the result is null
		if(result == null)
		{
			msg += "null encountered" + "\n";
		}
		//If the result is an array
		else if(result.getClass().isArray())
		{
			//Check for all of the different types of arrays
			//For each array type, read out all of the array elements
			Class classType = result.getClass();
			
			if(classType.equals(BOOLEAN_ARRAY))
			{
				boolean[] array = (boolean[]) result;
				msg += "boolean array, length = " + array.length + "\n";	
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", boolean: " + array[i] + "\n";
				}
			}
			else if(classType.equals(DOUBLE_ARRAY))
			{
				double[] array = (double[]) result;
				msg += "double array, length = " + array.length + "\n";
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", double: " + array[i] + "\n";
				}
			}
			else if(classType.equals(FLOAT_ARRAY))
			{
				float[] array = (float[]) result;
				msg += "float array, length = " + array.length + "\n";		
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", float: " + array[i] + "\n";
				}
			}
			else if(classType.equals(BYTE_ARRAY))
			{
				byte[] array = (byte[]) result;
				msg += "byte array, length = " + array.length + "\n";
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", byte: " + array[i] + "\n";
				}
			}
			else if(classType.equals(SHORT_ARRAY))
			{
				short[] array = (short[]) result;
				msg += "short array, length = " + array.length + "\n";
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", short: " + array[i] + "\n";
				}
			}
			else if(classType.equals(INT_ARRAY))
			{
				int[] array = (int[]) result;
				msg += "int array, length = " + array.length + "\n";
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", int: " + array[i] + "\n";
				}
			}
			else if(classType.equals(LONG_ARRAY))
			{
				long[] array = (long[]) result;
				msg += "long array, length = " + array.length + "\n";
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", long: " + array[i] + "\n";
				}
			}
			else if(classType.equals(CHAR_ARRAY))
			{
				char[] array = (char[]) result;
				msg += "char array, length = " + array.length + "\n";
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", char: " + array[i] + "\n";
				}
			}
			//Otherwise it must be an array of Objects
			else
			{
				Object[] array = (Object[]) result;
				msg += "Object array, length = " + array.length + "\n";
				for(int i = 0; i < array.length; i++)
				{
					msg += tab + "index " + i + ", " + formatResult(array[i], level+1);
				}
			}
		}
		//If an Object and not an Array
		else
		{
			msg += result.getClass().getCanonicalName() + ": " + result + "\n";
		}
		
		return msg;
	}
}