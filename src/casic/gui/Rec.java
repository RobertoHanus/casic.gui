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

    public boolean isEnd() {
        return !alive;
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

        short elapsedInterByte = 0;
        short elapsedIRG = 0;
        boolean endOfFile = false;
        while (elapsedIRG < 30000) {
            while (true) {
                chunk = new Chunk();
                chunk.setType("data");
                // chunk.setLength(132);
                long startIRG = System.currentTimeMillis();

                elapsedIRG = 0;
                while (serialPort.bytesAvailable() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Rec.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    elapsedIRG = (short) (System.currentTimeMillis() - startIRG);
                    if (elapsedIRG > 30000) {
                        endOfFile = true;
                        break;
                    }
                };
                if (endOfFile) {
                    break;
                }

                chunk.setAux(elapsedIRG + elapsedInterByte);
                // while (commPort.bytesAvailable() < chunk.getLength());

                byte[] buffer = new byte[1000];
                // commPort.readBytes(buffer, chunk.getLength());

                int j;
                for (j = 0; j < 1000; j++) {
                    byte[] single = new byte[1];
                    serialPort.readBytes(single, 1);
                    buffer[j] = single[0];
                    long startInterByte = System.currentTimeMillis();
                    elapsedInterByte = 0;
                    while (serialPort.bytesAvailable() == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Rec.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        elapsedInterByte = (short) (System.currentTimeMillis() - startInterByte);
                        if (elapsedInterByte > 5000) {
                            break;
                        }
                    }
                    if (elapsedInterByte > 16.666 * 5) {
                        break;
                    }
                }

                chunk.setLength(j + 1);
                // commPort.readBytes(buffer, chunk.getLength());
                chunk.setData(buffer);
                chunkList.add(chunk);
                // System.out.println(String.format("0x%01X", chunk.getData()[2]));
                progress++;
                // if (chunk.getData()[2] == (byte) 0xFE) {
                if (elapsedInterByte > 5000) {
                    break;
                }
            }
        }

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(filePath);
            for (Chunk chunk_ : chunkList.list()) {
                try {
                    outputStream.write(chunk_.array());
                } catch (IOException ex) {
                    Logger.getLogger(Rec.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            outputStream.close();
            serialPort.closePort();
            alive = false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rec.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
