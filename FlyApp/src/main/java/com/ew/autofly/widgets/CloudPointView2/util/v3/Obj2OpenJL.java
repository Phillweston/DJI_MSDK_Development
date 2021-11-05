package com.ew.autofly.widgets.CloudPointView2.util.v3;

import com.ew.autofly.widgets.CloudPointView2.util.v3.io.LineReader;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.MatchHandler;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.MatchHandlerPool;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.implementation.FaceMatcher;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.implementation.NormalMatcher;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.implementation.TextureMatcher;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.implementation.VertexMatcher;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.FaceProperty;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Normal;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.RawOpenGLModel;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.TexturePoint;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Vertex;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.builder.OpenGLModelBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * Usage:
 * 
 * <pre>
 * public static void main(String [] args) {
 *		OpenGLModelData openGLModelData;
 *		try {
 *			RawOpenGLModel openGLModel = new Obj2OpenJL().convert("src/org/obj2openjl/box.obj");
 *			openGLModelData = openGLModel.getDataForGLDrawElements();
 *			
 *
 *
 *		} catch (FileNotFoundException e) {
 *			e.printStackTrace();
 *		}
 *	}
 * </pre>
 * 
 * 
 * @author Miffels
 *
 */
public class Obj2OpenJL {
	
	private MatchHandlerPool matchHandlerPool;
	private MatchHandler<Vertex> vertexMatcher;
	private MatchHandler<Normal> normalMatcher;
	private MatchHandler<TexturePoint> textureMatcher;
	private MatchHandler<List<FaceProperty>> faceMatcher;
	
	public Obj2OpenJL() {
		this.resetMatchers();
	}
	
	private void resetMatchers() {
		this.vertexMatcher = new VertexMatcher();
		this.normalMatcher = new NormalMatcher();
		this.textureMatcher = new TextureMatcher();
		this.faceMatcher = new FaceMatcher();
		
		this.matchHandlerPool = new MatchHandlerPool(
				this.vertexMatcher,
				this.normalMatcher,
				this.textureMatcher,
				this.faceMatcher);
	}
	
	public RawOpenGLModel convert(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if(!file.exists() && !fileName.endsWith(".obj") && !fileName.endsWith(".wvo")) {
			file = new File(fileName + ".obj");
		}
		if(!file.exists()) {
			file = new File(fileName + ".wvo");
		}
		return this.convert(new FileInputStream(file));
	}
	
	public RawOpenGLModel convert(InputStream inputStream) {
		this.resetMatchers();
		
		matchHandlerPool.handleAll(new LineReader(inputStream));
		
		return this.buildOpenGLModel();
	}
	
	private RawOpenGLModel buildOpenGLModel() {
		return new OpenGLModelBuilder().buildOpenGLModel(
				this.vertexMatcher.getMatches(),
				this.normalMatcher.getMatches(),
				this.textureMatcher.getMatches(),
				this.faceMatcher.getMatches());
		
	}
}