/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.test.gui.inifileeditor;

import org.eclipse.swt.SWT;

import com.simulcraft.test.gui.access.Access;
import com.simulcraft.test.gui.access.ShellAccess;
import com.simulcraft.test.gui.access.TreeAccess;
import com.simulcraft.test.gui.access.TreeItemAccess;
import com.simulcraft.test.gui.access.WorkbenchWindowAccess;
import com.simulcraft.test.gui.util.WorkbenchUtils;
import com.simulcraft.test.gui.util.WorkspaceUtils;

public class InifileEditorUtils
{
    public static void createNewIniFileByWizard1(String projectName, String fileName, String networkName) {
        // Select the project in the "Project Explorer" view, and choose "New|Other..." from its context menu
        WorkbenchUtils.findInProjectExplorerView(projectName).chooseFromContextMenu("New|Other.*");

        // Open the "OMNeT++" category in the "New" dialog, and double-click "Ini file" in it
        ShellAccess shell = Access.findShellWithTitle("New");
        TreeAccess tree = shell.findTree();
        TreeItemAccess treeItem = tree.findTreeItemByContent(".*OMN.*");
        treeItem.click();
        shell.pressKey(SWT.ARROW_RIGHT); // open tree node
        tree.findTreeItemByContent("Ini.*").doubleClick();

        fillNewInifileWizard(null, fileName, networkName);

        // make sure the file really exists
        WorkspaceUtils.assertFileExists(projectName+"/"+fileName);
    }

    public static void createNewIniFileByWizard2(String parentFolder, String fileName, String networkName) {
        WorkbenchWindowAccess workbenchWindow = Access.getWorkbenchWindow();
        WorkbenchUtils.ensurePerspectiveActivated(".*Simul.*"); // so that we have "New|Inifile" in the menu
        workbenchWindow.chooseFromMainMenu("File|New.*|Ini.*");
        fillNewInifileWizard(parentFolder, fileName, networkName); // fill in wizard
        WorkspaceUtils.assertFileExists(parentFolder + "/" + fileName); // make sure file got created
    }

    public static void fillNewInifileWizard(String parentFolder, String fileName, String networkName) {
        // fill in the fields in the dialog, then click "Finish"
        ShellAccess shell = Access.findShellWithTitle("New Ini File");
        //Access.dumpWidgetHierarchy(shell.getShell());
        if (parentFolder != null)
            shell.findTextAfterLabel(".*parent folder.*").clickAndTypeOver(parentFolder);
        if (fileName != null)
            shell.findTextAfterLabel("File name.*").clickAndTypeOver(fileName);
        if (networkName != null)
            shell.findComboAfterLabel("NED Network:").clickAndTypeOver(networkName);
        shell.findButtonWithLabel("Finish").selectWithMouseClick();
    }
}
