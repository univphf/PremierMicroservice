package com.ht.dev;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

//@SpringBootApplication
@Controller
@EnableAutoConfiguration
@SpringBootConfiguration
public class PremierMicroService {

    private RestTemplate template=new RestTemplate();

    //mapper dans le fichier application.properties
    @Value("${backend.backendServiceHost}")
    String backendServiceHost;
    @Value("${backend.backendServicePort}")
    int backendServicePort;

    @RequestMapping(value="/backend", method=RequestMethod.GET, produces="text/plain")
    @ResponseBody
    String CallBackend() {
        //construire et appeler le service Backend
        String backendServiceURL=String.format("http://%s:%d/backend", backendServiceHost, backendServicePort);

        BackEndDTO response=template.getForObject(backendServiceURL, BackEndDTO.class);

        //return backendServiceURL;
        return response.getReponse();
    }


    @RequestMapping("/hello")
    @ResponseBody
    String home() {
        return "Salut les licences 3 alt!";
    }

    @RequestMapping("/message")
    @ResponseBody
    String message() throws TimeoutException {
        String QUEUE_NAME = "HELLO";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String message = "Salut les licences 3 alt!";
        try
        {
          Connection connection = factory.newConnection();
          Channel channel = connection.createChannel();

          channel.queueDeclare(QUEUE_NAME, false, false, false, null);

          channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));

          channel.close();
        }
     catch (IOException ex) {
          return ex.getMessage();
      }
        return " [x] Envoyé '" + message + "'";
    }

    @RequestMapping(value="/message/{msg}", method=RequestMethod.GET)
    @ResponseBody
    String messageMsg(@PathVariable("msg") String msg) throws TimeoutException {
        String QUEUE_NAME = "HELLO";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try
        {
          Connection connection = factory.newConnection();
          Channel channel = connection.createChannel();

          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
          String message = "Salut les licences 3 alt!";
          channel.basicPublish("", QUEUE_NAME, null, msg.getBytes("UTF-8"));
          System.out.println();

          channel.close();
        }
     catch (IOException ex) {
          return ex.getMessage();
      }
        return " [x] Envoyé '" + msg + "'";
    }

	public static void main(String[] args) {
		SpringApplication.run(PremierMicroService.class, args);
	}
}