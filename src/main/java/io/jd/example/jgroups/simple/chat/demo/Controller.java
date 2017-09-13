package io.jd.example.jgroups.simple.chat.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@Slf4j
@RestController
@AllArgsConstructor
public class Controller {

    private SimpleChat simpleChat;

    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public String create(@RequestBody String input) {
        int retVal = simpleChat.send(input);

        return retVal == 0 ? "message is sent out!" : "failed to send message!";
    }
}
