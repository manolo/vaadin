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

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.event.ActionManager;
import com.vaadin.event.ConnectorActionManager;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.ComponentSizeValidator;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.IsComponent;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.util.ReflectTools;

/**
 * An abstract class that defines default implementation for the
 * {@link Component} interface. Basic UI components that are not derived from an
 * external component can inherit this class to easily qualify as Vaadin
 * components. Most components in Vaadin do just that.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class AbstractComponent extends AbstractClientConnector
        implements Component, IsComponent {

    /* Private members */

    /**
     * Application specific data object. The component does not use or modify
     * this.
     */
    private Object applicationData;

    /**
     * The internal error message of the component.
     */
    private ErrorMessage componentError = null;

    /**
     * Locale of this component.
     */
    private Locale locale;

    /**
     * The component should receive focus (if {@link Focusable}) when attached.
     */
    private boolean delayedFocus;

    /* Sizeable fields */

    private float width = SIZE_UNDEFINED;
    private float height = SIZE_UNDEFINED;
    private Unit widthUnit = Unit.PIXELS;
    private Unit heightUnit = Unit.PIXELS;
    private static final Pattern sizePattern = Pattern
            .compile("^(-?\\d+(\\.\\d+)?)(%|px|em|rem|ex|in|cm|mm|pt|pc)?$");

    /**
     * Keeps track of the Actions added to this component; the actual
     * handling/notifying is delegated, usually to the containing window.
     */
    private ConnectorActionManager actionManager;

    private boolean visible = true;

    private HasComponents parent;

    private Boolean explicitImmediateValue;

    /* Constructor */

    /**
     * Constructs a new Component.
     */
    public AbstractComponent() {
        // ComponentSizeValidator.setCreationLocation(this);
    }

    /* Get/Set component properties */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#setId(java.lang.String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        getState().id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#getId()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getId()
     */
    @Override
    public String getId() {
        return getState(false).id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setDebugId(java.lang.String)
     */
    @Deprecated
    public void setDebugId(String id) {
        setId(id);
    }

    /**
     * @deprecated As of 7.0. Use {@link #getId()}
     */
    @Deprecated
    public String getDebugId() {
        return getId();
    }

    /*
     * Gets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getStyleName()
     */
    @Override
    public String getStyleName() {
        String s = "";
        if (ComponentStateUtil.hasStyles(getState())) {
            for (final Iterator<String> it = getState().styles.iterator(); it
                    .hasNext();) {
                s += it.next();
                if (it.hasNext()) {
                    s += " ";
                }
            }
        }
        return s;
    }

    /*
     * Sets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setStyleName(java.lang.String)
     */
    @Override
    public void setStyleName(String style) {
        if (style == null || "".equals(style)) {
            getState().styles = null;
            return;
        }
        if (getState().styles == null) {
            getState().styles = new ArrayList<String>();
        }
        List<String> styles = getState().styles;
        styles.clear();
        StringTokenizer tokenizer = new StringTokenizer(style, " ");
        while (tokenizer.hasMoreTokens()) {
            styles.add(tokenizer.nextToken());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setPrimaryStyleName(java.lang.String)
     */
    @Override
    public void setPrimaryStyleName(String style) {
        getState().primaryStyleName = style;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getPrimaryStyleName()
     */
    @Override
    public String getPrimaryStyleName() {
        return getState().primaryStyleName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#addStyleName(java.lang.String)
     */
    @Override
    public void addStyleName(String style) {
        if (style == null || "".equals(style)) {
            return;
        }
        if (style.contains(" ")) {
            // Split space separated style names and add them one by one.
            StringTokenizer tokenizer = new StringTokenizer(style, " ");
            while (tokenizer.hasMoreTokens()) {
                addStyleName(tokenizer.nextToken());
            }
            return;
        }

        if (getState().styles == null) {
            getState().styles = new ArrayList<String>();
        }
        List<String> styles = getState().styles;
        if (!styles.contains(style)) {
            styles.add(style);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#removeStyleName(java.lang.String)
     */
    @Override
    public void removeStyleName(String style) {
        if (ComponentStateUtil.hasStyles(getState())) {
            StringTokenizer tokenizer = new StringTokenizer(style, " ");
            while (tokenizer.hasMoreTokens()) {
                getState().styles.remove(tokenizer.nextToken());
            }
        }
    }

    /*
     * Get's the component's caption. Don't add a JavaDoc comment here, we use
     * the default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getCaption()
     */
    @Override
    public String getCaption() {
        return getState(false).caption;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setCaption(java.lang.String)
     */
    @Override
    public void setCaption(String caption) {
        getState().caption = caption;
    }

    /*
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getLocale()
     */
    @Override
    public Locale getLocale() {
        if (locale != null) {
            return locale;
        }
        HasComponents parent = getParent();
        if (parent != null) {
            return parent.getLocale();
        }
        final VaadinSession session = getSession();
        if (session != null) {
            return session.getLocale();
        }
        return null;
    }

    /**
     * Sets the locale of this component.
     * 
     * <pre>
     * // Component for which the locale is meaningful
     * InlineDateField date = new InlineDateField(&quot;Datum&quot;);
     * 
     * // German language specified with ISO 639-1 language
     * // code and ISO 3166-1 alpha-2 country code.
     * date.setLocale(new Locale(&quot;de&quot;, &quot;DE&quot;));
     * 
     * date.setResolution(DateField.RESOLUTION_DAY);
     * layout.addComponent(date);
     * </pre>
     * 
     * 
     * @param locale
     *            the locale to become this component's locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;

        if (locale != null && isAttached()) {
            getUI().getLocaleService().addLocale(locale);
        }

        markAsDirty();
    }

    /*
     * Gets the component's icon resource. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getIcon()
     */
    @Override
    public Resource getIcon() {
        return getResource(ComponentConstants.ICON_RESOURCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setIcon(com.vaadin.server.Resource)
     */
    @Override
    @Deprecated
    public void setIcon(Resource icon) {
        setResource(ComponentConstants.ICON_RESOURCE, icon);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#isEnabled()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return getState(false).enabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#setEnabled(boolean)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        getState().enabled = enabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Connector#isConnectorEnabled()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#isConnectorEnabled()
     */
    @Override
    public boolean isConnectorEnabled() {
        if (!isVisible()) {
            return false;
        } else if (!isEnabled()) {
            return false;
        } else if (!super.isConnectorEnabled()) {
            return false;
        } else if ((getParent() instanceof SelectiveRenderer)
                && !((SelectiveRenderer) getParent()).isRendered(this)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isImmediate() {
        if (explicitImmediateValue != null) {
            return explicitImmediateValue;
        } else if (hasListeners(ValueChangeEvent.class)) {
            /*
             * Automatic immediate for fields that developers are interested
             * about.
             */
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the component's immediate mode to the specified status.
     * 
     * @param immediate
     *            the boolean value specifying if the component should be in the
     *            immediate mode after the call.
     */
    public void setImmediate(boolean immediate) {
        explicitImmediateValue = immediate;
        getState().immediate = immediate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#isVisible()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#isVisible()
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#setVisible(boolean)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        if (isVisible() == visible) {
            return;
        }

        this.visible = visible;
        if (visible) {
            /*
             * If the visibility state is toggled from invisible to visible it
             * affects all children (the whole hierarchy) in addition to this
             * component.
             */
            markAsDirtyRecursive();
        }
        if (getParent() != null) {
            // Must always repaint the parent (at least the hierarchy) when
            // visibility of a child component changes.
            getParent().markAsDirty();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#getDescription()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getDescription()
     */
    @Override
    public String getDescription() {
        return getState(false).description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
        getState().description = description;
    }

    /*
     * Gets the component's parent component. Don't add a JavaDoc comment here,
     * we use the default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getParent()
     */
    @Override
    public HasComponents getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setParent(com.vaadin.ui.HasComponents)
     */
    @Override
    public void setParent(HasComponents parent) {
        // If the parent is not changed, don't do anything
        if (parent == this.parent) {
            return;
        }

        if (parent != null && this.parent != null) {
            throw new IllegalStateException(getClass().getName()
                    + " already has a parent.");
        }

        // Send a detach event if the component is currently attached
        if (isAttached()) {
            detach();
        }

        // Connect to new parent
        this.parent = parent;

        // Send attach event if the component is now attached
        if (isAttached()) {
            attach();
        }
    }

    /**
     * Returns the closest ancestor with the given type.
     * <p>
     * To find the Window that contains the component, use {@code Window w =
     * getParent(Window.class);}
     * </p>
     * 
     * @param <T>
     *            The type of the ancestor
     * @param parentType
     *            The ancestor class we are looking for
     * @return The first ancestor that can be assigned to the given class. Null
     *         if no ancestor with the correct type could be found.
     */
    public <T extends HasComponents> T findAncestor(Class<T> parentType) {
        HasComponents p = getParent();
        while (p != null) {
            if (parentType.isAssignableFrom(p.getClass())) {
                return parentType.cast(p);
            }
            p = p.getParent();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getErrorMessage()
     */
    @Override
    public ErrorMessage getErrorMessage() {
        return componentError;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getComponentError()
     */
    @Override
    public ErrorMessage getComponentError() {
        return componentError;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.IsComponent#setComponentError(com.vaadin.server.ErrorMessage
     * )
     */
    @Override
    @Deprecated
    public void setComponentError(ErrorMessage componentError) {
        this.componentError = componentError;
        fireComponentErrorEvent();
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.shared.ui.IsComponent#setComponentError(com.vaadin.shared.
     * ui.ErrorMessage)
     */
    @Override
    public void setComponentError(
            com.vaadin.shared.ui.ErrorMessage componentError) {
        setComponentError((com.vaadin.server.ErrorMessage) componentError);
    }

    /*
     * Tests if the component is in read-only mode. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return getState(false).readOnly;
    }

    /*
     * Sets the component's read-only mode. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        getState().readOnly = readOnly;
    }

    /*
     * Notify the component that it's attached to a window. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#attach()
     */
    @Override
    public void attach() {
        super.attach();
        if (delayedFocus) {
            focus();
        }
        setActionManagerViewer();
        if (locale != null) {
            getUI().getLocaleService().addLocale(locale);
        }

    }

    /*
     * Detach the component from application. Don't add a JavaDoc comment here,
     * we use the default documentation from implemented interface.
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#detach()
     */
    @Override
    public void detach() {
        super.detach();
        if (actionManager != null) {
            // Remove any existing viewer. UI cast is just to make the
            // compiler happy
            actionManager.setViewer((UI) null);
        }
    }

    /**
     * Sets the focus for this component if the component is {@link Focusable}.
     */
    protected void focus() {
        if (this instanceof Focusable) {
            final VaadinSession session = getSession();
            if (session != null) {
                getUI().setFocusedComponent((Focusable) this);
                delayedFocus = false;
            } else {
                delayedFocus = true;
            }
        }
    }

    /**
     * Build CSS compatible string representation of height.
     * 
     * @return CSS height
     */
    private String getCSSHeight() {
        return getHeight() + getHeightUnits().getSymbol();
    }

    /**
     * Build CSS compatible string representation of width.
     * 
     * @return CSS width
     */
    private String getCSSWidth() {
        return getWidth() + getWidthUnits().getSymbol();
    }

    /**
     * Returns the shared state bean with information to be sent from the server
     * to the client.
     * 
     * Subclasses should override this method and set any relevant fields of the
     * state returned by super.getState().
     * 
     * @since 7.0
     * 
     * @return updated component shared state
     */
    @Override
    protected AbstractComponentState getState() {
        return (AbstractComponentState) super.getState();
    }

    @Override
    protected AbstractComponentState getState(boolean markAsDirty) {
        return (AbstractComponentState) super.getState(markAsDirty);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        // TODO This logic should be on the client side and the state should
        // simply be a data object with "width" and "height".
        if (getHeight() >= 0
                && (getHeightUnits() != Unit.PERCENTAGE || ComponentSizeValidator
                        .parentCanDefineHeight(this))) {
            getState().height = "" + getCSSHeight();
        } else {
            getState().height = "";
        }

        if (getWidth() >= 0
                && (getWidthUnits() != Unit.PERCENTAGE || ComponentSizeValidator
                        .parentCanDefineWidth(this))) {
            getState().width = "" + getCSSWidth();
        } else {
            getState().width = "";
        }

        ErrorMessage error = getErrorMessage();
        if (null != error) {
            getState().errorMessage = error.getFormattedHtmlMessage();
        } else {
            getState().errorMessage = null;
        }

        getState().immediate = isImmediate();
    }

    /* General event framework */

    private static final Method COMPONENT_EVENT_METHOD = ReflectTools
            .findMethod(Component.Listener.class, "componentEvent",
                    Component.Event.class);

    /* Component event framework */

    /*
     * Registers a new listener to listen events generated by this component.
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public void addListener(Component.Listener listener) {
        addListener(Component.Event.class, listener, COMPONENT_EVENT_METHOD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.IsComponent#addListener(com.vaadin.ui.Component.Listener)
     */
    @Override
    public void addListener(IsListener listener) {
        addListener(Component.Event.class, listener, COMPONENT_EVENT_METHOD);
    }

    /*
     * Removes a previously registered listener from this component. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void removeListener(Component.Listener listener) {
        removeListener(Component.Event.class, listener, COMPONENT_EVENT_METHOD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.shared.ui.IsComponent#removeListener(com.vaadin.shared.ui.
     * IsComponent.IsListener)
     */
    @Override
    public void removeListener(IsListener listener) {
        removeListener(Component.Event.class, listener, COMPONENT_EVENT_METHOD);
    }

    /**
     * Emits the component event. It is transmitted to all registered listeners
     * interested in such events.
     */
    protected void fireComponentEvent() {
        fireEvent(new Component.Event(this));
    }

    /**
     * Emits the component error event. It is transmitted to all registered
     * listeners interested in such events.
     */
    protected void fireComponentErrorEvent() {
        fireEvent(new Component.ErrorEvent(getComponentError(), this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setData(java.lang.Object)
     */
    @Override
    public void setData(Object data) {
        applicationData = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getData()
     */
    @Override
    public Object getData() {
        return applicationData;
    }

    /* Sizeable and other size related methods */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.Sizeable#getHeight()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getHeight()
     */
    @Override
    public float getHeight() {
        return height;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#getHeightUnits()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getHeightUnits()
     */
    @Override
    public Unit getHeightUnits() {
        return heightUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#getWidth()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getWidth()
     */
    @Override
    public float getWidth() {
        return width;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#getWidthUnits()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#getWidthUnits()
     */
    @Override
    public Unit getWidthUnits() {
        return widthUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setHeight(float, Unit)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setHeight(float,
     * com.vaadin.server.Sizeable.Unit)
     */
    @Override
    public void setHeight(float height, Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit can not be null");
        }
        this.height = height;
        heightUnit = unit;
        markAsDirty();
        // ComponentSizeValidator.setHeightLocation(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setSizeFull()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setSizeFull()
     */
    @Override
    public void setSizeFull() {
        setWidth(100, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setSizeUndefined()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setSizeUndefined()
     */
    @Override
    public void setSizeUndefined() {
        setWidth(-1, Unit.PIXELS);
        setHeight(-1, Unit.PIXELS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setWidth(float, Unit)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setWidth(float,
     * com.vaadin.server.Sizeable.Unit)
     */
    @Override
    public void setWidth(float width, Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit can not be null");
        }
        this.width = width;
        widthUnit = unit;
        markAsDirty();
        // ComponentSizeValidator.setWidthLocation(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setWidth(java.lang.String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setWidth(java.lang.String)
     */
    @Override
    public void setWidth(String width) {
        Size size = parseStringSize(width);
        if (size != null) {
            setWidth(size.getSize(), size.getUnit());
        } else {
            setWidth(-1, Unit.PIXELS);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Sizeable#setHeight(java.lang.String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IsComponent#setHeight(java.lang.String)
     */
    @Override
    public void setHeight(String height) {
        Size size = parseStringSize(height);
        if (size != null) {
            setHeight(size.getSize(), size.getUnit());
        } else {
            setHeight(-1, Unit.PIXELS);
        }
    }

    /*
     * Returns array with size in index 0 unit in index 1. Null or empty string
     * will produce {-1,Unit#PIXELS}
     */
    private static Size parseStringSize(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if ("".equals(s)) {
            return null;
        }
        float size = 0;
        Unit unit = null;
        Matcher matcher = sizePattern.matcher(s);
        if (matcher.find()) {
            size = Float.parseFloat(matcher.group(1));
            if (size < 0) {
                size = -1;
                unit = Unit.PIXELS;
            } else {
                String symbol = matcher.group(3);
                unit = Unit.getUnitFromSymbol(symbol);
            }
        } else {
            throw new IllegalArgumentException("Invalid size argument: \"" + s
                    + "\" (should match " + sizePattern.pattern() + ")");
        }
        return new Size(size, unit);
    }

    private static class Size implements Serializable {
        float size;
        Unit unit;

        public Size(float size, Unit unit) {
            this.size = size;
            this.unit = unit;
        }

        public float getSize() {
            return size;
        }

        public Unit getUnit() {
            return unit;
        }
    }

    /*
     * Actions
     */

    /**
     * Gets the {@link ActionManager} used to manage the
     * {@link ShortcutListener}s added to this {@link Field}.
     * 
     * @return the ActionManager in use
     */
    protected ActionManager getActionManager() {
        if (actionManager == null) {
            actionManager = new ConnectorActionManager(this);
            setActionManagerViewer();
        }
        return actionManager;
    }

    /**
     * Set a viewer for the action manager to be the parent sub window (if the
     * component is in a window) or the UI (otherwise). This is still a
     * simplification of the real case as this should be handled by the parent
     * VOverlay (on the client side) if the component is inside an VOverlay
     * component.
     */
    private void setActionManagerViewer() {
        if (actionManager != null && getUI() != null) {
            // Attached and has action manager
            Window w = findAncestor(Window.class);
            if (w != null) {
                actionManager.setViewer(w);
            } else {
                actionManager.setViewer(getUI());
            }
        }

    }

    public void addShortcutListener(ShortcutListener shortcut) {
        getActionManager().addAction(shortcut);
    }

    public void removeShortcutListener(ShortcutListener shortcut) {
        if (actionManager != null) {
            actionManager.removeAction(shortcut);
        }
    }
}
