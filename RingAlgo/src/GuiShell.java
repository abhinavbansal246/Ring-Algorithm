import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


//  ----------------------------------------------------------------Created by Abhinav Bansal --------------------------------------------------------

public class GuiShell {



	RingImplement process;
	//----------------------------------------------------------------- declaration--------------------------
	private static JFrame MainWindow = new JFrame();        								
	private static JButton Start = new JButton("Start Process");			
	private static JButton B_crash = new JButton("crash");			
	private static JButton B_restart = new JButton("restart");					
	private static JLabel L_Message = new JLabel("Select process ");
	private static JLabel L_Conv = new JLabel();
	public static JTextArea TA_status = new JTextArea();				
	public static JScrollPane SP_Conversation = new JScrollPane();			
    public static JComboBox jComboBox1 = new javax.swing.JComboBox();

	public static void main(String args[]){ //----------------- main method (because it has to be)
		BuildMainWindow();
		Initialise();
	}


	private static void Initialise() { // we need to start the window
		
		
		Start.setEnabled(true);
		
	}

	
	private static void BuildMainWindow() { // where else will all the buttons go
		MainWindow.setTitle("main");
		MainWindow.setSize(100, 500);
		MainWindow.setVisible(true);
		ConfigurationMainWindow();
		algoRithm();
		
	}

	
	 public void startAction(java.awt.event.ActionEvent evt) { // we need to start
	       
	        for (int i = 1; i < 6; i++) {
	        	process = new RingImplement(i, 50000 + i, this, false);
	            process.start();
	            jComboBox1.addItem("Process " + i);
	        }
	    }
	 
	 
		public void restartAction(java.awt.event.ActionEvent evt) { // it handles the restart button
	        // TODO add your handling code here:
	        for (int i = 1; i < 6; i++) {
	            try {
	                Socket socket = new Socket("127.0.0.1", 50000 + i);
	                PrintWriter pw = new PrintWriter(socket.getOutputStream());
	                pw.println("Restart");
	                pw.close();
	                socket.close();
	            } catch (ConnectException ex) {
	                System.out.println("Process " + i + " is crashed");
	            } catch (UnknownHostException ex) {
	                Logger.getLogger(GuiShell.class.getName()).log(Level.SEVERE, null, ex);
	            } catch (IOException ex) {
	                Logger.getLogger(GuiShell.class.getName()).log(Level.SEVERE, null, ex);
	            }
	        }

	        String restart = jComboBox1.getSelectedItem().toString();
	        int ID = Integer.parseInt(restart.substring(restart.length() - 1));
	        process = new RingImplement(ID, 50000 + ID, this, true);
	        process.start();
	        outputStatus("Process " + ID + "has been restarted");


	    }
	
	private static void algoRithm() { // YES here is the ring algo
			
		Start.addActionListener(
				new java.awt.event.ActionListener()
				{
					@Override
					public void actionPerformed(java.awt.event.ActionEvent evt)
					{
						 GuiShell gui = new GuiShell();
						gui.startAction(evt);
					}
			
}
				);

		//_______________________________________________CRASH BUTTON_________________________________________________________
		B_crash.addActionListener( 
				new java.awt.event.ActionListener()
				{
					@Override
					public void actionPerformed(java.awt.event.ActionEvent evt)
					{
						 crashActionPerform(evt);
					}

					private void crashActionPerform(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
				        // TODO add your handling code here:
				        String crash = jComboBox1.getSelectedItem().toString();
				        try {
				            Socket socket = new Socket("127.0.0.1", 50000 + Integer.parseInt(crash.substring(crash.length() - 1)));
				            PrintWriter pw = new PrintWriter(socket.getOutputStream());
				            pw.println("Crash");
				            pw.close();
				            socket.close();
				        } catch (UnknownHostException ex) {
				            Logger.getLogger(GuiShell.class.getName()).log(Level.SEVERE, null, ex);
				        } catch (IOException ex) {
				            Logger.getLogger(GuiShell.class.getName()).log(Level.SEVERE, null, ex);
				        }
				    }
}
				);
	
			
		B_restart.addActionListener(
				new java.awt.event.ActionListener()
				{
					@Override
					public void actionPerformed(java.awt.event.ActionEvent evt)
					{
						GuiShell gui = new GuiShell();
						gui.restartAction(evt);
					}

		
}
				);
		
	}
	
	 public void outputStatus(String text) {
	        TA_status.append(text);
	        TA_status.append("\n");
	        TA_status.append("\n");

	    }


	private static void ConfigurationMainWindow() { // Configuring the main window---------------------------------------------------------------------------
	
		
		MainWindow.setSize(375,350);
		MainWindow.getContentPane().setLayout(null);
		B_restart.setBounds(250, 40, 81, 25);
		B_crash.setBounds(250, 10, 81, 25);
		Start.setBounds(13, 270, 323, 25);
		//Start.setBackground(new Color(152,251,152));
		
		
		MainWindow.getContentPane().add(B_restart);
		MainWindow.getContentPane().add(B_crash);
		MainWindow.getContentPane().add(Start);
		MainWindow.getContentPane().add(L_Message);
		MainWindow.getContentPane().add(L_Conv);
		MainWindow.getContentPane().add(jComboBox1);
		jComboBox1.setBounds(120, 10, 90,20);
		L_Message.setBounds(10, 10, 160, 20);
		L_Conv.setBounds(100,70,140,16);
		
		TA_status.setColumns(20);
		TA_status.setRows(5);
		TA_status.setEditable(false);
		
		SP_Conversation.setViewportView(TA_status);
		MainWindow.getContentPane().add(SP_Conversation);
		SP_Conversation.setBounds(10, 90, 330, 180);
		MainWindow.getContentPane().add(SP_Conversation);	
	}

}
