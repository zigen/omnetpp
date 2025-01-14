/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package com.simulcraft.test.gui.access;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.simulcraft.test.gui.core.UIStep;
import com.simulcraft.test.gui.core.InBackgroundThread;

public class TableItemAccess extends ClickableWidgetAccess
{
    public TableItemAccess(TableItem widget) {
        super(widget);
    }

    @Override
    public TableItem getWidget() {
        return (TableItem)widget;
    }

    @UIStep
    public TableAccess getTable() {
        return (TableAccess) createAccess(getWidget().getParent());
    }

    @UIStep
    public TableItemAccess reveal() {
        Table table = getWidget().getParent();
        int myIndex = ArrayUtils.indexOf(table.getItems(), getWidget());
        table.setTopIndex(myIndex); // scroll there
        return this;
    }

    @Override
    protected Point getAbsolutePointToClick() {
        Point point = toAbsolute(getCenter(getWidget().getBounds()));
        Assert.assertTrue("point to click is scrolled out", getTable().getAbsoluteBounds().contains(point));
        Assert.assertTrue("column has zero width", getWidget().getBounds().width > 0);
        return point;
    }

    @Override
    protected Point toAbsolute(Point point) {
        return getWidget().getParent().toDisplay(point);
    }

    /**
     * Useful for selecting a table item without incidentally activating its cell editor.
     */
    @UIStep
    public void clickLeftEdge() {
        Rectangle bounds = getWidget().getBounds();
        Point point = getWidget().getParent().toDisplay(new Point(1, bounds.y+bounds.height/2));
        Assert.assertTrue("point to click is scrolled out", getTable().getAbsoluteBounds().contains(point));
        Assert.assertTrue("column has zero width", bounds.width > 0);
        clickAbsolute(LEFT_MOUSE_BUTTON, point);
    }


    @Override
    protected Menu getContextMenu() {
        return (Menu)getWidget().getParent().getMenu();
    }

    @UIStep
    public void ensureChecked(boolean state) {
        if (getWidget().getChecked() != state) {
            reveal();
            click();
            pressKey(' ');
        }
    }

    @UIStep
    public void clickColumn(int index) {
        Point point = getWidget().getParent().toDisplay(getCenter(getWidget().getTextBounds(index)));
        Assert.assertTrue("point to click is scrolled out", getTable().getAbsoluteBounds().contains(point));
        clickAbsolute(LEFT_MOUSE_BUTTON, point);
    }

    @InBackgroundThread
    public TextAccess activateCellEditor() {
        Control oldFocusControl = getFocusControl().getControl();
        click();
        Control focusControl = getFocusControl().getControl();
        Assert.assertTrue("cell editor could not be activated", focusControl instanceof Text && oldFocusControl != focusControl);
        System.out.println(oldFocusControl.getClass().getSimpleName());
        return (TextAccess)createAccess(focusControl);
    }

    @InBackgroundThread
    public TextAccess activateCellEditor(int index) {
        Control oldFocusControl = getFocusControl().getControl();
        clickColumn(index);
        Control focusControl = getFocusControl().getControl();
        Assert.assertTrue("cell editor could not be activated", focusControl instanceof Text && oldFocusControl != focusControl);
        System.out.println(oldFocusControl.getClass().getSimpleName());
        return (TextAccess)createAccess(focusControl);
    }

    @InBackgroundThread
    public void clickAndTypeOver(String content) {
        TextAccess cellEditor = activateCellEditor();
        cellEditor.typeOver(content);
        cellEditor.pressEnter();
        //assertTextContent(Pattern.quote(content));  //XXX this would assume text gets stored "as is"
    }

    @InBackgroundThread
    public void clickAndTypeOver(int index, String content) {
        TextAccess cellEditor = activateCellEditor(index);
        cellEditor.typeOver(content);
        cellEditor.pressEnter();
        //assertTextContent(index, Pattern.quote(content));  //XXX this would assume text gets stored "as is"
    }

    @UIStep
    public void assertTextContent(String content) {
        Assert.assertTrue(getWidget().getText().matches(content));
    }

    @UIStep
    public void assertTextContent(int index, String content) {
        Assert.assertTrue(getWidget().getText(index).matches(content));
    }

}

