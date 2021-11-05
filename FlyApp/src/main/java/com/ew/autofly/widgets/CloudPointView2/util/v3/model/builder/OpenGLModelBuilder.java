package com.ew.autofly.widgets.CloudPointView2.util.v3.model.builder;

import com.ew.autofly.widgets.CloudPointView2.util.v3.model.DirectionalVertex;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Face;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.FaceProperty;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Normal;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.RawOpenGLModel;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.TexturePoint;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Vertex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OpenGLModelBuilder {
	
	public RawOpenGLModel buildOpenGLModel(List<Vertex> vertices,
										   List<Normal> normals,
										   List<TexturePoint> texturePoints,
										   List<List<FaceProperty>> faceProperties) {
		
		Iterator<List<FaceProperty>> facePropertyIterator = faceProperties.iterator();
		FaceBuilder faceBuilder = new FaceBuilder(vertices, normals, texturePoints);
		
		while(facePropertyIterator.hasNext()) {
			faceBuilder.buildFace(facePropertyIterator.next());
		}
		
		return new RawOpenGLModel(faceBuilder.getFaces(),
				faceBuilder.vertexManager.getVertexBoundingBox(),
				vertices);
	}
	
	private static class FaceBuilder {
		
		private VertexManager vertexManager;
		private List<Face> faces;
		
		public FaceBuilder(List<Vertex> vertices,
				List<Normal> normals,
				List<TexturePoint> texturePoints) {
			this.vertexManager = new VertexManager(vertices, normals, texturePoints);
			this.faces = new ArrayList<Face>();
		}
		
		public void buildFace(List<FaceProperty> faceProperties) {
			Iterator<FaceProperty> facePropertyIterator = faceProperties.iterator();
			List<DirectionalVertex> vertices = new ArrayList<DirectionalVertex>();
			
			while(facePropertyIterator.hasNext()) {
				vertices.add(this.vertexManager.buildAndRegisterVertex(facePropertyIterator.next()));
			}
			
			faces.add(new Face(vertices));
		}
		
		public List<Face> getFaces() {
			return this.faces;
		}
	}
	
}
