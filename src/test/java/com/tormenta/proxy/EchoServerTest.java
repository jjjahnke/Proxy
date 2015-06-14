package com.tormenta.proxy;

import junit.framework.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoServerTest {
    @Test
    public void testMain_noPort() throws Exception {
        String[] no_port = {};

        try {
            EchoServer.main(no_port);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void testMain_noNumPort() throws Exception {
        String[] no_port = {"bogus"};

        try {
            EchoServer.main(no_port);
            Assert.fail();
        } catch (NumberFormatException e) {
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testMain_goodPort() throws Exception {
        final String[] good_port = {"9877"};

        new Thread(new Runnable() {
            public void run() {
                try {
                    EchoServer.main(good_port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        execEcho(9877);
    }

    @Test
    public void testMain_tooManyPort() throws Exception {
        final String[] too_many_port = {"9878", "Some More"};

        new Thread(new Runnable() {
            public void run() {
                try {
                    EchoServer.main(too_many_port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        execEcho(9878);
    }


    @Test
    public void testStart() throws Exception {

        int port = 9876;
        final EchoServer es = new EchoServer(port);

        new Thread(new Runnable() {
            public void run() {
                try {
                    es.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        execEcho(port);

    }

    private void execEcho(int port) throws InterruptedException, IOException {
        Thread.sleep(4000);

        Socket socket = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String testString = "testStart Test";
        out.println(testString);
        Assert.assertEquals(testString, in.readLine());
    }
}