##Assignment DICTIONARY Client
Recent updates
Changes in the assignment description, if any, will be listed here.
One DICT service online is dict.org on the standard port 2628
Updated ****No matching word(s) found**** (equal *'s)
Git References
Read before trying to use GIT: Using GITLinks to an external site.
The online version of the book: Pro Git (Links to an external site.)Links to an external site.
Atlassian Git CheatsheetLinks to an external site.
Git-Tower's Git CheatsheetLinks to an external site.
Java Network Programming Resources
Here is a simple online tutorial for reading and writing data from a socket.  https://docs.oracle.com/javase/tutorial/networking/sockets/ (Links to an external site.)

There are many other ones online.  For much more detailed there is "TCP/IP Sockets in Java" by Calvert and Donahoo.
Do not register for a repo until you have read the assignment and the instructions on using git.  The link for registering for the assignment is https://www.ugrad.cs.ubc.ca/~cs317/php/register-partners.php Links to an external site.


Goals
To learn how to make, in Java, a TCP/IP connection and how to exchange data over the connection.
To study and understand the implementation of an application level protocol.
To learn how to read and implement a well-specified protocol.
Improve your programming and debugging skills as they relate to the use of sockets in Java.
Develop general networking experimental skills.
Special Note
All assignments in this course must compile and run on the Linux undergrad servers provided for student use. If you use a different environment be sure to check that your solution works on the ugrad machines as you will be graded on how it works on the department machines not on your own machine. All instructions for accessing code, building programs, using source code control tools etc, assume you are working on these machines. If you are working in a different environment commands and parameters may be different. Given the diverse collection of machines it is not possible for us to provide instruction and guidance on how to use these tools or their analogues across these varied environments. Pleas for leniency or special consideration of the nature "But it works on my laptop under ...." will not be entertained, so make sure it works on the undergraduate Linux servers and allow time for testing in that environment so that you are confident your code works as expected there. Running code on different machines and environments is a good way to find bugs.

Assignment Overview
In this assignment you will write a Java program that uses the Java socket related classes to create a client program to retrieve definitions from a dictionary server using the client/server protocol described in RFC 2229 (Links to an external site.). The strategies you need to employ are similar to those used for many client applications you are familiar with; like mail programs or HTTP browsers. Your program will read input from the command line and, based on those commands, communicate with a dictionary server to ultimately retrieve word definitions. You will only be implementing a subset of the commands that can be sent to a dictionary server. Note: to facilitate automated testing the output of your program must be exactly as specified. Don't go adding extra information etc.

Your program will provide a simple shell-style interface to the user. This interface will read  lines of input with application  commands and interpret them according to the description below. Whenever the program is expecting input it is to print the the text 'csdict> ' (Note the blank character after the >).  It is to leave the cursor at that position awaiting input. (i.e. the program is not to print 'csdict>' on a line by itself. You may assume that command lines do not have more than 255 characters. Empty lines and lines starting with the character '#' are to be silently ignored, and a new prompt displayed.

The following table describes the commands that can be entered at the command line:

Application Command	Description
open SERVER PORT	Opens a new TCP/IP connection to an dictionary server. The server's name and the port number the server is listening  on are specified by the command's parameters. The server name can be either a domain name or an IP address in dotted form.  Both the SERVER and PORT values must be provided. This command is considered an unexpected command if it is given when a control connection is already open.
dict	Retrieve and print the list of all the dictionaries the server supports. Each line will consist of a single word that is the the name of a dictionary followed by some information about the dictionary. You simply have to print each of these lines as returned by the server.
set DICTIONARY	Set the dictionary to retrieve subsequent definitions and/or matches from. The name of the dictionary is either the first word on one of the lines returned by the dict command or one of the required virtual databases defined in section 3.4 of the RFC. The default dictionary to use if the set command has not been given is "*".  Every time a connection to a dictionary server is made the dictionary to use is reset to "*".  Multiple set commands simply result in a new dictionary to search being set. Multiple set commands do not result in the building of a collection of dictionaries to search. This command does not have to verify that the specified DICTIONARY actually exists.
define WORD	Retrieve and print all the definitions for WORD. WORD is looked up in the dictionary or dictionaries as specified through the set command. For each definition returned print on a single line, and left justified, the name of the dictionary. On the next line start printing the returned definition. If the word can't be found and no definitions are returned You are to print
***No definition found***

on a single line by itself and then do a match using the server's default matching strategy and print all of its responses. If there are no matches for the default matching strategy then you should print, on a line by itself

****No matches found****

match WORD	Retrieve and print all the exact matches for WORD. WORD is looked up in the dictionary or dictionaries as specified through the set command. The responses from the server will consist of one or more lines of form
database word

If no matches are found print *****No matching word(s) found***** on a single line by itself. This command is most useful when you want to determine which dictionary a word occurs in so that you can retrieve a definition for that word from a particular dictionary.

prefixmatch WORD	Retrieve and print all the prefix matches. for WORD. WORD is looked up in the dictionary or dictionaries as specified through the set command. The responses from the server will consist of one or more lines of form database word If the word can't be found simply print
****No matching word(s) found****

on a single line by itself.

close	After sending the appropriate command to the server and receiving a response, closes the established connection and enters a state where the next command expected is an open or quit.
quit	Closes any established connection and exits the program. This command is valid at any time.
A command line is composed of a command, as described above, followed by zero or more parameters. The command is separated from any parameters by one or more spaces and/or tabs. Tabs or spaces at the end of the line are to be ignored. Your dictionary client may detect certain types of errors. When an error is detected the client is to print a single line consisting of a 3 digit number starting with 9 and a short text message. In the list below, the message to print is in bold and it is followed by a description of the error it corresponds to, and if appropriate it describes how to proceed. Note that the client is only to exit when it gets the quit command.

900 Invalid command. This is printed when the command entered by the user is not one of the accepted commands.
901 Incorrect number of arguments. This is printed when the command is valid but the wrong number of arguments is provided. Note this could be the case if there are either too many or too few arguments.
902 Invalid argument. This is printed when the command is valid, and has the proper number of arguments, but one or more the arguments are invalid. For example the second argument of open is a port number so a non-numeric value for the second argument would produce this error.
903 Supplied command not expected at this time. This is printed when the command is valid, but not allowed at this time. For example, when the client first starts the only commands it can accept are open and quit. If it gets any other known command it would print this message. Note that the printing of this message takes priority over error messages 901 and 902. (i.e. even if errors 901 or 902 occur this message is the only one to print.)
920 Control connection to xxx on port yyy failed to open. When an attempt to establish the connection can't be completed within a reasonable time (say 30 seconds), or the socket cannot be created, then print this message, replacing xxx and yyy with the hostname and port number of the dictionary server you are trying to establish the control connection to,.
925 Control connection I/O error, closing control connection.If at any point an error while attempting to read from, or write to, the open control connection occurs, this message is to printed, and the socket closed/destroyed. The client is then to go back to the state were it is expecting an open command.
996 Too many command line options - Only -d is allowed.This is printed if there are too many command line options.
997 Invalid command line option - Only -d is allowed.This is printed a command line option is not -d. The printing of error 996 takes priority over this message.
998 Input error while reading commands, terminating. This error message is printed if an exception is thrown while the client is reading its commands (i.e standard input). After printing this message the client will terminate.
999 Processing error. yyyy. If for some reason you detect an error that isn't described above, print this message and replace yyyy with some appropriate text that briefly describes the error.
Client output
Whenever you send a command to the server it will respond in some fashion. A detailed description of how a server will response can be found in section 2.4 of the RFC but basically responses consist of collections of Status and Text responses. With respect to output for things like lists of dictionaries matches, etc you simply have to print the Text Responses. However prior to the printing of each definition you are to print a line starting with @ followed by a blank and then the name of the dictionary as returned by the server. Such a line might look like:

@ easton "Easton's 1897 Bible Dictionary"

Only print the the Status responses if the -d command line option is provided (see below). For text responses, always print the line the contains nothing but a period on it.

Your program will be run at the command line by doing

java -jar CSdict.jar [-d].

(The supplied Makefile will also run the command if you enter make run)

Your program is to always print the text responses and if the if the -d option is present that output is augmented with all commands sent by the client to the dictionary server must be echoed to standard output with the prefix '>  ' (Note the space after the >). In addition status response messages received  by the client program must be printed with the prefix '<--  ' (Note the space after the <--). Except for the text described above, no other text is to be printed by your program unless it is part of what the  client does or is listed as an error message your program is supposed to print.

If the -d option is not supplied then you are to print only the results and none of the status information.

Remember, You are only required to implement a subset of the protocol so some of the material in the references goes beyond what you need. Keep in mind that the RFC describes the data (protocol) exchanges between the client (i.e. what you are writing) and a dictionary server. It does not describe the commands accepted by your client program, those are described in the table above. Conversely the above table describes the commands the client program accepts and the expected actions and not what needs to be exchanged on the control connection between the client and server to achieve the desired result.

Implementation Constraints and Suggestions
Don't spend too much time in the implementation of the shell. A simple show prompt, read line, identify command, run command is sufficient.

Don't try to implement this assignment all at once. Instead incrementally build and simultaneously test the solution. A suggested strategy is to:

Read the RFC to understand how the protocol works and test your understanding by using netcat or telnet to connect to a server and then issue the commands. One server you can connect to is test.dict.org. The names of other servers will be provided later.
Use the supplied template to create a simple prompt, accept and parse simple commands but don't act on them
Implement the open command, but don't parse the arguments and simply connect to a "hard coded" server.
Next read the arguments and create the connection based on the arguments;
Implement commands like quit that do not require an active connection
Implement the commands to get the list of dictionaries
Implement the remaining commands
Make sure that each partner knows how to open, close, and read/write data on a connection.
For testing purposes you can connect to any dictionary server, like test.dict.org on port 2628. Your program is expected to work with a wide variety of dictionary servers so you should locate other servers and try your program against them. Constantly typing in the commands for your client can become a bit tedious. To reduce your typing load you might want to checkout the command autoexpect on the cs machines. It basically lets you issue a series of commands, record the output and then play the commands back. You would start by doing:

autoexpect java -jar CSdict.jar
In the folder where CSdict.jar lives. You can then type in the sequence of commands that you wish to test and quit at the end. Once you're done autoexpect will generate a script.exp file that you can run by doing

./script.exp
You may discuss this assignment with your classmates, but you must do your work only with your partner. You are not allowed to show your code, even for discussion or explanatory purposes to other classmates.

Your code is to be developed for the Undergrad Linux server machines (e.g. remote.students.cs.ubc.ca). The TAs will test your code on these machines, so test your program extensively in this environment, including your Makefile.

Style and comments are part of the evaluation, so keep your code clear, clean and well-documented. Your code needs to be easy to read, with blank lines delineating blocks of functionality, and avoiding long lines. Use proper comments and names for variables and methods that accurately reflect their purpose. Use methods to deal with long blocks of code and repeated functionality.
