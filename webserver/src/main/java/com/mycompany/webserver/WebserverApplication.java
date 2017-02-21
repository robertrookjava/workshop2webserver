/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.webserver;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.*;
import com.mycompany.webserver.HttpRequest;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author robertrook
 */
public class WebserverApplication {
    
    public static void main(String[] args) {
        try {
            int port =80;
            ServerSocket WebSocket = new ServerSocket(port);
     
            while (true) {
      // Listen for a TCP connection request.
                Socket connectionSocket = WebSocket.accept();
      //Construct object to process HTTP request message
                HttpRequest request = new HttpRequest(connectionSocket);
      
      
                Thread thread = new Thread(request);
                thread.start(); //start thread
            }
        }
            catch (IOException e){
            e.printStackTrace();
        }   catch (Exception ex) {
            Logger.getLogger(WebserverApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    
    }
}
