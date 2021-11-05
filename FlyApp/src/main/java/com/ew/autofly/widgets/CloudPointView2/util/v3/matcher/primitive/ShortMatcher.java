package com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.primitive;

import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.MatchHandler;

import java.util.regex.Pattern;

public class ShortMatcher extends MatchHandler<Short> {

	private Pattern shortNumberPattern = Pattern.compile("-?[1-9]++[0-9]*");
	
	@Override
	protected Pattern getPattern() {
		return this.shortNumberPattern;
	}

	@Override
	protected void handleMatch(String group) {
		this.addMatch(Short.parseShort(group));
	}

}
