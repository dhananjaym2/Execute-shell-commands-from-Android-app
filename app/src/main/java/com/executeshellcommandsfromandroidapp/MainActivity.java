package com.executeshellcommandsfromandroidapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private static final String IPTABLES = "iptables";
    private static final String INSERT_FLAG = " -I";
    private static final String INPUT_CHAIN_NAME = " INPUT";
    private static final String DESTINATION_FLAG = " -d";
    private static final String SOURCE_FLAG = " -s";
    private static final String JUMP_FLAG = " -j";
    private static final String ACCEPT = " ACCEPT";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private TextView textView_response_MainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_response_MainActivity = (TextView) findViewById(R.id.textView_response_MainActivity);

    }

    private void executeTerminalCommand() throws IOException {
        try {
            Process su = Runtime.getRuntime().exec("su");//W/System.err: java.io.IOException: java.io.IOException: Error running exec(). Command: [su] Working Directory: null Environment: null
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            Log.v(LOG_TAG, getRuleToInsertInInputChain(1, "216.58.203.206", ACCEPT));
            outputStream.writeBytes(getRuleToInsertInInputChain(1, "216.58.203.206", ACCEPT));
            //outputStream.writeBytes("iptables -A OUTPUT -d 216.58.203.206 -j ACCEPT\n");//java.io.IOException: write failed: EPIPE (Broken pipe)
//            outputStream.writeBytes("iptables -A OUTPUT -j REJECT\n");//java.io.IOException: write failed: EPIPE (Broken pipe)
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
//                throw new InterruptedException(e);
            }
            outputStream.close();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private String getRuleToInsertInInputChain(int insertPosition, String ipAddress, String jumpFlagValue) {
        return addRule(INPUT_CHAIN_NAME, insertPosition, ipAddress, jumpFlagValue + "\n");
    }

    private String addRule(String chainName, int insertPosition, String ipAddress, String jumpFlagValue) {// DESTINATION_FLAG + ""
        // sudo iptables -A INPUT -s 216.58.203.206 -j ACCEPT
        // sudo iptables -I INPUT 1 -s 216.58.203.206 -j ACCEPT
        return IPTABLES + INSERT_FLAG + chainName + insertPosition + SOURCE_FLAG + " " + ipAddress + JUMP_FLAG + jumpFlagValue;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {
                executeTerminalCommand();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
