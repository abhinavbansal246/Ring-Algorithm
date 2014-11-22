
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;


public class RingImplement extends Thread {
    int pid, portNo, delay, ID;
    ServerSocket listenSock;
    Socket socket;
    Timer clock;
    GuiShell guiBox;
    boolean active = true, restart,  init = true;


    RingImplement(int ID, int portNo, GuiShell guiElement, boolean restart) {
        this.pid = ID;
        this.portNo = portNo;
        this.guiBox = guiElement;
        this.ID = ID;
        this.restart = restart;

        delay = ID*4* 1000;
    }

    public void timer() throws IOException{
    	listenSock = new ServerSocket(portNo); 
        listenSock.setSoTimeout(delay);
    }
    
    public String polls() {
        String msg = "Election - " + pid;
        guiBox.outputStatus("Process " + pid + " : " + msg);
        return msg;
    }
    public void run() {
    	  
        try {
        	timer();
            sendToSock();
       } catch (IOException ex) {
            Logger.getLogger(RingImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean startElection(boolean bool){
    	 String message = polls();
         pushMsg(message);
         bool = false;
         return bool;
    	}
    
    public void sendToSock() throws IOException {
        while (active) {
            try {
                if (restart == true) {
              restart =  startElection(restart);
                }
                if (pid == 1 && init == true) {
                	init = startElection(init);
                }
                socket = listenSock.accept(); 
                BufferedReader intoProcess = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receiveMsg = intoProcess.readLine(); 
                System.out.println("Process: " + pid + " " + receiveMsg);
                decodeMessage(receiveMsg);

            } catch (SocketTimeoutException x) {
                String message = polls(); 
                pushMsg(message);
    
            } finally {
                    socket.close();
            }
        }
    }

    public int newPort(int ID) {
        int portNxt = ID + 50000;
        if (ID == 6) {
            portNxt = 50000;
        }
        return portNxt + 1;
    }

    public int lastPro(int ID) {
        int lastPort = ID;
        if (lastPort == 1) {
            lastPort = 7;
        }
        return lastPort - 1;
    }
    
    @SuppressWarnings({ "unused", "deprecation" })
	public void decodeMessage(String text) {
        String message = text, header, proStart = "any", proStart2;
        int cordPro = 0;
        String elec = "Election"; String crash = "Crash";String restart = "Restart";String cord = "Co-ordinator";String alive = "Alive";
        StringTokenizer decodeMsg = new StringTokenizer(text);
        header = decodeMsg.nextToken();
			switch (header) {
			
			case "Election":
            if (text.length() > 12) {
                proStart = text.substring(11, 12);
            }
            if (!proStart.equals(String.valueOf(pid))) {
                pushMsg(message + "," + pid);
                guiBox.outputStatus("Process " + pid + " : " + message + "," + pid);
            } else {
                for (int k = 11; k < text.length(); k++) {
                    if (cordPro < Integer.parseInt(text.substring(k, k + 1))) {
                        cordPro = Integer.parseInt(text.substring(k, k + 1));
                    }
                    k++;
                }
                message = "Co-ordinator - " + cordPro;
                pushMsg(message);
                guiBox.outputStatus("Process " + pid + " : " + message);
            }
			break;
			case "Co-ordinator":
            proStart2 = text.substring(text.length() - 1);
            if (!proStart2.equals(String.valueOf(pid))) {
                pushMsg(message);
                guiBox.outputStatus("Process " + pid + " : " + message);
            } else {
                if (clock == null) {
                    clock = new Timer();
                    clock.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            String mssg = "Alive - " + pid;
                            pushMsg(mssg);
                        }
                    }, 0, 4 * 1000);
                }
            }
			break;
		case "Alive":

            String creatorProcess = text.substring(text.length() - 1);
            if (clock != null && pid < Integer.parseInt(creatorProcess)) {
                clock.cancel();
            }
            if (lastPro(Integer.parseInt(creatorProcess)) != pid) {
                pushMsg(text);
            } else {
                pushMsg("Process Alive");
            }
			break;
			case "Crash":
            try {
                listenSock.close();
                if (clock != null) {
                    clock.cancel();
                }
                active = false;
                guiBox.outputStatus("Process " + pid + ": Crashed");
                this.stop();
            } catch (IOException ex) {
                Logger.getLogger(RingImplement.class.getName()).log(Level.SEVERE, null, ex);
            }
			break;
			case "Restart":
                ID = pid;
		  break;
      
		}
    }
    
    //-------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("static-access")
	public void pushMsg(String message) {
        try {
            int port = newPort(ID);
            socket = new Socket("127.0.0.1", port);
            PrintWriter toProcess = new PrintWriter(socket.getOutputStream());
            this.sleep(150);
            toProcess.println(message);
            toProcess.close();
            socket.close();
        } catch (ConnectException ex) {
            if (ID == 6) {
                ID = 0;
            }
            ID++;
            pushMsg(message);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RingImplement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RingImplement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(RingImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

