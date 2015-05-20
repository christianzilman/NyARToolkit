/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.qualitas.nyartoolkit.respberrypi.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.NyARException;

/**
 *
 * @author IOL
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }
    
    public static void test() throws FileNotFoundException
    {
       try{
            String CARCODE_FILE ="";
            
            // TODO code application logic here
            InputStream i_stream = new FileInputStream("C:/Users/IOL/Documents/Maestría/Para la Tesis/realidad aumentada/NyARToolkit/NyARToolkit.RaspberryPI.sample/src/main/resources/data/patt.hiro");
            
//            while ((len = i_stream.read(buf)) > 0) {
//     
//            }

            NyARCode ar_code = NyARCode.createFromARPattFile(i_stream, 16, 16);
        } catch (NyARException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
}
