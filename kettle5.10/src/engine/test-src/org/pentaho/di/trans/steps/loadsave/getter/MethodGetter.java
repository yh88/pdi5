package org.pentaho.di.trans.steps.loadsave.getter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodGetter<T> implements Getter<T> {
  private final Method method;

  public MethodGetter( Method method ) {
    this.method = method;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public T get( Object obj ) {
    try {
      return (T) method.invoke( obj );
    } catch ( Exception e ) {
      throw new RuntimeException( "Error invoking " + method + " on " + obj, e );
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public Class<T> getType() {
    return (Class<T>) method.getReturnType();
  }

  @Override
  public Type getGenericType() {
    return method.getGenericReturnType();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( method == null ) ? 0 : method.hashCode() );
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    if ( getClass() != obj.getClass() ) {
      return false;
    }
    MethodGetter<?> other = (MethodGetter<?>) obj;
    if ( method == null ) {
      if ( other.method != null ) {
        return false;
      }
    } else {
      if ( !method.equals( other.method ) ) {
        return false;
      }
    }
    return true;
  }

}
