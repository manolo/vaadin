package com.vaadin.tests.widgetset.client.api;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

public class ApiTestWidget extends Widget {

    public ApiTestWidget() {
        // Create some simple dom element
        setElement(Document.get().createElement("div"));
    }
}