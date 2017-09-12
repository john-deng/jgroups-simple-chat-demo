package io.jd.example.jgroups.simple.chat.demo;

import lombok.extern.slf4j.Slf4j;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
public class SimpleChat extends ReceiverAdapter {

    JChannel channel;

    String username = System.getProperty("user.name", "n/a");


    public void start() throws Exception {

        channel = new JChannel(); // use the default config, udp.xml

        channel.setReceiver(this);
        channel.connect("ChatCluster");

        eventLoop();

        channel.close();


    }

    private void eventLoop() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            try {

                System.out.print("> ");
                System.out.flush();

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

        System.out.println("** view: " + newView);

    }


    public void receive(Message msg) {

        System.out.println(msg.getSrc() + ": " + msg.getObject());

    }
}
