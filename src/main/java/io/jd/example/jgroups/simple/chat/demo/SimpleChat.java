package io.jd.example.jgroups.simple.chat.demo;

import lombok.extern.slf4j.Slf4j;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.*;

@Slf4j
public class SimpleChat extends ReceiverAdapter {

    private JChannel channel;

    private String username = getProperty("user.name", "n/a");

    private final List<String> state = new LinkedList<String>();


    public void start() throws Exception {

        channel=new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }

    private void eventLoop() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            try {

                out.print("> ");
                out.flush();

                String line = in.readLine().toLowerCase();

                if (line.startsWith("quit") || line.startsWith("exit"))
                    break;

                line = "[" + username + "] " + line;

                Message msg = new Message(null, line);

                channel.send(msg);

            } catch (Exception e) {
                log.error("Error: {}", e);
            }

        }

    }

    public void viewAccepted(View newView) {

        out.println("** view: " + newView);

    }


    public void receive(Message msg) {

        String line=msg.getSrc() + ": " + msg.getObject();

        out.println(line);

        synchronized(state) {

            state.add(line);

        }

    }

    public void getState(OutputStream output) throws Exception {

        synchronized(state) {

            Util.objectToStream(state, new DataOutputStream(output));

        }

    }

    public void setState(InputStream input) throws Exception {

        List<String> list;

        list= Util.objectFromStream(new DataInputStream(input));

        synchronized(state) {

            state.clear();

            state.addAll(list);

        }

        out.println(list.size() + " messages in chat history):");

        for(String str: list) {

            out.println(str);

        }

    }
}
