package com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.implementation;

import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.MatchHandler;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.primitive.FloatMatcher;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Normal;

import java.util.List;
import java.util.regex.Pattern;

public class NormalMatcher extends MatchHandler<Normal> {
	
	
	private Pattern normalLinePattern = Pattern.compile("^vn.*$");

	@Override
	protected Pattern getPattern() {
		return this.normalLinePattern;
	}
	
	@Override
	protected void handleMatch(String group) {
		FloatMatcher floatMatcher = new FloatMatcher();
		floatMatcher.matchString(group);
		
		List<Float> coordinates = floatMatcher.getMatches();
		
		this.addMatch(new Normal(coordinates));
	}
	
}
