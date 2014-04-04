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
package com.vaadin.shared.ui.button;

import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public interface IsButton {

    /**
     * Adds the button click listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    void addClickListener(ClickListener listener);

    /**
     * Removes the button click listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    void removeClickListener(ClickListener listener);

    /**
     * Simulates a button click, notifying all server-side listeners.
     * 
     * No action is taken is the button is disabled.
     */
    void click();

    void addBlurListener(BlurListener listener);

    void removeBlurListener(BlurListener listener);

    void addFocusListener(FocusListener listener);

    void removeFocusListener(FocusListener listener);

    /**
     * Makes it possible to invoke a click on this button by pressing the given
     * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
     * The shortcut is global (bound to the containing Window).
     * 
     * @param keyCode
     *            the keycode for invoking the shortcut
     * @param modifiers
     *            the (optional) modifiers for invoking the shortcut, null for
     *            none
     */
    void setClickShortcut(int keyCode, int... modifiers);

    /**
     * Removes the keyboard shortcut previously set with
     * {@link #setClickShortcut(int, int...)}.
     */
    void removeClickShortcut();

    /**
     * Determines if a button is automatically disabled when clicked. See
     * {@link #setDisableOnClick(boolean)} for details.
     * 
     * @return true if the button is disabled when clicked, false otherwise
     */
    boolean isDisableOnClick();

    /**
     * Determines if a button is automatically disabled when clicked. If this is
     * set to true the button will be automatically disabled when clicked,
     * typically to prevent (accidental) extra clicks on a button.
     * <p>
     * Note that this is only used when the click comes from the user, not when
     * calling {@link #click()}.
     * </p>
     * 
     * @param disableOnClick
     *            true to disable button when it is clicked, false otherwise
     */
    void setDisableOnClick(boolean disableOnClick);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#getTabIndex()
     */
    int getTabIndex();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    void setTabIndex(int tabIndex);

    void focus();

    /**
     * Sets the component's icon and alt text.
     * 
     * An alt text is shown when an image could not be loaded, and read by
     * assisitve devices.
     * 
     * @param icon
     *            the icon to be shown with the component's caption.
     * @param iconAltText
     *            String to use as alt text
     */
    void setIcon(Resource icon, String iconAltText);

    /**
     * Returns the icon's alt text.
     * 
     * @return String with the alt text
     */
    String getIconAlternateText();

    void setIconAlternateText(String iconAltText);

    /**
     * Set whether the caption text is rendered as HTML or not. You might need
     * to retheme button to allow higher content than the original text style.
     * 
     * If set to true, the captions are passed to the browser as html and the
     * developer is responsible for ensuring no harmful html is used. If set to
     * false, the content is passed to the browser as plain text.
     * 
     * @param htmlContentAllowed
     *            <code>true</code> if caption is rendered as HTML,
     *            <code>false</code> otherwise
     */
    void setHtmlContentAllowed(boolean htmlContentAllowed);

    /**
     * Return HTML rendering setting
     * 
     * @return <code>true</code> if the caption text is to be rendered as HTML,
     *         <code>false</code> otherwise
     */
    boolean isHtmlContentAllowed();

}