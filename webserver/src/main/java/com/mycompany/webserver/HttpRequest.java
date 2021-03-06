/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.webserver;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author robertrook
 */
public class HttpRequest implements Runnable{
    final static String CRLF = "\r\n";//For convenience
    Socket socket;
 
 // Constructor
public HttpRequest(Socket socket) throws Exception
{
    this.socket = socket;
}
 
 // Implement the run() method of the Runnable interface.
@Override
public void run()
{
    try {
        processRequest();
    } 
    catch (Exception e) {
    System.out.println(e);
    }
}
 
private void processRequest() throws Exception
{
    String httpMethod;
    InputStream is = socket.getInputStream();
    DataOutputStream os = new DataOutputStream(
    socket.getOutputStream());
  
  // Set up input stream filters.
  
    BufferedReader br = new BufferedReader(
    new InputStreamReader(is));
   
    String requestLine = br.readLine();
   
    System.out.println();  //Echoes request line out to screen
    System.out.println("Robert 9 "+requestLine);
   
   //The following obtains the IP address of the incoming connection.
    InetAddress incomingAddress = socket.getInetAddress();
    String ipString= incomingAddress.getHostAddress();
    System.out.println("The incoming address is:   " + ipString);
   //String Tokenizer is used to extract file name from this class.
    StringTokenizer tokens = new StringTokenizer(requestLine);
    
    httpMethod = tokens.nextToken();  // skip over the method, which should be “GET”
    if (httpMethod.equals("GET")){
        System.out.println("Robert 10: httpMethod = "+httpMethod);
        String fileName = tokens.nextToken();
        // Prepend a “.” so that file request is within the current directory.
        // Robert even afgevinkt
        fileName = "." + fileName;
   
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) { //While the header still has text, print it
            System.out.println(headerLine);
        }
   
   
        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
        fis = new FileInputStream(fileName);
        System.out.println("Robert 1 fileName = "+fileName);
        System.out.println("Robert 2 directory ="+System.getProperty("user.dir"));
        } 
        catch (FileNotFoundException e) {
            fileExists = false;
            System.out.println("Robert 3 fileName = "+fileName);
            System.out.println("Robert 4 directory ="+System.getProperty("user.dir"));
        
        }   
 
   //Construct the response message
        String statusLine = null; //Set initial values to null
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK: ";
            contentTypeLine = "Content-Type: " +
            contentType(fileName) + CRLF;
            System.out.println("Robert 5 fileName = "+fileName);
            System.out.println("Robert 6 directory ="+System.getProperty("user.dir"));
        } 
        else {
            statusLine = "HTTP/1.1 404 Not Found: ";
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML> <HEAD><TITLE>Not Found</TITLE></HEAD> <BODY>Not Found Robert Rook WebServer</BODY></HTML>";
            System.out.println("Robert 7 fileName = "+fileName);
            System.out.println("Robert 8 directory ="+System.getProperty("user.dir"));
        }
   //End of response message construction
 
   // Send the status line.
        os.writeBytes(statusLine);
 
   // Send the content type line.
        os.writeBytes(contentTypeLine);
 
   // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);
   
   // Send the entity body.
        if (fileExists) {
        sendBytes(fis, os);
        fis.close();
        } 
        else {
            os.writeBytes(entityBody);
        }
    }
    else if (httpMethod.equals("POST")){
        // verwerk POST request
        System.out.println("Robert 11 POST requestline = "+ requestLine );
        // nu POST request verwerken en iets terugsturen
        //os.writeBytes(requestLine);
        Map<String, String> postMap = getPostDataMap(requestLine);
        //os.writeBytes(postMap.toString());
        for(Object objname:postMap.keySet()) {
            System.out.println("Robert 17 "+objname);
            System.out.println("Robert 18 "+ postMap.get(objname));
            os.writeBytes("key = "+objname+" ");
            os.writeBytes("value = "+postMap.get(objname));
            os.writeBytes("\n");
        }
        
    }
    else {
        // Nu een request ongelijk aan GET of POST
        System.out.println("Robert 12 httpMethod = "+ httpMethod);
        System.out.println("Robert 13 requestLine = "+ requestLine);
        os.writeBytes("This is no GET or POST request");
    }
   
    os.close(); //Close streams and socket.
    br.close();
    socket.close();
 
}
 
//Need this one for sendBytes function called in processRequest
private static void sendBytes(FileInputStream fis, OutputStream os)
throws Exception
{
   // Construct a 1K buffer to hold bytes on their way to the socket.
   byte[] buffer = new byte[1024];
   int bytes = 0;
 
   // Copy requested file into the socket’s output stream.
   while((bytes = fis.read(buffer)) != -1 ) {
      os.write(buffer, 0, bytes);
   }
}
private static String contentType(String fileName) {
    if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
        return "text/html";
    if(fileName.endsWith(".jpg"))
        return "text/jpg";
    if(fileName.endsWith(".gif"))
        return "text/gif";
    return "application/octet-stream";
}

public static Map<String, String> getPostDataMap(String postString) {
        Map<String, String> postDataMap = new HashMap<String, String>();

        postString = postString.substring(0, postString.length()-8);
        String[] parts = postString.split("\\?");
        String part1 = parts[0]; 
        String part2 = parts[1];
        postString = part2;
        
        for (String postReq : postString.split("&")) {
            String[] nameValue = postReq.split("=");

            if (nameValue.length > 1) {
                postDataMap.put(nameValue[0], nameValue[1]);
                System.out.println("Robert14 nameValue[0] = "+ nameValue[0]);
                System.out.println("Robert15 nameValue[1] = "+ nameValue[1]);
            } else {
                postDataMap.put(nameValue[0], null);
                System.out.println("Robert16 nameValue[0] = "+ nameValue[0]);
            }
        }

        return postDataMap;
    }


    
    
}
