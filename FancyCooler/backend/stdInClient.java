/** CIS542: Fancy Cooler: Java stdIn client
Yayang Tian, Xiao Zhang, Jiehua Zhu, Tianming Zheng

This class acts as a debug client before the implementation of Android client.
it receives command from stdIn and tells chip to update temperature;
It also receives status signals from Arduino board and print as System.out.println(...).
*/

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.lang.Thread;

/**
* This reads socket from C server and print
*/
class ReadServer implements Runnable{
    private final BufferedReader readServer;
    ReadServer(BufferedReader reader){
        this.readServer=reader;
    }
    public void run(){
        //read socket from C server and print
        try{
            String str;
            while((str = readServer.readLine()) != null){
                System.out.println(str);
            }
            }catch(IOException e) {
            System.err.println("Cannot read from server!");
            e.printStackTrace();
        }
    }
}


/**
* This read command and send to C Server
*/
class ReadClientCommand implements Runnable{
    private final BufferedReader fromStdIn;
    private final PrintWriter toServer;
    ReadClientCommand(BufferedReader stdIn,PrintWriter out){
        this.fromStdIn = stdIn;
        this.toServer = out;
    }
    public void run(){
        String userInput;
        //read command and send to C Server
        try{
            while((userInput = fromStdIn.readLine())! = null){
                toServer.println(userInput);
                toServer.flush();
            }
            }catch(IOException e){
            System.err.println("Cannot read lines from user input!");
            e.printStackTrace();
        }
    }
}

/**
* This is fake client providing an seperate interface for Android communication debugging
* It communicates exchanges information with Arduino chip
*/
public class stdInClient {
    public static void main(String[] args) throws IOException, Exception{
        System.out.println("\n------------CIS542 MileStone2-- Back-End--------------------");
        System.out.println("  Tianming Zheng, Jiehua Zhu, Yayang Tian, Xiao Zhang,        |");
        System.out.println("------------------  Sample Usage:----------------------------");
        System.out.println("t30:         Setting the temperature to be 30 degree        | ");
        System.out.println("a:           Returning to the actual temperaturee           | ");
        System.out.println("l1/ l2/ l3:  Setting the light color to be green/blue/red   | ");
        System.out.println("b:           Back to the right color                        | ");
        System.out.println("------------------------------------------------------------\n");

        //Bind to port 12345
        Socket s = new Socket("localhost", 12345);

        //Communcation via Internet
        InputStream in = s.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        //Take action from the keyboard
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));


        Runnable taskServer = new ReadServer(reader);
        Runnable taskClient = new ReadClientCommand(stdIn, out);
        Thread threadServer = new Thread(taskServer);
        Thread threadClient = new Thread(taskClient);
        threadServer.start();
        threadClient.start();
    }
}
