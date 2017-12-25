package org.jdesktop.test.matchers;

import static org.hamcrest.CoreMatchers.equalTo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.mockito.ArgumentMatcher;

class EquivalentMatcher<T> extends ArgumentMatcher<T> {
    private final T object;
    
    public EquivalentMatcher(T object) {
        this.object = object;
    }
    
    @Override
    public boolean matches(Object argument) {
        if (equalTo(object).matches(argument)) {
            //short circuit: equal is always equivalent
            return true;
        }
        
        if (argument != null && object.getClass() == argument.getClass()) {
            BeanInfo beanInfo = null;
            
            try {
                beanInfo = Introspector.getBeanInfo(object.getClass());
            } catch (IntrospectionException shouldNeverHappen) {
                throw new Error(shouldNeverHappen);
            }
            
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if (pd.getReadMethod() == null) {
                    continue;
                }
                
                Object value1 = null;
                
                try {
                    value1 = pd.getReadMethod().invoke(object);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    new Error(e);
                }
                
                Object value2 = null;
                
                try {
                    value2 = pd.getReadMethod().invoke(object);
                } catch (RuntimeException e) {
                    //prevent us from wrapping RuntimExceptions unnecessarily
                    throw e;
                } catch (Exception shouldNeverHappen) {
                    new Error(shouldNeverHappen);
                }
                
                if (!equalTo(value1).matches(value2)) {
                    return false;
                }
            }
            
            
            return true;
        }
        
        return false;
    }
}
