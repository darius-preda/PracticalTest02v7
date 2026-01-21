package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread extends Thread{

    private int port;
    private ServerSocket serverSocket;
    private boolean isRunning = true;

    public Map<String, String> cache;

    public ServerThread(int port) {
        this.port = port;
        this.isRunning = false;
        this.cache = new HashMap<String, String>();
    }

    @Override
    public void run() {
        try {
            Log.i("ServerThread", "Starting server on port " + port);
            serverSocket = new ServerSocket(port);
            isRunning = true;

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();

                CommunicationThread communicationThread = new CommunicationThread(this, clientSocket);
                communicationThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
