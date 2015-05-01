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
	//create proxy to connect to matlab
	
	File file;
	static double class1;
	static String classname;
	static String resultfiles[];
	static double prec;
	static double prob_score[];
	static double orig_class[];
	static RemoteMatlabProxy proxy;
	static String file_abs_path;
	public static void remoteJava() throws MatlabConnectionException, MatlabInvocationException
	{
	    MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).setHidden(true).build();
		RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory(options);
		proxy = (RemoteMatlabProxy)factory.getProxy();
		
				
				//vid_gui v = new vid_gui();
//				final filechoose FC = new filechoose();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						creatGUI();
					}
				
				});
				
				
				
	}
	JButton Browse;
	JButton Analyze;
	JTextArea fileText;
	JFileChooser fc;
	JTextArea TA;
	static JFrame frame;
	
	public filechoose()
	{
		super(new BorderLayout());
		//creating the log to feed it to the action listeners
		fileText = new JTextArea(1,60);
		fileText.setMargin(new Insets(5,5,5,5));
		fileText.setEditable(false);
		JScrollPane scrPane = new JScrollPane(fileText);
		
		
		//creating a new file chooser
		fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MP4 File","mp4");
		fc.setFileFilter(filter);
		TA = new JTextArea(10,50);
		TA.setEditable(false);
		JScrollPane scrPane2 = new JScrollPane(TA);
		//create the browse button for the file selection
		Browse = new JButton("Browse");
		Browse.addActionListener(this);
		Analyze = new JButton("Analyze");
		Analyze.addActionListener(this);
		
		
		
		//System.out.println("video object:" + (double[])retunArgs[0]);
		//for layout pursoses putting buttons in a separate panel
		JPanel fcPanel = new JPanel(); // use FlowLayout
		fcPanel.add(Browse);
		fcPanel.add(Analyze);
		//create a separate panel - putting the open button and the text field in separate panel
		JLabel label1 = new JLabel("Select your file from here");
		add(fcPanel,BorderLayout.EAST);
		add(scrPane,BorderLayout.WEST);
	    add(label1,BorderLayout.PAGE_START);
	    add(scrPane2,BorderLayout.SOUTH);
	    
	    //JList sim_list_scroll = new JList(sim_vid);
		//add(sim_list_scroll);
        
		
		//TA.setBounds(1, 1, 1, 1);
		
		
	}
	

	public void actionPerformed(ActionEvent e)
	{
		//handling the browse button action
		if(e.getSource() == Browse)
		{
			int returnFile = fc.showOpenDialog(filechoose.this);
			if(returnFile == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();
				file_abs_path = file.getAbsolutePath();
			    fileText.append(file_abs_path);
			}
		}
		if(e.getSource() == Analyze)
		{
			    String test_video = file_abs_path;
			    //String str = "vid_obj = VideoReader('"+file_abs_path+"')";
			    try {
					//proxy.eval(str);
					//proxy.eval("B = vid_obj.Height");
					//proxy.eval("X = vid_obj.Duration");
					/*proxy.eval("class = 'vid_action'");
					proxy.eval("sim_videos = cell{4,1}");*/
			    	//proxy.eval("addpath('/home/jyotsna/Copy/evensem-1/Computer Vision/project/code');");
			    	int num_of_instances = 10;
			    	String dataset = "H";
			    	String feature_type = "STIP";
			    	//String command = "[resultfiles,classname,score,path] = vid('"+test_video+"',"+num_of_instances+",'"+dataset+"','"+feature_type+"');";
			    	//System.out.println(command);
			    	
			    	//proxy.eval(command);
			    	String command2 = "[ predictedclassname,sim_vids,score,orig_class,precision] =ret_similar('actioncliptest00083',10,'H','STIP')";
			    	System.out.println(command2);
			    	//proxy.eval("cd '/home/jyotsna/VideoClassification/ClassificationScripts'");
			    	proxy.eval("addpath('/home/jyotsna/VideoClassification/ClassificationScripts');");
			    	proxy.eval(command2);
					//classname = ((String) proxy.getVariable("predictedclassname"));
					class1 = ((double[]) proxy.getVariable("predictedclassname"))[0];
					resultfiles = ((String[]) proxy.getVariable("sim_vids"));
					prob_score  = ((double[]) proxy.getVariable("score"));
					orig_class  = ((double[]) proxy.getVariable("orig_class"));
					prec  = ((double[]) proxy.getVariable("precision"))[0];
					//path = ((double[])proxy.getVariable("X"))[0];
					//Object[] retunArgs = proxy.returningEval("B", 1);
					String str3 = "Class Name of the video:" + class1 + '\n';
					//String str1 = "Height,duration: " + result1 +"; "+ result2 + '\n';
					//String str2 = "video object:" + (double[])retunArgs[0] + '\n';
					
                    TA.setText("Video Analysis for the video " + file.getName() + ":\n");
                    TA.append(str3);
                    //TA.append(str1);
                    //TA.append(str2);
                    TA.append("\n\nSimilar videos for the video are: \n");
                    //proxy.eval("addpath('/home/jyotsna/Copy/evensem-1/Computer Vision/project/code');");
			    	//proxy.eval(command);
                    
                    for(int k = 0;k<resultfiles.length;k++)
                    {
                    	String str5 = resultfiles[k]+'\n';
                    	TA.append(str5);
                    	
                    }
                                
                                     
                    //JButton play_button = new JButton("Play");
                    
                    
				} 
			    catch (MatlabInvocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    //proxy.eval("vid_obj = VideoReader('/home/jyotsna/Downloads/hollywood2_actioncliptrain00001_h264.mp4')");
				
			    
			
			fileText.setCaretPosition(fileText.getDocument().getLength());
		}
	}
	
	// create the GUI and show it finally
	public static void creatGUI()
	{
		frame = new JFrame("Video Analysis");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		//add content to the window
		filechoose FC = new filechoose();
		frame.add(FC);
		//FC.setAlignmentX(BOTTOM_ALIGNMENT);
		
		
		
		
		//display the window
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String args[]) throws MatlabConnectionException, MatlabInvocationException
	{
		remoteJava();
		
	}
 	
}
	