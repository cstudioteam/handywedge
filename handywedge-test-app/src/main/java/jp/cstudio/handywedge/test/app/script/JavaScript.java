package jp.cstudio.handywedge.test.app.script;

import java.io.StringReader;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.Invocable;

import com.handywedge.script.FWScriptEngine;
import com.handywedge.script.FWScriptEngineManager;

import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class JavaScript {

	@Inject
	private FWScriptEngineManager engineManager;

	@Getter
	private String result1;

	@Getter
	@Setter
	private String script1 = "'UserID=' + fwContext.getUser().getId();";

	@Getter
	@Setter
	private String script2 = "function f(arg1, arg2) {\r\n" +
			"  var ans = arg1 + arg2;\r\n" +
			"  return 'UserID=' + fwContext.getUser().getId() + ', Ans.=' + ans;\r\n" +
			"}\r\n" ;

	@Getter
	@Setter
	private int arg1 = 10;

	@Getter
	@Setter
	private int arg2 = 5;

	@Getter
	private String result2;


	public String eval() {

		FWScriptEngine engine = engineManager.getEngine();

		try {
		  result1 = (String)engine.eval(script1);
		} catch (Exception e) {
		  e.printStackTrace();
		}

		return "";
	}

	public String callFunction() {

		FWScriptEngine engine = engineManager.getEngine();

		try {
		  StringReader reader = new StringReader(script2);
		  engine.eval(reader);
		  Invocable inv = (Invocable) engine;
		  result2 = (String)inv.invokeFunction("f", arg1, arg2 );
		} catch (Exception e) {
		  e.printStackTrace();
		}

		return "";
	}
}
