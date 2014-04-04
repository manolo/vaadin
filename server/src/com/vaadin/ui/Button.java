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

import org.json.JSONException;

import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.button.ButtonServerRpc;
import com.vaadin.shared.ui.button.ButtonState;
import com.vaadin.shared.ui.button.IsButton;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.util.ReflectTools;

/**
 * A generic button component.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Button extends AbstractComponent implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier, Focusable,
        Action.ShortcutNotifier, IsButton {

    private ButtonServerRpc rpc = new ButtonServerRpc() {

        @Override
        public void click(MouseEventDetails mouseEventDetails) {
            fireClick(mouseEventDetails);
        }

        @Override
        public void disableOnClick() throws RuntimeException {
            setEnabled(false);
            // Makes sure the enabled=false state is noticed at once - otherwise
            // a following setEnabled(true) call might have no effect. see
            // ticket #10030
            try {
                getUI().getConnectorTracker().getDiffState(Button.this)
                        .put("enabled", false);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };

    FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {

        @Override
        protected void fireEvent(com.vaadin.ui.Component.Event event) {
            Button.this.fireEvent(event);
        }

    };

    /**
     * Creates a new push button.
     */
    public Button() {
        registerRpc(rpc);
        registerRpc(focusBlurRpc);
    }

    /**
     * Creates a new push button with the given caption.
     * 
     * @param caption
     *            the Button caption.
     */
    public Button(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new push button with the given icon.
     * 
     * @param icon
     *            the icon
     */
    public Button(Resource icon) {
        this();
        setIcon(icon);
    }

    /**
     * Creates a new push button with the given caption and icon.
     * 
     * @param caption
     *            the caption
     * @param icon
     *            the icon
     */
    public Button(String caption, Resource icon) {
        this();
        setCaption(caption);
        setIcon(icon);
    }

    /**
     * Creates a new push button with a click listener.
     * 
     * @param caption
     *            the Button caption.
     * @param listener
     *            the Button click listener.
     */
    public Button(String caption, ClickListener listener) {
        this(caption);
        addListener(listener);
    }

    /**
     * Click event. This event is thrown, when the button is clicked.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public static class ClickEvent extends Component.Event {

        private final MouseEventDetails details;

        /**
         * New instance of text change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public ClickEvent(Component source) {
            super(source);
            details = null;
        }

        /**
         * Constructor with mouse details
         * 
         * @param source
         *            The source where the click took place
         * @param details
         *            Details about the mouse click
         */
        public ClickEvent(Component source, MouseEventDetails details) {
            super(source);
            this.details = details;
        }

        /**
         * Gets the Button where the event occurred.
         * 
         * @return the Source of the event.
         */
        public Button getButton() {
            return (Button) getSource();
        }

        /**
         * Returns the mouse position (x coordinate) when the click took place.
         * The position is relative to the browser client area.
         * 
         * @return The mouse cursor x position or -1 if unknown
         */
        public int getClientX() {
            if (null != details) {
                return details.getClientX();
            } else {
                return -1;
            }
        }

        /**
         * Returns the mouse position (y coordinate) when the click took place.
         * The position is relative to the browser client area.
         * 
         * @return The mouse cursor y position or -1 if unknown
         */
        public int getClientY() {
            if (null != details) {
                return details.getClientY();
            } else {
                return -1;
            }
        }

        /**
         * Returns the relative mouse position (x coordinate) when the click
         * took place. The position is relative to the clicked component.
         * 
         * @return The mouse cursor x position relative to the clicked layout
         *         component or -1 if no x coordinate available
         */
        public int getRelativeX() {
            if (null != details) {
                return details.getRelativeX();
            } else {
                return -1;
            }
        }

        /**
         * Returns the relative mouse position (y coordinate) when the click
         * took place. The position is relative to the clicked component.
         * 
         * @return The mouse cursor y position relative to the clicked layout
         *         component or -1 if no y coordinate available
         */
        public int getRelativeY() {
            if (null != details) {
                return details.getRelativeY();
            } else {
                return -1;
            }
        }

        /**
         * Checks if the Alt key was down when the mouse event took place.
         * 
         * @return true if Alt was down when the event occured, false otherwise
         *         or if unknown
         */
        public boolean isAltKey() {
            if (null != details) {
                return details.isAltKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Ctrl key was down when the mouse event took place.
         * 
         * @return true if Ctrl was pressed when the event occured, false
         *         otherwise or if unknown
         */
        public boolean isCtrlKey() {
            if (null != details) {
                return details.isCtrlKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Meta key was down when the mouse event took place.
         * 
         * @return true if Meta was pressed when the event occured, false
         *         otherwise or if unknown
         */
        public boolean isMetaKey() {
            if (null != details) {
                return details.isMetaKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Shift key was down when the mouse event took place.
         * 
         * @return true if Shift was pressed when the event occured, false
         *         otherwise or if unknown
         */
        public boolean isShiftKey() {
            if (null != details) {
                return details.isShiftKey();
            } else {
                return false;
            }
        }
    }

    /**
     * Interface for listening for a {@link ClickEvent} fired by a
     * {@link Component}.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public interface ClickListener extends Serializable {

        public static final Method BUTTON_CLICK_METHOD = ReflectTools
                .findMethod(ClickListener.class, "buttonClick",
                        ClickEvent.class);

        /**
         * Called when a {@link Button} has been clicked. A reference to the
         * button is given by {@link ClickEvent#getButton()}.
         * 
         * @param event
         *            An event containing information about the click.
         */
        public void buttonClick(ClickEvent event);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.IButton#addClickListener(com.vaadin.ui.Button.ClickListener
     * )
     */
    @Override
    public void addClickListener(ClickListener listener) {
        addListener(ClickEvent.class, listener,
                ClickListener.BUTTON_CLICK_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addClickListener(ClickListener)}
     **/
    @Deprecated
    public void addListener(ClickListener listener) {
        addClickListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.IButton#removeClickListener(com.vaadin.ui.Button.ClickListener
     * )
     */
    @Override
    public void removeClickListener(ClickListener listener) {
        removeListener(ClickEvent.class, listener,
                ClickListener.BUTTON_CLICK_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeClickListener(ClickListener)}
     **/
    @Deprecated
    public void removeListener(ClickListener listener) {
        removeClickListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#click()
     */
    @Override
    public void click() {
        if (isEnabled() && !isReadOnly()) {
            fireClick();
        }
    }

    /**
     * Fires a click event to all listeners without any event details.
     * 
     * In subclasses, override {@link #fireClick(MouseEventDetails)} instead of
     * this method.
     */
    protected void fireClick() {
        fireEvent(new Button.ClickEvent(this));
    }

    /**
     * Fires a click event to all listeners.
     * 
     * @param details
     *            MouseEventDetails from which keyboard modifiers and other
     *            information about the mouse click can be obtained. If the
     *            button was clicked by a keyboard event, some of the fields may
     *            be empty/undefined.
     */
    protected void fireClick(MouseEventDetails details) {
        fireEvent(new Button.ClickEvent(this, details));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#addBlurListener(com.vaadin.event.FieldEvents.
     * BlurListener)
     */
    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by {@link #addBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void addListener(BlurListener listener) {
        addBlurListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.IButton#removeBlurListener(com.vaadin.event.FieldEvents
     * .BlurListener)
     */
    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(BlurListener listener) {
        removeBlurListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#addFocusListener(com.vaadin.event.FieldEvents.
     * FocusListener)
     */
    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void addListener(FocusListener listener) {
        addFocusListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.IButton#removeFocusListener(com.vaadin.event.FieldEvents
     * .FocusListener)
     */
    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(FocusListener listener) {
        removeFocusListener(listener);
    }

    /*
     * Actions
     */

    protected ClickShortcut clickShortcut;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#setClickShortcut(int, int)
     */
    @Override
    public void setClickShortcut(int keyCode, int... modifiers) {
        if (clickShortcut != null) {
            removeShortcutListener(clickShortcut);
        }
        clickShortcut = new ClickShortcut(this, keyCode, modifiers);
        addShortcutListener(clickShortcut);
        getState().clickShortcutKeyCode = clickShortcut.getKeyCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#removeClickShortcut()
     */
    @Override
    public void removeClickShortcut() {
        if (clickShortcut != null) {
            removeShortcutListener(clickShortcut);
            clickShortcut = null;
            getState().clickShortcutKeyCode = 0;
        }
    }

    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that invokes a click on the given button.
     * 
     */
    public static class ClickShortcut extends ShortcutListener {
        protected Button button;

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * shorthand notation defined in {@link ShortcutAction}.
         * 
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param shorthandCaption
         *            the caption with shortcut keycode and modifiers indicated
         */
        public ClickShortcut(Button button, String shorthandCaption) {
            super(shorthandCaption);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode} and {@link ModifierKey}s.
         * 
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         * @param modifiers
         *            optional modifiers for shortcut
         */
        public ClickShortcut(Button button, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode}.
         * 
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         */
        public ClickShortcut(Button button, int keyCode) {
            this(button, keyCode, null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            button.click();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#isDisableOnClick()
     */
    @Override
    public boolean isDisableOnClick() {
        return getState().disableOnClick;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#setDisableOnClick(boolean)
     */
    @Override
    public void setDisableOnClick(boolean disableOnClick) {
        getState().disableOnClick = disableOnClick;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#getTabIndex()
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#getTabIndex()
     */
    @Override
    public int getTabIndex() {
        return getState().tabIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#focus()
     */
    @Override
    public void focus() {
        // Overridden only to make public
        super.focus();
    }

    @Override
    protected ButtonState getState() {
        return (ButtonState) super.getState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#setIcon(com.vaadin.server.Resource,
     * java.lang.String)
     */
    @Override
    public void setIcon(Resource icon, String iconAltText) {
        super.setIcon(icon);
        getState().iconAltText = iconAltText == null ? "" : iconAltText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#getIconAlternateText()
     */
    @Override
    public String getIconAlternateText() {
        return getState().iconAltText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#setIconAlternateText(java.lang.String)
     */
    @Override
    public void setIconAlternateText(String iconAltText) {
        getState().iconAltText = iconAltText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#setHtmlContentAllowed(boolean)
     */
    @Override
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().htmlContentAllowed = htmlContentAllowed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.IButton#isHtmlContentAllowed()
     */
    @Override
    public boolean isHtmlContentAllowed() {
        return getState().htmlContentAllowed;
    }

}
