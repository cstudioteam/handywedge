package com.handywedge.script;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.script.Bindings;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import com.handywedge.context.FWContext;
import com.handywedge.log.FWLogger;

/**
 * ScriptEngineを生成するクラスです。<br>
 * JavaScriptへアクセスするにはgetEngineメソッドからScriptEngineインスタンスを取得してアクセスします。 <br>
 *
 * <pre>
 *
 * {@code   @Inject}
 * {@code   private FWScriptEngineManager engineManager;
 * ・・・
 *    FWScriptEngine engine = engineManager.getEngine();
 *    engine.eval("print('Hello World!');");
 * ・・・
 * }
 *
 * </pre>
 *
 * @see ScriptEngineManager
 * @see FWScriptEngine
 */
@RequestScoped
public class FWScriptEngineManager extends ScriptEngineManager {

	@Inject
	private FWLogger logger;

	@Inject
	private FWContext ctx;

	private ScriptEngineManager scriptEngineManager;


	public FWScriptEngineManager() {
		this.scriptEngineManager = new ScriptEngineManager();
	}

	public FWScriptEngineManager(ClassLoader loader) {
		this.scriptEngineManager = new ScriptEngineManager(loader);
	}

	@Override
	public void setBindings(Bindings bindings) {
		logger.trace("setBindings bindings={}", bindings);
		scriptEngineManager.setBindings(bindings);
	}

	@Override
	public Bindings getBindings() {
		logger.trace("getBindings");
		return scriptEngineManager.getBindings();
	}

	@Override
	public void put(String key, Object value) {
		logger.trace("put key={}, value={}", key, value);
		scriptEngineManager.put(key, value);
	}

	@Override
	public Object get(String key) {
		logger.trace("get key={}", key);
		return scriptEngineManager.get(key);
	}

	@Override
	public FWScriptEngine getEngineByName(String shortName) {
		long startTime = logger.perfStart("getEngineByname");
		FWScriptEngine engine = new FWScriptEngineWrapper(scriptEngineManager.getEngineByName(shortName));
		// コンテキスト情報をセット
		engine.put("fwContext", ctx);
		logger.perfEnd("getEngineByName", startTime);
		return engine;
	}

	/**
	 * @see #getEngineByExtension(String)
	 */
	@Override
	public FWScriptEngine getEngineByExtension(String extension) {
		long startTime = logger.perfStart("getEngineByExtension");
		logger.trace("getEngineByExtension mimeType={}", extension);
		FWScriptEngine engine =  new FWScriptEngineWrapper(scriptEngineManager.getEngineByExtension(extension));
		// コンテキスト情報をセット
		engine.put("fwContext", ctx);
		logger.perfEnd("getEngineByExtension", startTime);
		return engine;
	};

	/**
	 * @see #getEngineByMimeType(String)
	 */
	@Override
	public FWScriptEngine getEngineByMimeType(String mimeType) {
		long startTime = logger.perfStart("getEngineByMimeType");
		logger.trace("getEngineByMimeType mimeType={}", mimeType);
		FWScriptEngine engine =  new FWScriptEngineWrapper(scriptEngineManager.getEngineByMimeType(mimeType));
		// コンテキスト情報をセット
		engine.put("fwContext", ctx);
		logger.perfEnd("getEngineByMimeType", startTime);
		return engine;
	};

	/**
	 * @see #getEngineFactories()
	 */
	@Override
	public List<ScriptEngineFactory> getEngineFactories() {
		logger.trace("getEngineFactories");
		return scriptEngineManager.getEngineFactories();
	};

	/**
	 * @see #registerEngineName(String, ScriptEngineFactory)
	 */
	@Override
	public void registerEngineName(String name, ScriptEngineFactory factory) {
		logger.trace("registerEngineName name={}, factory={}", name, factory);
		scriptEngineManager.registerEngineName(name, factory);
	};

	/**
	 * @see #registerEngineMimeType(String, ScriptEngineFactory)
	 */
	@Override
	public void registerEngineMimeType(String type, ScriptEngineFactory factory) {
		logger.trace("registerEngineMimeType type={}, factory={}", type, factory);
		scriptEngineManager.registerEngineMimeType(type, factory);
	};

	/**
	 * @see #registerEngineExtension(String, ScriptEngineFactory)
	 */
	@Override
	public void registerEngineExtension(String extension, ScriptEngineFactory factory) {
		logger.trace("registerEngineExtension extension={}, factory={}", extension, factory);
		scriptEngineManager.registerEngineExtension(extension, factory);
	};
}
