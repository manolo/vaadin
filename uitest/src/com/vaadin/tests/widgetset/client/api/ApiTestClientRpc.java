package com.vaadin.tests.widgetset.client.api;

import com.vaadin.shared.communication.ClientRpc;

public interface ApiTestClientRpc extends ClientRpc {
    public void run(String testName);
}