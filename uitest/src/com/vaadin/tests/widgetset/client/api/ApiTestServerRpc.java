package com.vaadin.tests.widgetset.client.api;

import com.vaadin.shared.communication.ServerRpc;

public interface ApiTestServerRpc extends ServerRpc {

    public void echo(String s);

}
