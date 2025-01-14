/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package com.simulcraft.test.gui.util;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.omnetpp.common.util.Predicate;

import com.simulcraft.test.gui.access.Access;
import com.simulcraft.test.gui.access.CompositeAccess;
import com.simulcraft.test.gui.access.ShellAccess;
import com.simulcraft.test.gui.access.TreeAccess;
import com.simulcraft.test.gui.access.TreeItemAccess;
import com.simulcraft.test.gui.access.ViewPartAccess;
import com.simulcraft.test.gui.access.WorkbenchWindowAccess;
import com.simulcraft.test.gui.core.GUITestCase;
import com.simulcraft.test.gui.core.InBackgroundThread;
import com.simulcraft.test.gui.core.UIStep;

public class WorkbenchUtils
{
    public static void choosePerspectiveFromDialog(String perspectiveLabel) {
        WorkbenchWindowAccess workbenchWindow = Access.getWorkbenchWindow();
        workbenchWindow.chooseFromMainMenu("Window|Open Perspective|Other.*");
        ShellAccess dialog = Access.findShellWithTitle("Open Perspective");
        dialog.findTable().findTableItemByContent(perspectiveLabel).reveal().doubleClick();
    }

    public static void ensurePerspectiveActivated(String perspectiveLabel) {
        WorkbenchWindowAccess workbenchWindow = Access.getWorkbenchWindow();
        if (!workbenchWindow.getPerspective().getLabel().matches(perspectiveLabel))
            choosePerspectiveFromDialog(perspectiveLabel);
    }

    public static ViewPartAccess chooseViewFromDialog(String viewCategory, String viewLabel) {
        WorkbenchWindowAccess workbenchWindow = Access.getWorkbenchWindow();
        workbenchWindow.chooseFromMainMenu("Window|Show View|Other.*");
        TreeAccess tree = Access.findShellWithTitle("Show View").findTree();
        tree.findTreeItemByContent(viewCategory).reveal().click();
        tree.pressKey(SWT.ARROW_RIGHT); // open category node
        tree.findTreeItemByContent(viewLabel).reveal().doubleClick();
        //return workbenchWindow.findViewPartByTitle(viewLabel, true);
        return (ViewPartAccess) workbenchWindow.getActivePart();
    }

    public static ViewPartAccess ensureViewActivated(String viewCategory, String viewLabel) {
        WorkbenchWindowAccess workbenchWindow = Access.getWorkbenchWindow();
        if (workbenchWindow.collectViewPartsByTitle(viewLabel, false).size() == 0)
            return chooseViewFromDialog(viewCategory, viewLabel);
        else {
            workbenchWindow.findViewPartByTitle(viewLabel).ensureActivated();
            return (ViewPartAccess)workbenchWindow.getActivePart();
        }
    }

    public static TreeItemAccess findInProjectExplorerView(String path) {
        ViewPartAccess projectExplorerView = ensureViewActivated("General", "Project Explorer");
        TreeAccess tree = projectExplorerView.findTree();
        tree.assertHasFocus();
        return tree.findTreeItemByPath(path);
    }

    public static ShellAccess openProjectPropertiesFromProjectExplorerView(String projectName) {
        findInProjectExplorerView(projectName).activateContextMenuWithMouseClick().findMenuItemByLabel(".*Properties.*").activateWithMouseClick();
        return WorkbenchWindowAccess.findShellWithTitle("Properties.*" + projectName + ".*");
    }

    public static void refreshProjectFromProjectExplorerView(String projectName) {
        findInProjectExplorerView(projectName).reveal().chooseFromContextMenu("Refresh.*");;
    }

    public static void assertNoErrorMessageInProblemsView() {
        ViewPartAccess problemsView = WorkbenchUtils.ensureViewActivated("General", "Problems");
        // TODO: how do we know that validation has already taken place?
        problemsView.findTree().assertEmpty();
    }

    // FIXME should work in a case when two or more message is matching
    public static void assertErrorMessageInProblemsView(String errorText) {
        ViewPartAccess problemsView = WorkbenchUtils.ensureViewActivated("General", "Problems");
        problemsView.findTree().findTreeItemByContent(errorText);
    }

    @InBackgroundThread
    public static void waitUntilProgressViewContains(String text, double timeout) {
        double oldRetryTimeout = GUITestCase.getRetryTimeout();
        try {
            ViewPartAccess progressView = WorkbenchUtils.ensureViewActivated("General", "Progress.*");
            GUITestCase.setRetryTimeout(timeout);
            progressView.getComposite().findLabel(text);
        } finally {
            GUITestCase.setRetryTimeout(oldRetryTimeout);
        }
    }

    @InBackgroundThread
    public static void waitUntilProgressViewNotContains(String text, double timeout) {
        double oldRetryTimeout = GUITestCase.getRetryTimeout();
        try {
            ViewPartAccess progressView = WorkbenchUtils.ensureViewActivated("General", "Progress.*");
            GUITestCase.setRetryTimeout(timeout);
            ensureNoSuchLabel(progressView.getComposite(), text);
        } finally {
            GUITestCase.setRetryTimeout(oldRetryTimeout);
        }
    }

    @UIStep
    private static void ensureNoSuchLabel(CompositeAccess composite, String label) {
        Assert.assertTrue(composite.collectDescendantControls(Predicate.labelWithText(label)).isEmpty());
    }

}
