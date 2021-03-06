package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.services.MessageService;
import edu.progmatic.messenger.services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;
import java.util.List;

/**
 *This controller is make the API restful, to more separate the FE and BE, and instead of html view , it returns with
 * an abject, what is transformed to a JSON file.
 *
 * @author csaba
 */

@RestController
public class MessageRestController {
    MessageService messageService;
    TopicService topicService;

    @Autowired
    public MessageRestController(MessageService messageService, TopicService topicService) {
        this.messageService = messageService;
        this.topicService = topicService;
    }


    @RequestMapping(value = "/rest/messages", method = RequestMethod.GET)
    public List<Message> showMessages(
            @RequestParam(value = "limit", required = false, defaultValue = Integer.MAX_VALUE + "") long limit,
                                      @RequestParam(value = "orderby", required = false, defaultValue = "sender") String order,
                                      @RequestParam(value = "direction", required = false, defaultValue = "asc") String direction,
                                      @RequestParam(value = "topicId", required = false, defaultValue = "0") Long topicId,
                                      @RequestParam(value = "isDeleted", required = false, defaultValue = "") Boolean isDeleted,
                                      @RequestParam(value = "text", required = false) String text,
                                      @RequestParam(value = "sender", required = false) String sender,
                                      @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                      @RequestParam(value = "dateTo", required = false) String dateTo) {
        return messageService.showMessages(order, limit, direction, topicId, isDeleted, text,
                sender, true, dateFrom, dateTo);
    }

    @RequestMapping(value = "/rest/messages/{messageId}", method = RequestMethod.PUT)
    public Message setMessageForDeletion(@PathVariable("messageId") Long msgId) {
       messageService.setMessageForDeletion(msgId);
       return  messageService.showSelectedMessageById(msgId);
    }


    @GetMapping(value = "/rest/messages/{messageId}")
    public Message showASingleMessage(@PathVariable("messageId") Long msgId){
       return messageService.showSelectedMessageById(msgId);
    }

    @GetMapping(value = "/rest/csrf")
    public CsrfToken getCurrentCsrfToken(CsrfToken token) {
        return token;
    }
}
