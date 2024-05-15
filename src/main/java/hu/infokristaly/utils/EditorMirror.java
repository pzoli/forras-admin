package hu.infokristaly.utils;

import javax.faces.component.UIComponent;

import hu.infokristaly.front.annotations.EditorInfo;
import hu.infokristaly.front.annotations.EditοrFieldInfo;

public class EditorMirror {

    @EditorInfo(fields={@EditοrFieldInfo()})
    public UIComponent parent;
    
}
