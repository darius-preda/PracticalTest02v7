package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private static final String TAG = "ClientThread";

    private String serverAddress;
    private int serverPort;
    private TextView resultTextView;

    private String command;

    public ClientThread(String serverAddress, int serverPort, TextView resultTextView, String command) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.resultTextView = resultTextView;
        this.command = command;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            Log.d(TAG, "Connecting to server: " + serverAddress + ":" + serverPort);

            socket = new Socket(serverAddress, serverPort);
            Log.d(TAG, "Connected to server");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Log.d(TAG, "Sending request: " + command);
            writer.println(command);

            String response = reader.readLine();
            Log.i(TAG, "Response received: " + response);

            if (resultTextView != null) {
                resultTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextView.setText(response);
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in ClientThread", e);

            if (resultTextView != null) {
                resultTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextView.setText("Error: " + e.getMessage());
                    }
                });
            }

        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
                Log.d(TAG, "Connection closed");
            } catch (Exception e) {
                Log.e(TAG, "Error closing connection", e);
            }
        }
    }
}