package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class CommunicationThread extends Thread {
    private Socket clientSocket;
    private ServerThread serverThread;

    private static final String TAG = "CommunicationThread";


    public CommunicationThread(ServerThread serverThread, Socket clientSocket) {
        this.serverThread = serverThread;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            Log.d(TAG, "Client connected, waiting for request...");

            String request = reader.readLine();
            Log.d(TAG, "Received request: " + request);

            if (request == null || request.isEmpty()) {
                Log.e(TAG, "Empty request received");
                writer.println("Error: Empty request");
                return;
            }

            //proceseaza cererea
            String key = "localhost";

            if (request.contains("set,")) {
                String[] parts = request.split(",");
                String hour = parts[1];
                String minutes = parts[2];
                String seconds = "00";
                String AlarmTime = hour + ":" + minutes + ":" + seconds;
                serverThread.cache.put(key, AlarmTime);
                Log.d(TAG, "Set alarm time to " + AlarmTime);
            } else if (request.contains("reset")) {
                serverThread.cache.remove(key);
                Log.d(TAG, "Reset alarm time");
            } else if (request.contains("poll")) {
                String nistTime = getNISTTime();
                Log.i(TAG, "NIST Time: " + nistTime);
                String alarmTime = serverThread.cache.get("localhost");

                if (alarmTime == null) {
                    Log.i(TAG, "No alarm time set");
                    writer.println("none\n");
                    return;
                }

                int result = alarmTime.compareTo(nistTime);

                if (result > 0) {
                    Log.i(TAG, "Alarm time is in the future");
                    writer.println("active\n");
                } else if (result < 0) {
                    Log.i(TAG, "Alarm time is in the past");
                    writer.println("inactive\n");
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in CommunicationThread", e);
            if (writer != null) {
                writer.println("Error: " + e.getMessage());
            }
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientSocket != null) clientSocket.close();
                Log.d(TAG, "Client connection closed");
            } catch (Exception e) {
                Log.e(TAG, "Error closing resources", e);
            }
        }
    }

    private String getNISTTime() {
        Socket nistSocket = null;
        BufferedReader nistReader = null;

        try {
            Log.d(TAG, "Connecting to " + Constants.NIST_SERVER_HOST + ":" + Constants.NIST_SERVER_PORT);

            nistSocket = new Socket();
            nistSocket.connect(
                    new InetSocketAddress(Constants.NIST_SERVER_HOST, Constants.NIST_SERVER_PORT),
                    Constants.NIST_CONNECTION_TIMEOUT
            );

            Log.d(TAG, "Connected to NIST server successfully");

            nistReader = new BufferedReader(
                    new InputStreamReader(nistSocket.getInputStream())
            );

            String nistTime = nistReader.readLine();
            nistTime = nistReader.readLine();
            String serverTime = nistTime.split(" ")[2];
            Log.i(TAG, "Raw NIST response: " + serverTime);

            return serverTime;

        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Connection to NIST server timeout", e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to NIST server", e);
            return null;
        } finally {
            try {
                if (nistReader != null) nistReader.close();
                if (nistSocket != null && !nistSocket.isClosed()) {
                    nistSocket.close();
                }
                Log.d(TAG, "NIST connection closed");
            } catch (Exception e) {
                Log.e(TAG, "Error closing NIST socket", e);
            }
        }
    }
}
