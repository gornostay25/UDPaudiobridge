package loc.gornostay25.audiobridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.util.Log;
import java.util.regex.*;
import android.widget.TextView;

public class MainActivity extends Activity 
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView ips = findViewById(R.id.ips);
        ips.setText(getip());
    }

    public void clickC(View v)
    {
        startForegroundService(new Intent(this,AudioResiver.class));

//Log.i("lll", getip());

        //  startSpeakers();

    }

    public void clickD(View v)
    {

        stopService(new Intent(this,AudioResiver.class));


    }


public String getip(){
    String ret = "";
    try {
        // Executes the command.
        Process process = Runtime.getRuntime().exec("/system/bin/ip -4 a ls up ");

        // Reads stdout.
        // NOTE: You can write to stdin of the command using
        //       process.getOutputStream().
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));
        int read;
        char[] buffer = new char[4096];
        StringBuffer output = new StringBuffer();
        while ((read = reader.read(buffer)) > 0) {
            output.append(buffer, 0, read);
        }
        reader.close();

        // Waits for the command to finish.
        process.waitFor();

        
        final String regex = "\\s(?!127)[0-9]{0,3}\\.[0-9]{0,3}\\.[0-9]{0,3}\\.(?!255)[0-9]{0,9}";
        final String string = output.toString();

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            ret = ret + matcher.group(0) + "\n";
            
        }
        if(ret == ""){
            return("offline");
        }
        return (ret);
    } catch (IOException e) {
        throw new RuntimeException(e);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}










//    public void startSpeakers()
//    {
//        // Creates the thread for receiving and playing back audio
//        int mono = AudioFormat.CHANNEL_IN_MONO;
//        int stereo = AudioFormat.CHANNEL_IN_STEREO;
//        String lencn = "";
//        if (CHANNEL == mono)
//        {
//            //  bfsze = SAMPLE_RATE*2;
//            lencn = "mono";
//
//        }
//        else if (CHANNEL == stereo)
//        {
//            lencn = "stereo";
//        }
//
//        if (!speakers)
//        {
//            //         final Toast stoptost = Toast.makeText(this, "This is a toast from Thread", Toast.LENGTH_SHORT);
//            Toast.makeText(getApplicationContext(), "buf size " + bfsze + " channels " + lencn, Toast.LENGTH_SHORT).show();
//            speakers = true;
//            Thread receiveThread = new Thread(new Runnable() {
//
//                    @Override
//                    public void run()
//                    {
//                        // Create an instance of AudioTrack, used for playing back audio
//                        Log.i(LOG_TAG, "Receive thread started. Thread id: " + Thread.currentThread().getId());
//
//                        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL,
//                                                          AudioFormat.ENCODING_PCM_16BIT, bfsze, AudioTrack.MODE_STREAM);
//                        track.play();
//                        try
//                        {
//                            // Define a socket to receive the audio
//                            DatagramSocket socket = new DatagramSocket(port,InetAddress.getByName("0.0.0.0"));
//
//                            byte[] buf = new byte[bfsze];
//                            while (speakers)
//                            {
//                                // Play back the audio received from packets
//                                DatagramPacket packet = new DatagramPacket(buf, bfsze);
//                                socket.receive(packet);
//                                Log.i(LOG_TAG, "Packet received: " + packet.getLength());
//
//                                
//                                track.write(packet.getData(), 0, bfsze);
//                            }
//                            // Stop playing back and release resources
//                            socket.disconnect();
//                            socket.close();
//                            track.stop();
//                            track.flush();
//                            track.release();
//                            //  stoptost.show();
//
//                            return;
//                        }
//                        catch (SocketException e)
//                        {
//
//                            Log.e(LOG_TAG, "SocketException: " + e.toString());
//                            speakers = false;
//                        }
//                        catch (IOException e)
//                        {
//
//                            Log.e(LOG_TAG, "IOException: " + e.toString());
//                            speakers = false;
//                        }
//                    }
//
//
//
//
//
//
//
//
//                });
//            receiveThread.start();
//        }
//        else
//        {
//
//            Toast.makeText(getApplicationContext(), "running", Toast.LENGTH_SHORT).show();
//
//        }
//
//	}



    /*

     public void startMic() {
     // Creates the thread for capturing and transmitting audio
     mic = true;
     try
     {
     address = InetAddress.getByName("192.168.8.101");
     }
     catch (UnknownHostException e)
     {}
     Thread thread = new Thread(new Runnable() {

     @Override
     public void run() {
     // Create an instance of the AudioRecord class
     Log.i(LOG_TAG, "Send thread started. Thread id: " + Thread.currentThread().getId());
     AudioRecord audioRecorder = new AudioRecord (MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE,
     AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 
     AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)*10);
     int bytes_read = 0;
     int bytes_sent = 0;
     byte[] buf = new byte[bfsze];
     try {
     // Create a socket and start recording
     Log.i(LOG_TAG, "Packet destination: " + address.toString());
     DatagramSocket socket = new DatagramSocket();
     //socket.setBroadcast(true);
     audioRecorder.startRecording();
     while(mic) {
     // Capture audio from the mic and transmit it
     bytes_read = audioRecorder.read(buf, 0, bfsze);
     DatagramPacket packet = new DatagramPacket(buf, bytes_read, address, port);
     socket.send(packet);
     bytes_sent += bytes_read;
     Log.i(LOG_TAG, "Total bytes sent: " + bytes_sent);
     Thread.sleep(20, 0);

     }
     // Stop recording and release resources
     audioRecorder.stop();
     audioRecorder.release();
     socket.disconnect();
     socket.close();
     mic = false;
     return;
     }

     catch(InterruptedException e) {

     Log.e(LOG_TAG, "InterruptedException: " + e.toString());
     mic = false;
     }
     catch(SocketException e) {

     Log.e(LOG_TAG, "SocketException: " + e.toString());
     mic = false;
     }
     catch(UnknownHostException e) {

     Log.e(LOG_TAG, "UnknownHostException: " + e.toString());
     mic = false;
     }
     catch(IOException e) {

     Log.e(LOG_TAG, "IOException: " + e.toString());
     mic = false;
     }
     }
     });
     thread.start();
     }



     */






}
