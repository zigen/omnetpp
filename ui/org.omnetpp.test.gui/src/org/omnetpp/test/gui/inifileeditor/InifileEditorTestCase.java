/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.test.gui.inifileeditor;

import org.omnetpp.test.gui.access.InifileEditorAccess;
import org.omnetpp.test.gui.core.ProjectFileTestCase;

import com.simulcraft.test.gui.access.Access;
import com.simulcraft.test.gui.access.WorkbenchWindowAccess;


public class InifileEditorTestCase extends ProjectFileTestCase {
    public InifileEditorTestCase() {
        super("test.ini");
    }

    protected InifileEditorAccess findInifileEditor() {
        WorkbenchWindowAccess workbenchWindow = Access.getWorkbenchWindow();
        return (InifileEditorAccess)workbenchWindow.findMultiPageEditorPartByTitle(fileName);
    }
}
