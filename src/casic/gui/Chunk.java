/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic.gui;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author SpongeBob
 */
public class Chunk {

    /* Chunk header */
    private byte[] type = new byte[4];
    private byte[] length = new byte[2];
    /* Little endian stored */
    private byte[] aux = new byte[2];
    /* Little endian stored */

 /* Chunk data */
    private byte[] data;

    /* Chunk length including data and header */
    private short entireChunkLength;

    /* Can create Chunk */
    private boolean creationResult = false;

    @Override
    public String toString() {
        return (new String(type));
    }

    public byte[] getType() {
        return type;
    }

    public short getLength() {
        byte[] shortInteger = Utils.changeEndianess(length);
        return ByteBuffer.wrap(shortInteger).getShort();
    }

    public short getAux() {
        byte[] shortInteger = Utils.changeEndianess(aux);
        return ByteBuffer.wrap(shortInteger).getShort();
    }

    public byte[] getData() {
        return data;
    }

    public short getEntireChunkLength() {
        return entireChunkLength;
    }

    public Chunk(ByteBuffer buffer) {
        creationResult = false;
        if (buffer.remaining() >= 8) {
            buffer.get(type);
            buffer.get(length);
            buffer.get(aux);
            if (buffer.remaining() >= getLength()) {
                data = new byte[getLength()];
                buffer.get(data);
                creationResult = true;
                entireChunkLength = (short) (getLength() + 8);
            }
        }
    }

    public void setType(String string) {
        type = string.getBytes();
    }

    public void setLength(int integer) {
        length = ByteBuffer.allocate(2).putShort((short) integer).array();
        length = Utils.changeEndianess(length);
        entireChunkLength = (short) (getLength() + 8);
    }

    public void setAux(int integer) {
        aux = ByteBuffer.allocate(2).putShort((short) integer).array();
        aux = Utils.changeEndianess(aux);
    }

    public void setData(byte[] data) {
        if (data != null) {
            this.data = data.clone();
        }
    }

    public Chunk() {
    }

    public boolean getCreationResult() {
        return creationResult;
    }

    public byte[] array() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(entireChunkLength);
        byteBuffer.put(type);
        byteBuffer.put(length);
        byteBuffer.put(aux);
        if (getLength() > 0) {
            byteBuffer.put(data);
        }
        return byteBuffer.array();
    }
}
