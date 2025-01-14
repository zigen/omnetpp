/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.scave.editors;

import java.util.concurrent.Callable;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.omnetpp.common.properties.PropertySource;
import org.omnetpp.common.ui.GenericTreeNode;
import org.omnetpp.scave.charting.properties.ChartProperties;
import org.omnetpp.scave.charting.properties.ChartSheetProperties;
import org.omnetpp.scave.charting.properties.VectorChartProperties;
import org.omnetpp.scave.engine.ResultFileManager;
import org.omnetpp.scave.model.Chart;
import org.omnetpp.scave.model.ChartSheet;
import org.omnetpp.scave.model.ProcessingOp;
import org.omnetpp.scave.model.SetOperation;
import org.omnetpp.scave.model2.ChartLine;
import org.omnetpp.scave.model2.ProcessingOpPropertySource;
import org.omnetpp.scave.model2.ResultFilePayload;
import org.omnetpp.scave.model2.ResultItemRef;
import org.omnetpp.scave.model2.RunPayload;
import org.omnetpp.scave.model2.SetOperationPropertySource;

/**
 * Provides properties of scave model objects and charts.
 *
 * @author tomi
 */
public class ScavePropertySourceProvider implements IPropertySourceProvider {

    AdapterFactory adapterFactory;
    IPropertySourceProvider delegate;
    ResultFileManager manager;

    public ScavePropertySourceProvider(AdapterFactory adapterFactory, ResultFileManager manager) {
        this.adapterFactory = adapterFactory;
        this.delegate = new AdapterFactoryContentProvider(adapterFactory);
        this.manager = manager;
    }

    public IPropertySource getPropertySource(final Object object) {
        if (object instanceof GenericTreeNode)
            return getPropertySource(((GenericTreeNode)object).getPayload());
        else if (object instanceof Chart)
            return ChartProperties.createPropertySource((Chart)object, manager);
        else if (object instanceof ChartSheet)
            return ChartSheetProperties.createPropertySource((ChartSheet)object, delegate.getPropertySource(object));
        else if (object instanceof SetOperation) {
            IItemPropertySource itemPropertySource =
                (IItemPropertySource) adapterFactory.adapt(object, IItemPropertySource.class);
            return new SetOperationPropertySource(object, itemPropertySource, manager);
        }
        else if (object instanceof ProcessingOp)
            return new ProcessingOpPropertySource((ProcessingOp)object);
        else if (object instanceof PropertySource)
            return (PropertySource)object;
        else if (object instanceof ChartLine) {
            ChartLine lineID = (ChartLine)object;
            ChartProperties properties = ChartProperties.createPropertySource(lineID.getChart(), manager);
            if (properties instanceof VectorChartProperties)
                return ((VectorChartProperties)properties).getLineProperties(lineID.getKey());
        }
        else if (object instanceof ResultItemRef)
            return new ResultItemPropertySource((ResultItemRef)object);
        else if (object instanceof ResultFilePayload)
            return new ResultFilePropertySource(manager.getFile(((ResultFilePayload)object).getFilePath()));
        else if (object instanceof RunPayload)
            return ResultFileManager.callWithReadLock(manager, new Callable<RunPropertySource>() {
                public RunPropertySource call() throws Exception {
                    return new RunPropertySource(manager.getRunByName(((RunPayload)object).getRunName()));
                }
            });

        return delegate.getPropertySource(object);
    }
}
