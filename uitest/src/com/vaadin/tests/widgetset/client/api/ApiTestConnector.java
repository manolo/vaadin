package com.vaadin.tests.widgetset.client.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.api.ApiTestComponent;

@SuppressWarnings("serial")
@Connect(ApiTestComponent.class)
public class ApiTestConnector extends AbstractComponentConnector {

    ApiTestServerRpc rpc = RpcProxy.create(ApiTestServerRpc.class, this);

    public ApiTestConnector() {
        registerRpc(ApiTestClientRpc.class, new ApiTestClientRpc() {
            @Override
            public void run(String testName) {
            }
        });
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(ApiTestWidget.class);
    }

    @Override
    public ApiTestWidget getWidget() {
        return (ApiTestWidget) super.getWidget();
    }

    @Override
    public ApiTestState getState() {
        return (ApiTestState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
    }
}
