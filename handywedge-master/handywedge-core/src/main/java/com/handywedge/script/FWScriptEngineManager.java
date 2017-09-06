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

  // 拡張メソッド（FW独自実装）
  /**
   * FWにて最適なエンジンを選択し、FWScriptEngine を返します。
   * @return FWScriptEngineオブジェクト
   */
  public FWScriptEngine getEngine() {
    long startTime = logger.perfStart("getEngine");
    FWScriptEngine engine = generateEngine();
    logger.perfEnd("getEngine", startTime);
    return engine;
  }

  // エンジン生成
  private FWScriptEngine generateEngine() {
    // nashorn固定
    FWScriptEngine engine = new FWScriptEngineWrapper(scriptEngineManager.getEngineByName("nashorn"));
    // コンテキスト情報をセット
    engine.put("fwContext", ctx);
    return engine;
  }

  /**
   * @deprecated ScriptEngineはFWにて最適なエンジンを制御、選択するため、当該メソッドは非推奨
   * @see #getEngineByName(String)
   */
  @Override
  @Deprecated
  public FWScriptEngine getEngineByName(String shortName) {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated ScriptEngineはFWにて最適なエンジンを制御、選択するため、当該メソッドは非推奨
   * @see #getEngineByExtension(String)
   */
  @Override
  @Deprecated
  public FWScriptEngine getEngineByExtension(String extension) {
    throw new UnsupportedOperationException();
  };

  /**
   * @deprecated ScriptEngineはFWにて最適なエンジンを制御、選択するため、当該メソッドは非推奨
   * @see #getEngineByMimeType(String)
   */
  @Override
  @Deprecated
  public FWScriptEngine getEngineByMimeType(String mimeType) {
    throw new UnsupportedOperationException();
  };

  /**
   * @deprecated ScriptEngineはFWにて最適なエンジンを制御、選択するため、当該メソッドは非推奨
   * @see #getEngineFactories()
   */
  @Override
  @Deprecated
  public List<ScriptEngineFactory> getEngineFactories() {
    throw new UnsupportedOperationException();
  };

  /**
   * @deprecated ScriptEngineはFWにて最適なエンジンを制御、選択するため、当該メソッドは非推奨
   * @see #registerEngineName(String, ScriptEngineFactory)
   */
  @Override
  @Deprecated
  public void registerEngineName(String name, ScriptEngineFactory factory) {
    throw new UnsupportedOperationException();
  };

  /**
   * @deprecated ScriptEngineはFWにて最適なエンジンを制御、選択するため、当該メソッドは非推奨
   * @see #registerEngineMimeType(String, ScriptEngineFactory)
   */
  @Override
  @Deprecated
  public void registerEngineMimeType(String type, ScriptEngineFactory factory) {
    throw new UnsupportedOperationException();
  };

  /**
   * @deprecated ScriptEngineはFWにて最適なエンジンを制御、選択するため、当該メソッドは非推奨
   * @see #registerEngineExtension(String, ScriptEngineFactory)
   */
  @Override
  @Deprecated
  public void registerEngineExtension(String extension, ScriptEngineFactory factory) {
    throw new UnsupportedOperationException();
  };
}
