package PBREngine.util;

public class Time {
    public static double timeStarted = System.nanoTime();

    public static double getTime(){
        return ((System.nanoTime()-timeStarted)* 1E-9);
    }
}


//private float[] vertexArray = {
//        //vertices              //Colors
//
//        //t1
//
//        0.5f, 0.5f, 0.5f,r, g, b, a, //Front Top Right
//        -0.5f, 0.5f, 0.5f, r, g, b, a, //Front Top Left
//        0.5f, -0.5f, 0.5f, r, g, b, a, //Front  Bottom Right
//
//        //t2
//        0.5f, -0.5f, 0.5f, r, g, b, a, //Front  Bottom Right
//        -0.5f, 0.5f, 0.5f, r, g, b, a, //Front  Top Left
//        -0.5f, -0.5f, 0.5f, r, g, b, a, //Front Bottom Left
//
//        //t3
//        0.5f, -0.5f, -0.5f,r, g, b, a, //Back Bottom Right
//        -0.5f, 0.5f, -0.5f, r, g, b, a, //Back Top Left
//        0.5f, 0.5f, -0.5f,r, g, b, a, //Back Top Right
//
//        //t4
//        -0.5f, -0.5f, -0.5f, r, g, b, a, //Back Bottom Left
//        -0.5f, 0.5f, -0.5f, r, g, b, a, //Back Top Left
//        0.5f, -0.5f, -0.5f, r, g, b, a,//Back Bottom Right
//
//
//        //t5
//        -0.5f, -0.5f, -0.5f, r, g, b, a, //Back Bottom Left
//        0.5f, -0.5f, -0.5f, r, g, b, a, //Back Bottom Right
//        -0.5f, -0.5f, 0.5f, r, g, b, a, //Front Bottom Left
//
//        //t6
//        0.5f, -0.5f, 0.5f, r, g, b, a, //Front  Bottom Right
//        -0.5f, -0.5f, 0.5f, r, g, b, a, //Front Bottom Left
//        0.5f, -0.5f, -0.5f, r, g, b, a, //Back Bottom Right
//
//        //t7
//        -0.5f, 0.5f, 0.5f, r, g, b, a, //Front Bottom Left
//        0.5f, 0.5f, -0.5f, r, g, b, a, //Back Bottom Right
//        -0.5f, 0.5f, -0.5f, r, g, b, a, //Back Bottom Left
//
//        //t8
//        0.5f, 0.5f, -0.5f, r, g, b, a, //Back Bottom Right
//        -0.5f, 0.5f, 0.5f,r, g, b, a, //Front Bottom Left
//        0.5f, 0.5f, 0.5f, r, g, b, a, //Front  Bottom Right
//
//        //t9
//        -0.5f, -0.5f, -0.5f,r, g, b, a, //Back Bottom Left
//        -0.5f, -0.5f, 0.5f, r, g, b, a, //Front Bottom Left
//        -0.5f, 0.5f, 0.5f, r, g, b, a, //Front  Top Left
//
//        //t10
//        -0.5f, 0.5f, 0.5f,r, g, b, a, //Front  Top Left
//        -0.5f, 0.5f, -0.5f, r, g, b, a, //Back Top Left
//        -0.5f, -0.5f, -0.5f, r, g, b, a, //Back Bottom Left
//
//        //t11
//        0.5f, 0.5f, 0.5f, r, g, b, a, //Front  Top Right
//        0.5f, -0.5f, 0.5f, r, g, b, a, //Front Bottom Right
//        0.5f, -0.5f, -0.5f, r, g, b, a, //Back Bottom Right
//
//        //t12
//        0.5f, -0.5f, -0.5f, r, g, b, a,  //Back Bottom Right
//        0.5f, 0.5f, -0.5f, r, g, b, a, //Back Top Right
//        0.5f, 0.5f, 0.5f, r, g, b, a //Front  Top Right
//};