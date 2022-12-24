import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

class Cli_sender extends Thread{
	Socket connecting_socket;
	public Cli_sender(Socket cs){
		this.connecting_socket=cs;
		
	}
	@Override
    public void run(){
        while(true){
            try {
                DataOutputStream to_server=new DataOutputStream(connecting_socket.getOutputStream());
                DataInputStream from_server=new DataInputStream(connecting_socket.getInputStream());
                BufferedReader u_inp=new BufferedReader(new InputStreamReader(System.in));
                String uinp=u_inp.readLine();
                System.out.println(uinp.substring(0, 4));
                
                String command="";
                    
                int fn_index=0;
                if(uinp.substring(0, 4).equals("send")){
                    fn_index=5;
                    command="send";
                }
                else if(uinp.substring(0, 3).equals("get")){
                    fn_index=4;
                    command="get";
                }
                String filename=uinp;
                System.out.println("filelengtsis: "+filename);
                File file=new File(filename.substring(fn_index,filename.length()-3));
                if(command.equals("send")){
                    
                    FileInputStream fin=new FileInputStream(file);
                    
                    System.out.println("filelengtsis: "+Long.toString(file.length()));
                    
                    to_server.writeBytes(filename);
                    to_server.writeLong(file.length());
                    //to_server.flush();
                    //to_server.writeBytes(uinp);
                    //
                    
                    
                    int bytes;
                    byte[] buffer=new byte[4*1024];
                    
                    while((bytes=fin.read(buffer))!=-1){
                        to_server.write(buffer,0,bytes);
                        to_server.flush();
                    }
                    System.out.println("file sent!");
                    fin.close();
                }
                else if(command.equals("get")){
                    byte[] buffer=new byte[4*1024];
                    
                    to_server.writeBytes(filename);
                    
                    long size=from_server.readLong();
                        
                    System.out.println("tying to get this work:"+size);
                        
                    FileOutputStream foup=new FileOutputStream(file); 
                    System.out.println("here::"+filename);
                    int bytes=0;
                    System.out.println("size is:"+Long.toString(size));
                    while(size>0&&(bytes = from_server.read(
                        buffer, 0,
                        (int)Math.min(buffer.length, size)))
                        != -1){
                        foup.write(buffer,0,bytes);
                        size-=bytes;
                        System.out.println("remaining:"+Long.toString(size/1000)+"kb");
                    }
                    foup.close();
                    System.out.println("done");

                }
            }
			 catch (Exception e) {
				// TODO: handle exception
			}
        }
    }
}



class ftp_client{
    public static void main(String argv[])throws Exception{

        Socket cli_socket=new Socket(argv[0],6969);
        if(cli_socket.isConnected()){
            System.out.print("connected to the server\n");
        }
        Cli_sender sender=new Cli_sender(cli_socket);
        sender.start();
    }
}