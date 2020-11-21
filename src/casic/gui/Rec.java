/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic.gui;

import com.fazecast.jSerialComm.SerialPort;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SpongeBob
 */
public class Rec extends Thread {

    private int progress = 0;
    private boolean alive = true;
    private SerialPort serialPort;
    private String filePath;

    public Rec(SerialPort serialPort, String filePath) {
        this.serialPort = serialPort;
        this.filePath = filePath;
    }

    public int getProgress() {
        return progress;
    }

    public void kill() {
        alive = false;
    }

    @Override
    public void run() {
        serialPort.openPort();
        serialPort.setComPortParameters(600, 8, 1, 0);

        ChunkList chunkList = new ChunkList();

        Chunk chunk = new Chunk();
        chunk.setType("FUJI");
        chunk.setLength(0);
        chunk.setAux(0);
        chunk.setData(null);
        chunkList.add(chunk);

        chunk = new Chunk();
        chunk.setType("baud");
        chunk.setLength(0);
        chunk.setAux(600);
        chunkList.add(chunk);

        while (alive) {
            progress++;
            chunk = new Chunk();
            chunk.setType("data");
            chunk.setLength(132);
            long start = System.currentTimeMillis();
            while (serialPort.bytesAvailable() == 0);
            short elapsed = (short) (System.currentTimeMillis() - start);
            chunk.setAux(elapsed);
            while (serialPort.bytesAvailable() < chunk.getLength());
            byte[] buffer = new byte[chunk.getLength()];
            serialPort.readBytes(buffer, chunk.getLength());
            chunk.setData(buffer);
            chunkList.add(chunk);
            // System.out.println(String.format("0x%01X", chunk.getData()[2]));
            if (chunk.getData()[2] == (byte) 0xFE) {
                break;
            }
        }
        OutputStream outputStream=null;
        try {
            outputStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rec.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Chunk chunk_ : chunkList.list()) {
            try {
                outputStream.write(chunk_.array());
            } catch (IOException ex) {
                Logger.getLogger(Rec.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
