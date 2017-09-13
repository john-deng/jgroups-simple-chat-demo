package io.jd.example.jgroups.simple.chat.demo;

import lombok.extern.slf4j.Slf4j;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.getProperty;
import static java.lang.System.out;

@Slf4j
@Component
public class SimpleChat extends ReceiverAdapter {

    private final List<String> state = new LinkedList<>();
    @Value("${app.jgroups.config:jgroups-config.xml}")
    private String jGroupsConfig;
    @Value("${app.jgroups.cluster:chat-cluster}")
    private String clusterName;
    private JChannel channel;
    private String username = getProperty("user.name", "n/a");

    @PostConstruct
    public void init()  {
        log.info("Start SimpleChat ...");

        try {
            channel = new JChannel(jGroupsConfig);
            channel.setReceiver(this);
            channel.connect(clusterName);
            channel.getState(null, 10000);
//            MBeanServer server = Util.getMBeanServer();
//            JmxConfigurator.registerChannel(channel, server, "control-channel", channel.getClusterName(), true);
        } catch (Exception ex) {
            log.error("registering the channel in JMX failed: {}", ex);
        }
    }

    public void close() {
        channel.close();
    }

    public int send(String message) {

        Message msg = new Message(null, message);
        try {
            log.info("send message: {}", message);
            channel.send(msg);
        } catch (Exception e) {
            log.error("Failed: {}", e);
            return -1;
        }
        return 0;
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

        log.info("** view: " + newView);

    }


    public void receive(Message msg) {

        String line = msg.getSrc() + ": " + msg.getObject();

        log.info("Received: " + line);

        synchronized (state) {

            state.add(line);

        }

    }

    public void getState(OutputStream output) throws Exception {

        synchronized (state) {

            Util.objectToStream(state, new DataOutputStream(output));

        }

    }

    public void setState(InputStream input) throws Exception {

        List<String> list;

        list = Util.objectFromStream(new DataInputStream(input));

        synchronized (state) {

            state.clear();

            state.addAll(list);

        }

        log.info(list.size() + " messages in chat history):");

        for (String str : list) {

            log.info(str);

        }

    }
}
