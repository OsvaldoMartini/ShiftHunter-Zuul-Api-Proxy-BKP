package com.shifthunter.threads.grp_1;

//A Class used to send a message 
public class Sender 
{ 
 public void send(String msg) 
 { 
     System.out.println("Sending\t"  + msg ); 
     try
     { 
         Thread.sleep(1000); 
     } 
     catch (Exception e) 
     { 
         System.out.println("Thread  interrupted."); 
     } 
     System.out.println("\n" + msg + "Sent"); 
 } 
} 