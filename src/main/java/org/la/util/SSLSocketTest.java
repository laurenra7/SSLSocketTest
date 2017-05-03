package org.la.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SSLSocketTest {

    private static boolean modeVerbose;

    private static final int HTTPS_PORT = 443;
    private static final String CHARSET_HTML = "ISO-8859-1";
    private static final String CHARSET_HTML5 = "UTF-8";

    public static void main(String[] args) {

        int exitStatus = 0;
        modeVerbose = false;

        // Build command line options
        Options clOptions = new Options();
        clOptions.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show this help")
                .build());
        clOptions.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("show processing messages")
                .build());

        if(args.length == 0) {
            showCommandHelp(clOptions);
        }
        else {
            exitStatus = processCommandLine(args, clOptions);
        }

        System.exit(exitStatus);

    }


    private static int processCommandLine(String[] args, Options clOptions) {
        int executeStatus = 0;
        String url = "";
        String outputJson = "";

        CommandLineParser clParser = new DefaultParser();


        try {
            CommandLine line = clParser.parse(clOptions, args);

            if (line.hasOption("help")) {
                showCommandHelp(clOptions);
            }
            else {
                if (line.hasOption("verbose")) {
                    modeVerbose = true;
                }

                // Remaining command line parameter(s), if any, is URL
                List<String> cmdLineUrl = line.getArgList();
                if(cmdLineUrl.size() > 0) {
                    url = cmdLineUrl.get(0); // Get only the first parameter as URL, ignore others
                    testSslSocket(url);
                }
                else {
                    System.out.println("Error: no URL");
                    showCommandHelp(clOptions);
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Command line parsing failed. Error: " + e.getMessage() + "\n");
            showCommandHelp(clOptions);
            executeStatus = 1;
        }

        return executeStatus;
    }


    private static void testSslSocket (String url) {

        Socket socket = null;
        Writer socketWriter = null;
        BufferedReader socketReader = null;

        try {
            socket = SSLSocketFactory.getDefault().createSocket(url, 443);

            socketWriter = new OutputStreamWriter(socket.getOutputStream(), CHARSET_HTML);
            socketWriter.write("GET / HTTP/1.1\r\n");
            socketWriter.write("Host: " + url + ":" + HTTPS_PORT + "\r\n");
            socketWriter.write("Agent: SSL-TEST\r\n");
            socketWriter.write("\r\n");
            socketWriter.flush();

            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET_HTML));
            String line = null;
            System.out.println("Start reading...");
            while ((line = socketReader.readLine()) != null) {
                System.out.println(line);
            }

            socketReader.close();
            socketWriter.close();

        } catch (IOException e) {
            System.out.println("IO error opening SSL socket or writing to it or reading from it. " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("IO error closing SSL socket. " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private static void showCommandHelp(Options options) {
        String commandHelpHeader = "\nOpen an SSL Socket and do an HTTP GET\n\n";
        String commandHelpFooter = "\nExamples:\n\n" +
                "  java -jar SSLSocketTest.jar someserver.com\n\n" +
                "  java -jar SSLSocketTest.jar www.somewhere.com\n\n" +
                "  java -jar SSLSocketTest.jar 176.182.12.13\n\n";

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar SSLSocketTest.jar url", commandHelpHeader, options, commandHelpFooter, true);
    }


}
