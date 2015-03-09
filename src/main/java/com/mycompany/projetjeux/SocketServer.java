/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.projetjeux;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author diop
 */
@ServerEndpoint("/serversocket")
public class SocketServer {

    static Set<Session> jeuxsession = Collections.synchronizedSet(new HashSet<Session>());
   @OnOpen
    public void handleOpen(Session usersession) {
        jeuxsession.add(usersession);

    }

    @OnMessage
    public void handleMessage(String inmessage, Session joueurssession) throws IOException {

        String username = (String) joueurssession.getUserProperties().get("username");
        if (username == null) {
            joueurssession.getUserProperties().put("username", inmessage);

            joueurssession.getBasicRemote().sendText(jsonBuilderData("System", "vous etes connectez avec" + inmessage));

        } else {
            Iterator<Session> iter = jeuxsession.iterator();
            while (iter.hasNext()) {
                iter.next().getBasicRemote().sendText(jsonBuilderData(username, inmessage));
            }
        }

    }

    @OnClose
    public void handleClose(Session usersession) {
        jeuxsession.remove(usersession);

    }
    private String jsonBuilderData(String username, String message) {

        JsonObject jsonObj = Json.createObjectBuilder().add("message", username + ":" + message).build();
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.write(jsonObj);
        }
        return stringWriter.toString();
    }

   

}
