package ro.pub.cs.systems.eim.practicaltest02v7;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PracticalTest02v7MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;
    private Button connectButton = null;
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText commandEditText = null;
    private Button sendCommandButton = null;
    private TextView commandResultTextView = null;

    void initializeViews() {
        serverPortEditText = findViewById(R.id.server_port_edit_text);
        connectButton = findViewById(R.id.connect_button);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        commandEditText = findViewById(R.id.command_edit_text);
        sendCommandButton = findViewById(R.id.send_command_button);
        commandResultTextView = findViewById(R.id.result_text_view);
    }

    void setupListeners() {
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConnectButton();
            }
        });

        sendCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendCommandButton();
            }
        });
    }

    private void handleConnectButton() {
        String serverPort = serverPortEditText.getText().toString();

        if (serverPort.isEmpty()) {
            Toast.makeText(this, "Server port must be filled!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Connecting to server on port: " + serverPort, Toast.LENGTH_SHORT).show();
            int serverPortInt = Integer.parseInt(serverPort);

            ServerThread serverThread = new ServerThread(serverPortInt);
            serverThread.start();

            Toast.makeText(this, "Server started on port: " + serverPort, Toast.LENGTH_SHORT).show();
        }
    }

    void handleSendCommandButton() {
        String clientAddress = clientAddressEditText.getText().toString();
        String clientPort = clientPortEditText.getText().toString();
        String command = commandEditText.getText().toString();

        if (clientAddress.isEmpty() || clientPort.isEmpty() || command.isEmpty()) {
            Toast.makeText(this, "Client connection parameters and command must be filled!", Toast.LENGTH_SHORT).show();
        } else {
            int clientPortInt = Integer.parseInt(clientPort);
            ClientThread clientThread = new ClientThread(clientAddress, clientPortInt, commandResultTextView, command);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // link xml
        setContentView(R.layout.activity_practical_test02v7_main);

        initializeViews();
        setupListeners();
    }
}