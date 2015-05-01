import java.awt.EventQueue;

import matlabcontrol.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import java.awt.Font;
import java.awt.Color;
import java.awt.Image;

import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.TextArea;

import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JDesktopPane;

import java.awt.Rectangle;

//import com.jgoodies.forms.factories.DefaultComponentFactory;








import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.JSlider;
import javax.swing.JEditorPane;
import javax.swing.Box;

import java.awt.SystemColor;

import javax.swing.UIManager;
import javax.swing.JTable;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;

import java.awt.Dimension;


public class FirstGui extends JPanel implements ActionListener{
	static RemoteMatlabProxy proxy;
	static double class1;
	static String resultfiles[];
	static double prob_score[];
	static double orig_class[];
	static double prec; 
	Object cmboitem;
	
	
	
	
	private JFrame frmVideoAnalysis;
	private JTable table;
	private JTable table_1;
	private JComboBox<Integer> comboBox;
	private JLabel ImLabel1;
	

	JFileChooser fc;
	
	
	File file;
	JButton browse;
	JButton stip;
	JButton dtf;
	TextArea TA1;
	
	static String file_abs_path;
	/**
	 * Launch the application.
	 * @throws MatlabConnectionException, MatlabInvocationException 
	 */
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).setHidden(true).build();
		RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory(options);
		proxy = (RemoteMatlabProxy)factory.getProxy();
			
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FirstGui window = new FirstGui();
					window.frmVideoAnalysis.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FirstGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVideoAnalysis = new JFrame();
		frmVideoAnalysis.getContentPane().setForeground(UIManager.getColor("OptionPane.warningDialog.titlePane.foreground"));
		frmVideoAnalysis.getContentPane().setBackground(new Color(119, 136, 153));
		frmVideoAnalysis.setBackground(SystemColor.desktop);
		frmVideoAnalysis.setBounds(new Rectangle(65, 24, 100, 100));
		frmVideoAnalysis.setTitle("Video Analysis");
		frmVideoAnalysis.setBounds(100, 100, 1290, 685);
		frmVideoAnalysis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmVideoAnalysis.getContentPane().setLayout(null);
		
		fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("AVI File","avi");
		fc.setFileFilter(filter);
		
		TA1 = new TextArea();
		TA1.setForeground(UIManager.getColor("TextArea.foreground"));
		TA1.setBackground(UIManager.getColor("TextArea.background"));
		TA1.setEditable(false);
		TA1.setBounds(22, 63, 181, 59);
		frmVideoAnalysis.getContentPane().add(TA1);
		
		browse = new JButton("Browse");
		browse.addActionListener(this);
		browse.setFont(new Font("Dialog", Font.BOLD, 12));
		browse.setBounds(209, 80, 94, 37);
		frmVideoAnalysis.getContentPane().add(browse);
		
		JLabel Label1 = new JLabel("Select any video to analyze..");
		Label1.setForeground(new Color(255, 215, 0));
		Label1.setBackground(UIManager.getColor("Label.foreground"));
		Label1.setBounds(22, 27, 224, 15);
		frmVideoAnalysis.getContentPane().add(Label1);
		

		dtf = new JButton("DTF Analysis");
		dtf.setBackground(SystemColor.info);
		dtf.setBounds(62, 364, 215, 37);
		frmVideoAnalysis.getContentPane().add(dtf);
		
		JLabel Label2 = new JLabel("STIP results");
		Label2.setForeground(new Color(255, 215, 0));
		Label2.setBounds(345, 17, 306, 35);
		frmVideoAnalysis.getContentPane().add(Label2);
		
		JLabel label4 = new JLabel("DTF results");
		label4.setForeground(new Color(255, 215, 0));
		label4.setBounds(842, 22, 307, 24);
		frmVideoAnalysis.getContentPane().add(label4);
		
		stip = new JButton("STIP Analysis");
		stip.addActionListener(this);
		stip.setBackground(Color.LIGHT_GRAY);
		stip.setBounds(62, 289, 215, 37);
		frmVideoAnalysis.getContentPane().add(stip);
		
		table = new JTable();
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setRowHeight(8);
		table.setBounds(852, 63, 146, 84);
		TableColumn classes = new TableColumn();
		Object c = new Object();
		c.toString();
		c = "classes";
		classes.setHeaderValue(c);
		table.addColumn(classes);
		TableColumn probability = new TableColumn();
		Object p = new Object();
		p.toString();
		probability.setHeaderValue(p);
		table.addColumn(probability);
		frmVideoAnalysis.getContentPane().add(table);
		

		
		
		
		JLabel label5 = new JLabel("Precision Curves");
		label5.setForeground(new Color(255, 215, 0));
		label5.setBounds(651, 127, 237, 28);
		frmVideoAnalysis.getContentPane().add(label5);
		
		Box Box1 = Box.createHorizontalBox();
		Box1.setBorder(new LineBorder(SystemColor.info));
		Box1.setBounds(852, 199, 281, 156);
		frmVideoAnalysis.getContentPane().add(Box1);
		
		JLabel label6 = new JLabel("Select Number of \nsimilar videos  desired.");
		label6.setForeground(new Color(255, 215, 0));
		label6.setBounds(22, 144, 296, 59);
		frmVideoAnalysis.getContentPane().add(label6);
		
		
		Integer[] comboValues = new Integer[]{1,5,10,15,20};
		comboBox = new JComboBox<Integer>(comboValues);
		//comboBox = new JComboBox<Integer>();
		comboBox.setEditable(true);
		comboBox.setBounds(126, 215, 58, 31);
        frmVideoAnalysis.getContentPane().add(comboBox);
        comboBox.addActionListener(this);
		
		ImLabel1 = new JLabel("Precision plot1");
		
		ImLabel1.setAutoscrolls(true);
		ImLabel1.setBounds(362, 170, 253, 192);
		
	
		
		frmVideoAnalysis.getContentPane().add(ImLabel1);
	}
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 * All actions begin here
 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/	
	//@SuppressWarnings("null")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == browse)
		{
			int returnFile = fc.showOpenDialog(FirstGui.this);
			if(returnFile == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();
				file_abs_path = file.getAbsolutePath();
			    TA1.append(file_abs_path);
			}
		//	TA1.setCaretPosition(TA1.getDocument().getLength());
		}
		if(e.getSource() == comboBox)
		{
			cmboitem = comboBox.getSelectedItem();
		}
		if(e.getSource() == stip)
		{
			//String test_video = file_abs_path;
		    try {
		    	int num_of_instances = (int) cmboitem;
		    	System.out.println(cmboitem);
				//int num_of_instances = 10;
		    	String dataset = "H";
		    	String feature_type = "STIP";
		    	String clipname = file.getName();
		    	clipname = clipname.substring(0, clipname.length()-4);
		        //clipname = "actioncliptest00083";
		    	String command2 = "[ predictedclassname,sim_vids,score,orig_class,precision] =ret_similar('"+clipname+"',"+num_of_instances+",'"+dataset+"','"+feature_type+"')";
		    	//String command2 = "[ predictedclassname,sim_vids,score,orig_class,precision] =ret_similar('actioncliptest00083',10,'H','STIP')";
		    	System.out.println(command2);
		    	proxy.eval("addpath('/home/jyotsna/VideoClassification/ClassificationScripts');");
		    	proxy.eval(command2);
				class1 = ((double[]) proxy.getVariable("predictedclassname"))[0];
				resultfiles = ((String[]) proxy.getVariable("sim_vids"));
				prob_score  = ((double[]) proxy.getVariable("score"));
				orig_class  = ((double[]) proxy.getVariable("orig_class"));
				prec  = ((double[]) proxy.getVariable("precision"))[0];
				String str3 = "Class Name of the video:" + class1 + '\n';
				
				
                //TA.setText("Video Analysis for the video " + file.getName() + ":\n");
                //TA.append(str3);
                //TA.append("\n\nSimilar videos for the video are: \n");
                
                for(int k = 0;k<resultfiles.length;k++)
                {
                	String str5 = resultfiles[k]+'\n';
                	//TA.append(str5);
                	
                }
                
                
                
      
        		JScrollPane scrollPane = new JScrollPane();
        		scrollPane.setBounds(335, 63, 237, 84);
        		frmVideoAnalysis.getContentPane().add(scrollPane);

        		String[] columnNames = {"Class Names",
                        "Score"};
        		
        		String ClassNames[] = {" AnswerPhone"," FightPerson"," HandShake"," HugPerson"};
        		int c = (int)class1;
                //data[0][0] = ClassNames[c];
        		Object[][] data = {{"Man",new Integer(0)}};
        		//= {{"Man",new Integer(0)}};
        		data[0][0] = ClassNames[c];
        		//data[0][0] = "Mango";
                //data[0][1] = new Integer(5);    		
        
        		table_1 = new JTable(data, columnNames);
        		scrollPane.setViewportView(table_1);
        		table_1.setRowHeight(20);
        		table_1.setColumnSelectionAllowed(true);
        		table_1.setCellSelectionEnabled(true);
        		

        		
        		ImageIcon imIcon = new ImageIcon("/home/jyotsna/VideoClassification/ClassificationScripts/plot_precision.jpg");
        		
        		
        		Image image = imIcon.getImage(); // transform it 
        		Image newimg = image.getScaledInstance(ImLabel1.getWidth(),ImLabel1.getHeight(),  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        		imIcon = new ImageIcon(newimg);  // transform it back
        		ImLabel1.setIcon(imIcon);
        		

                
                
			} 
		    catch (MatlabInvocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   
			
		    
		
		//fileText.setCaretPosition(fileText.getDocument().getLength());
	
		}
		
	}
}
