
// You can use this file as a starting point for your dictionary client
// The file contains the code for command line parsing and it also
// illustrates how to read and partially parse the input typed by the user. 
// Although your main class has to be in this file, there is no requirement that you
// use this template or hav all or your classes in this file.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

//
// This is an implementation of a simplified version of a command
// line dictionary client. The only argument the program takes is
// -d which turns on debugging output.
//


public class CSdict {
    static final int MAX_LEN = 255;
    static Boolean debugOn = false;

    private static final int PERMITTED_ARGUMENT_COUNT = 1;
    private static String command;
    private static String[] arguments;

    static String currentDict = "*";


    //boolean flags
    private static boolean connected = false;
    private static boolean connectionOpen =false;



    // socket connection fields
    static Socket dictSocket;
    static PrintWriter out; // request
    static BufferedReader in; // response from server
    static BufferedReader stdIn;
    static boolean start = true;

    public static void main(String [] args) {


        while(start) {

        byte cmdString[] = new byte[MAX_LEN];
            String fromUser;
            String fromServer;

            int len;



        // Verify command line arguments

        if (args.length == PERMITTED_ARGUMENT_COUNT) {
            debugOn = args[0].equals("-d");
            if (debugOn) {
                System.out.println("Debugging output enabled");
            } else {
                System.out.println("997 Invalid command line option - Only -d is allowed");
                return;
            }
        } else if (args.length > PERMITTED_ARGUMENT_COUNT) {
            System.out.println("996 Too many command line options - Only -d is allowed");
            return;
        }


        // Example code to read command line input and extract arguments.


            try {
                System.out.print("csdict> ");
                System.in.read(cmdString);


                // Convert the command string to ASII
                String inputString = new String(cmdString, "ASCII");



                // TODO Ignore any lines that start with #
                if(inputString.startsWith("#")){

                }


                // Split the string into words
                String[] inputs = inputString.trim().split("( |\t)+");
                if(inputs.length > 255){
                    throw new IOException();
                }


                // Set the command
                command = inputs[0].toLowerCase().trim();
                // Remainder of the inputs is the arguments.
                arguments = Arrays.copyOfRange(inputs, 1, inputs.length);


                //QUIT PROGRAM for some reason == "quit" doesn't work



                dictSocket = new Socket();

                    if(isCommandValid(command)) {
                        processCommand(command, arguments);
                    }

            } catch (IOException exception) {
                System.err.println("998 Input error while reading commands, terminating.");
               // System.exit(-1);
            }catch (Exception e){
                System.err.println(e.getMessage());
               /// System.exit(-1);
            }
        }
    }

    public static void openConnection(String[] args){



        if(args.length>2 || args.length<=1){
            incorrectNumArgs();
            return;
        }


        String portNumber = args[1];
        String serverName = args[0];

        try{
            if(portNumber.matches(".*[a-z].*")){
               invalidArgs();
                return;
            }
//            if(!serverName.equals("dict.org")){
//                processingError("Timed out while waiting for a response.");
//                return;
//            }
            int port = Integer.parseInt(portNumber);

            // new socket connection

            dictSocket = new Socket(serverName, port);
            //out = output stream
            out = new PrintWriter(dictSocket.getOutputStream(), true);

            // in = input stream
            in = new BufferedReader(new InputStreamReader(dictSocket.getInputStream()));

            stdIn = new BufferedReader(new InputStreamReader(System.in));
            currentDict = "*";

            String fromServer = in.readLine();

            if(!fromServer.startsWith("220")){
                TimeUnit.SECONDS.sleep(3);
                processingError("220 not received, not a dictionary server");
            }


            System.out.println("<-- "+fromServer);
            connected =true;
            connectionOpen = true;
        }catch (IOException | InterruptedException e){
            System.out.println("920 Control connection to "+serverName+" on port "+portNumber+" failed to open");
        } finally {
            return;
        }


    }

    // Decode the user-inputted command and handle errors
    // sout specific method () ;
    public static void processCommand(String command, String[] args) throws IOException {

        switch(command){
            case "open":
                openConnection(args);
                break;
            case "dict":
               if(args.length>0){
                   incorrectNumArgs();
                   return;
               }

               showDict();
                break;
            case "set":
                setDict(args);// +DICTIONARY
                break;
            case "define": // +WORD
                defineWord(args);
                break;
            case "match":
                matchWord(args);
                break;
            case "close":
                closeConnection();
                break;
            case "prefixmatch":
                prefixMatch(args); // +WORD
                break;
        }
    }

    private static void closeConnection() throws IOException {
        String fromServer;

        out.println("QUIT");

        if(debugOn){
            isDebug("QUIT");
            System.out.println("<-- " + in.readLine());
        }


        in.close();
        out.close();
        dictSocket.close();
        connectionOpen = false;
        connected = false;

    }

    private static void prefixMatch(String[] args) throws IOException {
        String fromServer;
        String word = args[0];
        System.out.println(args.length);

        if (args.length != 1) {
            incorrectNumArgs();
            return;
        }
        if(debugOn){
            isDebug("PREFIXMATCH WORD");
        }
        System.out.println("> PREFIXMATCH " +currentDict +" " +"prefix " +word);
        out.println("MATCH" +" " +currentDict +" " + "prefix "  +word);

        out.println("");
        out.flush();
        String next = "";

        while ((fromServer = in.readLine()) != null) {
            if (fromServer.startsWith("552")){
                System.out.println("*****No matching word(s) found*****");
                break;
            }

            System.out.println(fromServer);

            if (fromServer.equals(".")) {
                next = in.readLine();
                break;
            }
        }
        System.out.println( next);
    }


    private static void matchWord(String[] args) throws IOException {
        String fromServer;
        String word = args[0];
        System.out.println(args.length);

        if (args.length != 1) {
            incorrectNumArgs();
            return;
        }
        if(debugOn){
            isDebug("MATCH WORD");
        }
        System.out.println("> MATCH " +currentDict +" " +"exact " +word);
        out.println("MATCH" +" " +currentDict +" " + "exact "  +word);

        out.println("");
        out.flush();
        String next = "";
        String last = "";

        while ((fromServer = in.readLine()) != null) {
            if (fromServer.startsWith("552")){
                System.out.println("*****No matching word(s) found*****");
                break;
            }

            System.out.println(fromServer);

            if (fromServer.equals(".")) {
                next = in.readLine();
                break;
            }
        }
        System.out.println( next);
    }


    // retrieve and print all definitions for input word as specified through setDict.
    // if can't be found, print message then MATCH using server command.
    // if no match, print another message
    private static void defineWord(String[] args) throws IOException {
        if (args.length != 1) {
            incorrectNumArgs();
            return;
        }

        String fromServer;
        String word = args[0];
        out.println("DEFINE " + currentDict + " " + word);
        out.flush();


        if (debugOn) {
            System.out.println("> " + "DEFINE " + currentDict + " " + word);
        }

        StringBuilder contentReceived = new StringBuilder();
        while ((fromServer = in.readLine()) != null) {
            contentReceived.append(fromServer);
            contentReceived.append(System.lineSeparator());
            if (fromServer.startsWith("250")) {
                contentReceived.append(fromServer);
                System.out.println(fromServer);
                contentReceived.append(System.lineSeparator());
                break;
            }
            // TODO subsequent calls, following a fail, needs to go back to define, before match
            else if (fromServer.startsWith("552")) {
                System.out.println("***No definition found*** \n");
                out.println("MATCH " + currentDict + " " + ". " + word);
                fromServer = in.readLine();
                } if (fromServer.startsWith("152")) {

                    while ((fromServer = in.readLine()) != null) {
                        // clear contentReceived which contains 552 response code
                        contentReceived.delete(0,contentReceived.length());
                        contentReceived.append(fromServer);
                        contentReceived.append(System.lineSeparator());
                        if (fromServer.startsWith("250")) {
                            contentReceived.append(fromServer);
                          //  System.out.println(fromServer);
                            contentReceived.append(System.lineSeparator());
                            break;
                        }
                        break;
                    }
                } else if (fromServer.startsWith("552")) {
                System.out.println("****No matches found****");
                break;
            }


        }
        System.out.println(contentReceived.toString());
        }



    private static void setDict(String[] args) {
        if (args.length > 1) {
            incorrectNumArgs();
            return;
        }
        currentDict = args[0];

    }


        public static boolean isCommandValid (String command) throws Exception {

            switch (command) {

                case "open":
                    if(connectionOpen){
                        commandNotExpected();
                        return false;
                    }
                    return true;
                case "quit":
                    quit();
                    return true;
                case "dict":
                case "set":
                case "define":
                case "match":
                case "prefixmatch":
                    if(connected){
                        return true;
                    } else {
                      commandNotExpected();
                      return false;
                    }
                case "close":
                    if(connected){
                        return true;
                    }
                    commandNotExpected();
                    return false;

                default:
                    System.out.println("900 Invalid command");
                    return false;
            }

        }

        public static void showDict () throws IOException {

            String fromServer;
            out.println("SHOW DB");
            out.flush();
            String next = "";
            String last = "";

            if(debugOn){
                isDebug("SHOW DB");
            }
            last = in.readLine();
            System.out.println("<-- " + last);
            while ((fromServer = in.readLine()) != null) {
                // fromServer = in.readLine();

                // while loop was hanging so added this to break it,
                // doesnt show the final response code though so might not be right
                System.out.println(fromServer);
                if (fromServer.equals(".")) {
                    next = in.readLine();
                    break;
                }
            }
            System.out.println("<-- " + next);
        }

        public static void quit() throws IOException {
                if(connected){
                   isDebug("QUIT");
                    closeConnection();
                }
                start = false;

        }

        public static void incorrectNumArgs(){
            System.out.println("901 Incorrect number of arguments");
        }

        public static void invalidArgs(){
            System.out.println("902 Invalid argument");
        }

        public static void commandNotExpected(){
            System.out.println("903 Supplied command not expected at this time");
        }

        public static void processingError(String errorText){
            System.out.println("999 Processing error. " + errorText);
        }

        public static void isDebug(String serverMessage){
            if(debugOn){
                System.out.println("> " + serverMessage);
            } else{
                return;
            }
        }





}
    
    
