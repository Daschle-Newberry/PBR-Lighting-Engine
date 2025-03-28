package OpenGL_Basic.util;

import OpenGL_Basic.engine.scene.elements.model.Model;

import java.io.InputStream;
import java.util.Scanner;

public class FileReader {
    public static String readLine(int line,String filePath){
        InputStream file = Model.class.getResourceAsStream(filePath);
        Scanner scan = new Scanner(file);

        int currentLine = 0;
        while(scan.hasNext()){
            String ln = scan.nextLine();
            if(currentLine == line) return ln;
            currentLine ++;
        }
        return null;
    }
}
