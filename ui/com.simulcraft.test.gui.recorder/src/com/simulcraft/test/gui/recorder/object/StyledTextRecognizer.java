/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package com.simulcraft.test.gui.recorder.object;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

import com.simulcraft.test.gui.recorder.GUIRecorder;
import com.simulcraft.test.gui.recorder.JavaSequence;

public class StyledTextRecognizer extends ObjectRecognizer {
    public StyledTextRecognizer(GUIRecorder recorder) {
        super(recorder);
    }

    public JavaSequence identifyObject(Object uiObject) {
        if (uiObject instanceof StyledText) {
            StyledText styledText = (StyledText)uiObject;
            Composite container = findContainer(styledText);
            // dumpWidgetHierarchy(container);
            if (findDescendantControl(container, StyledText.class) == uiObject)
                return makeMethodCall(container, expr("findSyledText()", 0.8, styledText));
        }
        return null;
    }
}