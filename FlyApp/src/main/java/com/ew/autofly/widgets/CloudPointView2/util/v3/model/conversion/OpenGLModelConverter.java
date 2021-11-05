package com.ew.autofly.widgets.CloudPointView2.util.v3.model.conversion;

import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Face;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.OpenGLModelData;

import java.util.List;

public interface OpenGLModelConverter {
	
	public OpenGLModelData convert(List<Face> faces);

}