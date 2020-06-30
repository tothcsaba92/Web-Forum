package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.constans.Status;
import edu.progmatic.messenger.model.Topic;
import edu.progmatic.messenger.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
public class MessageController implements WebMvcConfigurer {

    Logger logger = LoggerFactory.getLogger(MessageController.class);


    MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String limitedMessages(SecurityContextHolderAwareRequestWrapper request,
                                  @RequestParam(value = "limit", required = false, defaultValue = Integer.MAX_VALUE + "") int limit,
                                  @RequestParam(value = "orderby", required = false, defaultValue = "sender") String order,
                                  @RequestParam(value = "direction", required = false, defaultValue = "asc") String dir,
                                  //@RequestParam(value = "status",required = false,defaultValue = "false")boolean status,
                                  Model model) {
        boolean isAdmin = request.isUserInRole("ROLE_ADMIN");
        List<Message> messages;
        if (isAdmin) {
            messages = messageService.showMessages(order, limit, dir);
        } else {
            messages = messageService.showNonDeletedMessages(order, limit, dir);
        }
        model.addAttribute("messages", messages);


        return "messages";
    }

    @RequestMapping(value = "/messages/{messageId}", method = RequestMethod.GET)
    public String showSelectedMessageId(
            @PathVariable("messageId") int msgId, Model model) {
        Message message = messageService.showSelectedMessageById(msgId);
        if (message != null) {
            model.addAttribute("message", message);
        } else {
            Message nonExistMessage = new Message(null, null);
            nonExistMessage.setDateTime(null);
            nonExistMessage.setId(0);
            model.addAttribute("message", nonExistMessage);
        }
        return "single_message";
    }


    @GetMapping(value = "/new_message")
    public String showNewMessage(Model model) {
        Message msg = new Message(null, null);
        Topic topic = new Topic();
        msg.setTopic(topic);
        model.addAttribute("newMessage", msg);
        model.addAttribute("topic", topic);
        model.addAttribute("topicList", messageService.findAllTopic());
        return "new_message";
    }

    @RequestMapping(value = "/new_message", method = RequestMethod.POST)
    public String createNewMessage(@ModelAttribute(value = "newMessage") @Valid Message newMessage,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/new_message";
        }

        messageService.createNewMessage(newMessage);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/new_topic", method = RequestMethod.POST)
    public String createNewTopic(@ModelAttribute(value = "topic") Topic topic) {
        messageService.createNewTopic(topic.getName());
        return "redirect:/messages";
    }
}
