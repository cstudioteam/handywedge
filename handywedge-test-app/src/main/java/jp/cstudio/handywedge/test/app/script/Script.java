package jp.cstudio.handywedge.test.app.script;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.Invocable;
import javax.script.ScriptEngineFactory;

import com.handywedge.script.FWScriptEngine;
import com.handywedge.script.FWScriptEngineManager;


import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class Script {

	@Inject
	private FWScriptEngineManager engineManager;

	private List<String> languages;

	@Getter
	@Setter
	private String shortName;

	@Getter
	private String result1;

	@Getter
	@Setter
	private String script1;

	@Getter
	@Setter
	private String script2;

	@Getter
	@Setter
	private int arg1 = 10;

	@Getter
	@Setter
	private int arg2 = 5;

	@Getter
	private String result2;

	static {


	}

	public String eval() {

		if (shortName == null || shortName.trim().length() == 0) return "";
		FWScriptEngine engine = engineManager.getEngineByName(shortName);

		try {
			result1 = (String) engine.eval(script1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public String callFunction() {

		if (shortName == null || shortName.trim().length() == 0) return "";
		FWScriptEngine engine = engineManager.getEngineByName(shortName);

		try {
			StringReader reader = new StringReader(script2);
			engine.eval(reader);
			Invocable inv = (Invocable) engine;
			result2 = (String) inv.invokeFunction("f", arg1, arg2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public List<String> getLanguages() {
		if (languages == null) {
			languages = new ArrayList<String>();
			languages.add("");
			for (final ScriptEngineFactory factory : engineManager.getEngineFactories()) {
				System.out.println(" lang" + factory.getLanguageName());
				languages.add(factory.getLanguageName());
			}
		}
		return languages;
	}

	public void onLanguageChange() {

		if ("ECMAScript".equalsIgnoreCase(shortName)) {
			script1 = "'UserID=' + fwContext.getUser().getId();";
			script2 = "function f(arg1, arg2) {\r\n" + "  var ans = arg1 + arg2;\r\n"
					+ "  return 'UserID=' + fwContext.getUser().getId() + ', Ans.=' + ans;\r\n" + "}\r\n";
		} else if ("python".equalsIgnoreCase(shortName)) {
			script1 = "'UserID=' + fwContext.getUser().getId();";
			script2 = "def f(arg1, arg2): \r\n" +
					"  return 'UserID=' + fwContext.getUser().getId() + ', Ans.=' + (arg1 + arg2)\r\n";
		} else {
			script1 = "";
			script2 = "";
		}

	}

}
