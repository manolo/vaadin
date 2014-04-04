package com.vaadin.tests.integration;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.api.ApiTestComponent;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Widgetset(TestingWidgetSet.NAME)
public class ApiTestUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        ApiTestComponent apiTest = new ApiTestComponent();
        setContent(apiTest);
    }
}
