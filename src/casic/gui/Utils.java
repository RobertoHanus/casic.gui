/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic.gui;

/**
 *
 * @author SpongeBob
 */
public class Utils {
    public static byte[] changeEndianess(byte[] bytes)
    {
        byte[] output = new byte[bytes.length];
        for(int i = 0; i<bytes.length;i++)
        {
            output[i]=bytes[bytes.length-1-i];
        }
        return output;
    }
}
