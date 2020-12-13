/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic.gui;

import com.fazecast.jSerialComm.SerialPort;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SpongeBob
 */
public class Play extends Thread {

    private int progress = 0;
    private boolean alive = true;
    private SerialPort serialPort;
    private String filePath;
    private boolean pause = false;

    public Play(SerialPort serialPort, String filePath) {
        this.serialPort = serialPort;
        this.filePath = filePath;
    }

    public int getProgress() {
        return progress;
    }
    
    public void kill()
    {
        alive=false;
    }
    
    public boolean isEnd()
    {
        return !alive;
    }
    
    public void pause()
    {
        pause = true;
    }  
    
    public void ahead()
    {
        pause = false;
    }
    
    public boolean isPaused()
    {
        return pause;
    }

    @Override
    public void run() {

        try {
            serialPort.openPort();
            serialPort.setComPortParameters(600, 8, 1, 0);

            /* Get file size and create a byte array with data */
            InputStream inputStream = new FileInputStream(filePath);
            int fileSize = (int) (new File(filePath)).length();
            byte[] fileData = new byte[fileSize];
            inputStream.read(fileData);

            /* Creates an array of Chunks */
            ChunkList chunkList = new ChunkList(fileData);

            int i = 1;
            int length = chunkList.list().size();
            for (Chunk chunk : chunkList.list()) {
                if(!alive) break;
                // System.out.println("Chunk Type: " + chunk.toString() + " Chunk " + i + " of " + length);
                progress = (int)((((float)i)/((float)length))*100);
                if (chunk.toString().equals("data")) {
                    for(int j=0;j<chunk.getAux();j+=10)
                    {
                        Thread.sleep(10);
                        while(pause) { 
                            Thread.sleep(1);
                        };
                    }
                    serialPort.writeBytes(chunk.getData(), chunk.getLength());
                    Thread.sleep((int) (chunk.getLength() * 16.666));
                }
                i++;
            }
            inputStream.close();
            serialPort.closePort();
            alive = false;  
        } catch (IOException ex) {
            Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
