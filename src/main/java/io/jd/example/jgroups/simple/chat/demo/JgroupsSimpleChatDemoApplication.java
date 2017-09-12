package io.jd.example.jgroups.simple.chat.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JgroupsSimpleChatDemoApplication {

	public static void main(String[] args) {
		try {
			new SimpleChat().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		SpringApplication.run(JgroupsSimpleChatDemoApplication.class, args);
	}
}
