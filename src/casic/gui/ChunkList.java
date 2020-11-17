/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic.gui;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author SpongeBob
 */
public class ChunkList {

    private ArrayList<Chunk> list = new ArrayList<Chunk>();

    public ChunkList(byte[] stream) {
        ByteBuffer buffer = ByteBuffer.wrap(stream);

        Chunk chunk;
        chunk = new Chunk(buffer);
        while (chunk.getCreationResult()) {
            list.add(chunk);
            chunk = new Chunk(buffer);
        }
    }
    
    public ChunkList() {
    }
    
    public void add(Chunk chunk) {
        list.add(chunk);
    }

    public ArrayList<Chunk> list() {
        return list;
    }
}
