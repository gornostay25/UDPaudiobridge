package loc.gornostay25.audiobridge;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.AudioManager;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import android.util.Log;
import java.net.SocketException;
import java.io.IOException;
public class AudioResiver extends Service
{
    public static final String CHANNEL_ID = "ForegroundPlayingChannel";


    public String LOG_TAG="lll";
    public int SAMPLE_RATE = 44100;
    public int CHANNEL = AudioFormat.CHANNEL_OUT_STEREO;
    public int bfsze = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL, AudioFormat.ENCODING_PCM_16BIT);
    // private InetAddress address; // Address to call
    private int port = 8079; 
    public static boolean speakers=true;






    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                                                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Play stream")
            .setContentText(input)
            .setSmallIcon(R.mipmap.nicon)
            .setContentIntent(pendingIntent)
            .build();
        startForeground(63428, notification);
        //do heavy work on a background thread
        
        
        
        
        Thread receiveThread = new Thread(new Runnable() {

                @Override
                public void run()
                {
                    // Create an instance of AudioTrack, used for playing back audio
                    Log.i(LOG_TAG, "Receive thread started. Thread id: " + Thread.currentThread().getId());

                    AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL,
                                                      AudioFormat.ENCODING_PCM_16BIT, bfsze, AudioTrack.MODE_STREAM);
                    track.play();
                    try
                    {
                        // Define a socket to receive the audio
                        DatagramSocket socket = new DatagramSocket(port,InetAddress.getByName("0.0.0.0"));

                        byte[] buf = new byte[bfsze];
                        while (speakers)
                        {
                            // Play back the audio received from packets
                            DatagramPacket packet = new DatagramPacket(buf, bfsze);
                            socket.receive(packet);
                            Log.i(LOG_TAG, "Packet received: " + packet.getLength());


                            track.write(packet.getData(), 0, bfsze);
                        }
                        // Stop playing back and release resources
                        socket.disconnect();
                        socket.close();
                        track.stop();
                        track.flush();
                        track.release();
                        //  stoptost.show();

                        stopSelf();
                    }
                    catch (SocketException e)
                    {

                        Log.e(LOG_TAG, "SocketException: " + e.toString());
                        speakers = false;
                        stopSelf();
                    }
                    catch (IOException e)
                    {

                        Log.e(LOG_TAG, "IOException: " + e.toString());
                        speakers = false;
                        stopSelf();
                    }
                    catch (Exception e){
                        Log.e(LOG_TAG, "Exception: " + e.toString());
                        stopSelf();
                    }
                }








            });
        receiveThread.start();
        

        
        
        
        
        
        
        
       
        
        
        
        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Playing Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }




    













}


