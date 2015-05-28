import java.io.*;
import java.util.*;
import java.net.*;

class Server extends Thread {
	HashMap<String,PrintWriter> hmap;
	volatile boolean flag=true;
	ServerSocket serverSocket;
	Socket s;
	Listener1 listener;
	Broadcaster1 broadcaster;
	Server(int port) throws Exception{
		
		hmap=new HashMap<String,PrintWriter>();
		serverSocket=new ServerSocket(port);
		listener=new Listener1(this);
		broadcaster=new Broadcaster1(this);
		start();
		
		BufferedReader brc=new BufferedReader(new InputStreamReader(System.in));
		while(true){
			System.out.println("enter 1-view childs , 2-send to child");
			switch(Integer.parseInt(brc.readLine())){
				case 1:	Set<String> set = hmap.keySet();
						for(String me : set)
							System.out.println(me);
						System.out.println();
						break;
				case 2:	System.out.println("to which child?");
						String str=brc.readLine();
						System.out.println("message?");
						String msg=brc.readLine();
						if(msg.charAt(0)=='T'){broadcaster.broadcast(msg,str,1);}
						else if(msg.charAt(0)=='H'){broadcaster.broadcast(msg,str,2);}
						else if(msg.charAt(0)=='L'){broadcaster.broadcast(msg,str,3);}
						else if(msg.charAt(0)=='P'){broadcaster.broadcast(msg,str,4);}
						else { broadcaster.broadcast(msg,str,5); }
						break;
			}
		}
		
	}
	
	public void run(){
		while(true){
			try{
				if(flag){
					s=serverSocket.accept();
					flag=false;
					new Thread(listener).start();
					new Thread(broadcaster).start();
				}
			}
			catch(Exception e){}  
		} 
	}
	  
	public static void main(String[] args) throws Exception {
			new Server(Integer.parseInt(args[0]));
	} 
}

class Listener1 implements Runnable {
	Server server;
 
	public Listener1(Server server){
		this.server=server;
	}
 
	public void run() {
	    try{
			BufferedReader br=new BufferedReader(new InputStreamReader(server.s.getInputStream()));
			while(!br.ready()) {}
			String name=br.readLine();
			server.hmap.put(name,new PrintWriter(server.s.getOutputStream(),true));
			server.flag=true;
				
			while(true){
				while(!br.ready());
				System.out.println(name+" : "+br.readLine());
			}   
	    }
	    catch(Exception e){}
  }
  
}


class Broadcaster1 implements Runnable{
	Server server;
	volatile int send_flag;
	String msg,dest;
	int priority;
	
	Queue queue1,queue2,queue3,queue4,queue5;
	ArrayList<Queue<String>> alist;
	
	public Broadcaster1(Server server){
		this.server=server;
		send_flag=0;
		msg="";
		dest="";
		priority=0;
		queue1=new LinkedList();
		queue2=new LinkedList();
		queue3=new LinkedList();
		queue4=new LinkedList();
		queue5=new LinkedList();
		alist=new ArrayList<Queue<String>>();
		alist.add(queue1);
		alist.add(queue2);
		alist.add(queue3);
		alist.add(queue4);
		alist.add(queue5);
	}
 
	public void run() {
	    try{
			while(true)
			{
				if(send_flag==1)
				{
					

					int i=0;
					String str="",msg="";
					for(i=0;i<5;i++)
					{
						

						if(alist.get(i).peek()!=null && !alist.get(i).peek().isEmpty())
						{

							
							msg=alist.get(i).poll();
							String[] temp=msg.split(":");
							str=temp[0];
							msg=temp[1];
							break;
						}

						
					}
					if(i==5)continue;
					server.hmap.get(str).println(msg);
					
				}
			}
	    }
	    catch(Exception e){}
  }
  public void broadcast(String msg,String dest,int priority)
  {
	send_flag=0;
	
	this.msg=msg;
	this.dest=dest;
	this.priority=priority;
	
	if(priority==1)
	{
		queue1.offer(dest+":"+msg);
	}
	else if(priority==2)
	{
		queue2.offer(dest+":"+msg);
		
	}
	else if(priority==3)
	{
		queue3.offer(dest+":"+msg);
	}
	else if(priority==4)
	{
		queue4.offer(dest+":"+msg);
	}
	else
	{
		queue5.offer(dest+":"+msg);
	}
send_flag=1;
	
  }
  
}