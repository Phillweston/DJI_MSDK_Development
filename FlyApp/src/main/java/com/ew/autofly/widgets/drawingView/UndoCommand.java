package com.ew.autofly.widgets.drawingView;

public interface UndoCommand {
    public void undo();

    public void redo();

    public boolean canUndo();

    public boolean canRedo();
}
