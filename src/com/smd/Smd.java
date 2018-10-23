/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author David
 */



public class Smd {
    
    private static final String BUTTON = "BUTTON";
    private static final String MENU = "MENU";

    private static File[] listFiles(String path){
        File dir = new File(path);
        if(dir.isDirectory())
        {
            return dir.listFiles((file, name)->
                {
                    return name.toUpperCase().endsWith(".SMD");
                });
        }
        return new File[0];
    }
    
    private static Stream<String> getFileLines(File file) throws FileNotFoundException{
        var fileReader = new FileReader(file);
        var​ bufferedReader = new BufferedReader(fileReader);
        return bufferedReader.lines();
    }

    private static boolean containsToken(String line){
        for (Token value : Token.values()) {
            if (line.contains(value.toString())) {
                return true;
            }
        }
        return false;
    }
    
    private static String toUnlFormat(String... values){
        var unl = new StringBuilder("");
        for(var value : values){
            unl.append(value);
            unl.append("|");
        }
        return unl.toString();
    }
    
    private static void processLines(BufferedWriter modulos, BufferedWriter comandos, String programa, Iterator<String> lines) throws IOException{
        
        String line = null;
        String menuLabel = "";
        String descripcion = null;
        
        while(lines.hasNext()){
            if(line == null)
                line = lines.next();
            
            if(line.contains(Token.CONTROL.toString()) && line.endsWith(Token.BUTTON.toString())){
                String command = null;
                String label = null;
                String comment = "";
                while(lines.hasNext()){
                    line = lines.next();
                    if(line.contains(Token.LABEL.toString()))
                    {
                        label = line.substring(line.indexOf(Token.LABEL.toString())
                                + Token.LABEL.toString().length())
                                .replace("\"", "")
                                .trim();
                        continue;
                    }
                    if(line.contains(Token.COMMENT.toString()))
                    {
                        comment = line.substring(line.indexOf(Token.COMMENT.toString())
                                + Token.COMMENT.toString().length())
                                .replace("\"", "")
                                .trim();
                        continue;
                    }
                    if(line.contains(Token.COMMAND.toString()))
                    {
                        command = line.substring(line.indexOf(Token.COMMAND.toString()) +
                                                 Token.COMMAND.toString().length());
                        continue;
                    }
                    if(containsToken(line))
                    {
                        break;
                    }
                }
                if(command != null){
                    //System.out.println(programa + "|" + command + "|" + label + "|" + comment + "|");
                    label = label != null ? label : command;
                    comandos.append(toUnlFormat(programa, command, label, comment, "N"));
                    comandos.newLine();
                }
                continue;
            }
            
            if(line.endsWith(Token.MENU.toString())){
                menuLabel = "";
                if(lines.hasNext()){
                    line = lines.next();
                    if(line.contains(Token.MENU_LABEL.toString()))
                    {
                        menuLabel = line.replace("\"", "").trim();
                    }
                    if(containsToken(line))
                    {
                        continue;
                    }
                    line = null;
                }
                continue;
            }
            
            if(line.endsWith(Token.OPTION.toString())){
                String command = null;
                String label = null;
                String help = "";
                while(lines.hasNext()){
                    line = lines.next();
                    if(line.contains(Token.OPTION_LABEL.toString()))
                    {
                        label = line.replace("\"", "").trim();
                        continue;
                    }
                    if(line.contains(Token.HELP.toString()))
                    {
                        help = line.substring(line.indexOf(Token.HELP.toString())
                             + Token.HELP.toString().length())
                             .replace("\"", "")
                             .trim();
                        continue;
                    }
                    if(line.contains(Token.COMMAND.toString()))
                    {
                        command = line.substring(line.indexOf(Token.COMMAND.toString()) +
                                                 Token.COMMAND.toString().length());
                        continue;
                    }
                    if(containsToken(line))
                    {
                        break;
                    }
                }
                if(command != null){
                    //System.out.println(programa + "|" + command + "|" + menuLabel + " -> " + label + "|" + help + "|");                    
                    label = label != null ? label : command;
                    comandos.append(toUnlFormat(programa, command, label, help, "N"));
                    comandos.newLine();
                }
                continue;
            }

            if(descripcion == null && line.endsWith(Token.INTERFACE.toString())){
                while(lines.hasNext()){
                    line = lines.next();
                    if(line.contains(Token.INTERFACE_LABEL.toString()))
                    {
                        descripcion = line.substring(line.indexOf(Token.INTERFACE_LABEL.toString())
                                    + Token.INTERFACE_LABEL.toString().length())
                                    .replace("\"", "")
                                    .trim();
                        continue;
                    }
                    if(containsToken(line))
                    {
                        break;
                    }
                }
                continue;
            }
            
            if(line.contains(Token.DESCRIPCION_PROGRAMA.toString())){
                var descripcionTemp = line.substring(line.indexOf(Token.DESCRIPCION_PROGRAMA.toString()) + 
                                                     Token.DESCRIPCION_PROGRAMA.toString().length(),
                                                     line.indexOf("}")).trim();
                descripcion = descripcionTemp.length() > 0 ? descripcionTemp : descripcion;
                line = null;
                continue;
            }
            
            line = null;                        
        }
        descripcion = descripcion != null ? descripcion : "";
        modulos.append(toUnlFormat(programa, descripcion));
        modulos.newLine();
        
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        var modulos = new File("C:\\tmp\\modulos.unl");
        var comandos = new File("C:\\tmp\\comandos.unl");
        BufferedWriter modulosBufferedWriter = null;
        BufferedWriter comandosBufferedWriter = null;
        try {
            modulosBufferedWriter = new BufferedWriter(new FileWriter(modulos));
            comandosBufferedWriter = new BufferedWriter(new FileWriter(comandos));
            var files = listFiles("C:\\Cosmos4.8\\proyectos\\General6.0.4_Oft");        
            for (File file : files) {
                var programa = file.getName().toUpperCase();
                programa = programa.substring(0, programa.length() - 4);
                java.util.stream.Stream<String> lines = getFileLines(file);
                var iterator = lines.iterator();
                processLines(modulosBufferedWriter, comandosBufferedWriter, programa, iterator);
            }
            modulosBufferedWriter.close();
            comandosBufferedWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Smd.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }
                
    }
    
}
