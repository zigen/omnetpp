package org.omnetpp.simulation.inspectors;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.omnetpp.common.color.ColorFactory;
import org.omnetpp.common.ui.HoverInfo;
import org.omnetpp.simulation.canvas.IInspectorContainer;
import org.omnetpp.simulation.figures.FigureUtils;
import org.omnetpp.simulation.figures.IInspectorFigure;
import org.omnetpp.simulation.inspectors.actions.IInspectorAction;
import org.omnetpp.simulation.model.cObject;

/**
 * Default implementation for IInspectorPart, base class for inspector classes
 *
 * @author andras
 */
public abstract class AbstractInspectorPart implements IInspectorPart, IAdaptable {
    protected InspectorDescriptor descriptor;
    protected cObject object;
    protected IInspectorFigure figure;
    protected IInspectorContainer inspectorContainer;
    protected boolean isSelected;

    public AbstractInspectorPart(InspectorDescriptor descriptor, IInspectorContainer parent, cObject object) {
        this.descriptor = descriptor;
        this.object = object;
        this.inspectorContainer = parent;

        figure = createFigure();
        figure.setRequestFocusEnabled(true);
    }

    @Override
    public InspectorDescriptor getDescriptor() {
        return descriptor;
    }

    protected abstract IInspectorFigure createFigure();

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        // being able to adapt to cObject helps working with the selection
        if (adapter.isInstance(object))
            return object;
        if (adapter.isInstance(this))
            return this;
        return null;
    }

    protected IInspectorAction my(IInspectorAction action) {
        action.setContext(this);
        return action;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void raiseToTop() {
        // there's no public API in draw2d for changing the order (Z-order) of children, but
        // Randy Hudson himself suggests just to change the list returned by getChildren()...
        // http://dev.eclipse.org/mhonarc/lists/gef-dev/msg00914.html
        List siblings = figure.getParent().getChildren();
        siblings.remove(figure);
        siblings.add(figure);
        figure.getParent().invalidate();
    }

    @Override
    public void setFocus() {
        figure.requestFocus();
    }

    public void dispose() {
        System.out.println("disposing inspector: " + object);

        if (figure != null) {
            if (figure.getParent() != null)
                figure.getParent().remove(figure);
            figure = null;
        }

        object = null;
    }

    public boolean isDisposed() {
        return object == null;
    }

    @Override
    public cObject getObject() {
        return object;
    }

    @Override
    public IInspectorFigure getFigure() {
        return figure;
    }

    @Override
    public IFigure findFigureContaining(cObject object) {
        return figure; //FIXME only if our object is the same as, or ancestor of, the given object
    }

    @Override
    public Control getSWTControl() {
        return null;
    }

    @Override
    public void refresh() {
        Assert.isTrue(figure.getParent()!=null && inspectorContainer!=null, "inspector not yet installed");
        Assert.isTrue(object != null, "inspector already disposed");

        // automatically close the inspector when the underlying object gets deleted
        if (object.isDisposed()) {
            System.out.println("object disposed - auto-closing inspector: ");
            getContainer().close(this);
        }
    }

    @Override
    public IInspectorContainer getContainer() {
        return inspectorContainer;
    }

    @Override
    public void selectionChanged(IStructuredSelection selection) {
        boolean oldSelectedState = isSelected;
        isSelected = selection.toList().contains(this);
        if (oldSelectedState != isSelected)
            setSelectionMark(isSelected);
    }

    protected void setSelectionMark(boolean isSelected) {
        // override in subclasses to provide a visually more attractive implementation
        figure.setBackgroundColor(isSelected ? ColorFactory.GREY50 : null);
    }

    @Override
    public HoverInfo getHoverFor(int x, int y) {
        return null;  // override to provide hover information
    }

    @Override
    public int getDragOperation(IFigure figure, int x, int y) {
        // a more-or-less sensible default, to be refined in subclasses
        return FigureUtils.getBorderResizeInsideMoveDragOperation(x, y, figure.getBounds());
    }

    @Override
    public int getDragOperation(Control control, int x, int y) {
        // a more-or-less sensible default, to be refined in subclasses that are SWT inspectors
        Point size = control.getSize();
        return FigureUtils.getBorderResizeInsideMoveDragOperation(x, y, new Rectangle(0, 0, size.x, size.y));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + (object==null ? "<object=null>" : object.toString());
    }

}