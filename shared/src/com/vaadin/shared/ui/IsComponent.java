/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared.ui;

import com.vaadin.shared.ui.Sizeable.Unit;

// TODO(manolo): make these available in client and implement methods.
//import com.vaadin.event.ShortcutListener;
//import com.vaadin.ui.HasComponents;

/**
 * 
 * @author Vaadin Ltd
 */
public interface IsComponent {

    // TODO(manolo): implement these
    // void addShortcutListener(ShortcutListener shortcut);
    // void removeShortcutListener(ShortcutListener shortcut);
    // HasComponents getParent();
    // void setParent(HasComponents parent);

    /*
     * Gets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    String getStyleName();

    /*
     * Sets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    void setStyleName(String style);

    void setPrimaryStyleName(String style);

    String getPrimaryStyleName();

    void addStyleName(String style);

    void removeStyleName(String style);

    /*
     * Get's the component's caption. Don't add a JavaDoc comment here, we use
     * the default documentation from implemented interface.
     */
    String getCaption();

    /**
     * Sets the component's caption <code>String</code>. Caption is the visible
     * name of the component. This method will trigger a
     * {@link RepaintRequestEvent}.
     * 
     * @param caption
     *            the new caption <code>String</code> for the component.
     */
    void setCaption(String caption);

    /*
     * Gets the component's icon resource. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    // Resource getIcon();

    /**
     * Sets the component's icon. This method will trigger a
     * {@link RepaintRequestEvent}.
     * 
     * @param icon
     *            the icon to be shown with the component's caption.
     */
    // void setIcon(Resource icon);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#isEnabled()
     */
    boolean isEnabled();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#setEnabled(boolean)
     */
    void setEnabled(boolean enabled);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#isVisible()
     */
    boolean isVisible();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#setVisible(boolean)
     */
    void setVisible(boolean visible);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#getDescription()
     */
    String getDescription();

    /**
     * Sets the component's description. See {@link #getDescription()} for more
     * information on what the description is. This method will trigger a
     * {@link RepaintRequestEvent}.
     * 
     * The description is displayed as HTML in tooltips or directly in certain
     * components so care should be taken to avoid creating the possibility for
     * HTML injection and possibly XSS vulnerabilities.
     * 
     * @param description
     *            the new description string for the component.
     */
    void setDescription(String description);

    /**
     * Gets the error message for this component.
     * 
     * @return ErrorMessage containing the description of the error state of the
     *         component or null, if the component contains no errors. Extending
     *         classes should override this method if they support other error
     *         message types such as validation errors or buffering errors. The
     *         returned error message contains information about all the errors.
     */
    ErrorMessage getErrorMessage();

    /**
     * Gets the component's error message.
     * 
     * @link Terminal.ErrorMessage#ErrorMessage(String, int)
     * 
     * @return the component's error message.
     */
    ErrorMessage getComponentError();

    /**
     * Sets the component's error message. The message may contain certain XML
     * tags, for more information see
     * 
     * @link Component.ErrorMessage#ErrorMessage(String, int)
     * 
     * @param componentError
     *            the new <code>ErrorMessage</code> of the component.
     */
    void setComponentError(ErrorMessage componentError);

    /*
     * Tests if the component is in read-only mode. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    boolean isReadOnly();

    /*
     * Sets the component's read-only mode. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    void setReadOnly(boolean readOnly);

    /*
     * Notify the component that it's attached to a window. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    void attach();

    /*
     * Detach the component from application. Don't add a JavaDoc comment here,
     * we use the default documentation from implemented interface.
     */
    void detach();

    /*
     * Registers a new listener to listen events generated by this component.
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    void addListener(IsListener listener);

    /*
     * Removes a previously registered listener from this component. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    void removeListener(IsListener listener);

    /**
     * Sets the data object, that can be used for any application specific data.
     * The component does not use or modify this data.
     * 
     * @param data
     *            the Application specific data.
     * @since 3.1
     */
    void setData(Object data);

    /**
     * Gets the application specific data. See {@link #setData(Object)}.
     * 
     * @return the Application specific data set with setData function.
     * @since 3.1
     */
    Object getData();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.Sizeable#getHeight()
     */
    float getHeight();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#getHeightUnits()
     */
    Unit getHeightUnits();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#getWidth()
     */
    float getWidth();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#getWidthUnits()
     */
    Unit getWidthUnits();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setHeight(float, Unit)
     */
    void setHeight(float height, Unit unit);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setSizeFull()
     */
    void setSizeFull();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setSizeUndefined()
     */
    void setSizeUndefined();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setWidth(float, Unit)
     */
    void setWidth(float width, Unit unit);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setWidth(java.lang.String)
     */
    void setWidth(String width);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setHeight(java.lang.String)
     */
    void setHeight(String height);

    public interface IsFocusable extends IsComponent {
        public void focus();

        public int getTabIndex();

        public void setTabIndex(int tabIndex);
    }

    public interface IsEvent {
        /**
         * Gets the component where the event occurred.
         */
        public IsComponent getComponent();
    }

    public interface IsErrorEvent extends IsEvent {
        public ErrorMessage getErrorMessage();
    }

    public interface IsListener {
        public void componentEvent(IsEvent event);
    }

}