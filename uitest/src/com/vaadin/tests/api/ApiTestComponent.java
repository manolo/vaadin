package com.vaadin.tests.api;

import com.vaadin.tests.widgetset.client.api.ApiTestServerRpc;
import com.vaadin.tests.widgetset.client.api.ApiTestState;

@SuppressWarnings("serial")
public class ApiTestComponent extends com.vaadin.ui.AbstractComponent {

    private ApiTestServerRpc rpc = new ApiTestServerRpc() {
        @Override
        public void echo(String s) {
        }
    };

    public ApiTestComponent() {
        registerRpc(rpc);
    }

    @Override
    public ApiTestState getState() {
        return (ApiTestState) super.getState();
    }
}
