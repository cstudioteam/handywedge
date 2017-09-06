package com.handywedge.script;

import java.io.Reader;

import javax.enterprise.context.RequestScoped;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

@RequestScoped
public class FWScriptEngineWrapper implements FWScriptEngine, Compilable, Invocable {

  private ScriptEngine scriptEngine;

  FWScriptEngineWrapper(ScriptEngine scriptEngine) {
    this.scriptEngine = scriptEngine;
  }

  @Override
  public Object eval(String script, ScriptContext context) throws ScriptException {
    return scriptEngine.eval(script, context);
  }

  @Override
  public Object eval(Reader reader, ScriptContext context) throws ScriptException {
    return scriptEngine.eval(reader, context);
  }

  @Override
  public Object eval(String script) throws ScriptException {
    return scriptEngine.eval(script);
  }

  @Override
  public Object eval(Reader reader) throws ScriptException {
    return scriptEngine.eval(reader);
  }

  @Override
  public Object eval(String script, Bindings bindings) throws ScriptException {
    return scriptEngine.eval(script, bindings);
  }

  @Override
  public Object eval(Reader reader, Bindings bindings) throws ScriptException {
    return scriptEngine.eval(reader, bindings);
  }

  @Override
  public void put(String key, Object value) {
    scriptEngine.put(key, value);
  }

  @Override
  public Object get(String key) {
    return scriptEngine.get(key);
  }

  @Override
  public Bindings getBindings(int scope) {
    return scriptEngine.getBindings(scope);
  }

  @Override
  public void setBindings(Bindings bindings, int scope) {
    scriptEngine.setBindings(bindings, scope);
  }

  @Override
  public Bindings createBindings() {
    return scriptEngine.createBindings();
  }

  @Override
  public ScriptContext getContext() {
    return scriptEngine.getContext();
  }

  @Override
  public void setContext(ScriptContext context) {
    scriptEngine.setContext(context);
  }

  @Override
  public ScriptEngineFactory getFactory() {
    return scriptEngine.getFactory();
  }

  // Compilable
  @Override
  public CompiledScript compile(String script) throws ScriptException {
    return ((Compilable)scriptEngine).compile(script);
  }

  // Compilable
  @Override
  public CompiledScript compile(Reader script) throws ScriptException {
    return ((Compilable)scriptEngine).compile(script);
  }

  // Invocable
  @Override
  public Object invokeMethod(Object thiz, String name, Object... args)
      throws ScriptException, NoSuchMethodException {
    return ((Invocable)scriptEngine).invokeMethod(thiz, name, args);
  }

  // Invocable
  @Override
  public Object invokeFunction(String name, Object... args)
      throws ScriptException, NoSuchMethodException {
    return ((Invocable)scriptEngine).invokeFunction(name, args);
  }

  // Invocable
  @Override
  public <T> T getInterface(Class<T> clasz) {
    return ((Invocable)scriptEngine).getInterface(clasz);
  }

  // Invocable
  @Override
  public <T> T getInterface(Object thiz, Class<T> clasz) {
    return ((Invocable)scriptEngine).getInterface(thiz, clasz);
  }
}
