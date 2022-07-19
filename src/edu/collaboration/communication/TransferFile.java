package edu.collaboration.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.File;

public class TransferFile extends Socket {
	public boolean timeOut;
	
    public TransferFile(String ip, int port) throws Exception {
        super(ip, port);
        timeOut = false;
        this.setSoTimeout(60*60*10000);// 10 hour

        System.out.println("Cliect[port:" + this.getLocalPort() + "] connect to the Server.");
    }
 

    public void sendFile(String path) throws Exception {
        FileInputStream fis = null;
        DataOutputStream dos = null;
        
        try {
            File file = new File(path);
            if(file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(this.getOutputStream());
 
                // File name and length
                //dos.writeUTF(file.getName());
                //dos.flush();
                //dos.writeLong(file.length());
                //dos.flush();
 
                // start to transfer
                System.out.println("======== Start to send files ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100*progress/file.length()) + "% |");
                }
                System.out.println();
                System.out.println("======== Files tranferred successfully ========");
            }
            else
            {
                System.out.println("File does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fis != null)
            {
                fis.close();
            }
            if(dos != null)
            {
            	dos.flush();
            	dos.close();
            }
                //dos.close();
            //this.close();
        }
        System.out.println("s: Socket close? " + this.isClosed());
    }
    
    public void receiveFile(String filePath)
    {
    	byte[] inputByte = null;
		int length = 0;
		DataInputStream dis = null;
		FileOutputStream fos = null;
		File file = null;
		try 
		{
			try 
			{
                System.out.println("r: Socket close? " + this.isClosed());
				dis = new DataInputStream(this.getInputStream());
				File f = new File(filePath);
				if(!f.exists())
				{
					f.mkdir();  
				}
				file = new File(filePath);
				if(file.exists())
				{
					fos = new FileOutputStream(file);    
					inputByte = new byte[1024];   
					System.out.println("Start to receive result data...");  
					while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) 
					{
						fos.write(inputByte, 0, length);
						fos.flush();    
					}
					this.timeOut = false;
					System.out.println("Complete receiving result data: " + filePath);
				}
				else
				{
					System.out.println("File does not exist: " + filePath);
				}
			} 
			finally 
			{
				if (fos != null)
					fos.close();
				if (dis != null)
					dis.close();
				//this.close();
			}
		} 
		catch(SocketTimeoutException e){  
			this.timeOut = true;
            System.out.println("Time out, No response from the server.");  
        } 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
