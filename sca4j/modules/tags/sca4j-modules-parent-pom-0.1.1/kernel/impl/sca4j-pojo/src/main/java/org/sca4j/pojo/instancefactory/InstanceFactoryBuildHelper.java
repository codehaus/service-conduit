/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.pojo.instancefactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.sca4j.scdl.Signature;

/**
 * Utility methods used when creating ReflectiveInstanceFactoryProviders.
 *
 * @version $Rev: 5238 $ $Date: 2008-08-20 20:27:39 +0100 (Wed, 20 Aug 2008) $
 */
public interface InstanceFactoryBuildHelper {

    /**
     * Loads the class using the given classloader
     *
     * @param cl   the classloader to load the class with
     * @param name the name of the class
     * @return the loaded class
     * @throws ClassNotFoundException if the class is not accessible to the classloader
     */
    Class<?> loadClass(ClassLoader cl, String name) throws ClassNotFoundException;

    /**
     * Returns the constructor on the given class matching the signature.
     *
     * @param implClass the class
     * @param signature the constructor signature
     * @return the constructor
     * @throws ClassNotFoundException if one of the constructor parameters could not be loaded
     * @throws NoSuchMethodException  if no matching constructor could be found
     */
    <T> Constructor<T> getConstructor(Class<T> implClass, Signature signature) throws ClassNotFoundException, NoSuchMethodException;

    /**
     * Returns the method on the given class matching the signature.
     *
     * @param implClass the class
     * @param signature the method signature
     * @return the constructor
     * @throws ClassNotFoundException if one of the method parameters could not be loaded
     * @throws NoSuchMethodException  if no matching method could be found
     */
    Method getMethod(Class<?> implClass, Signature signature) throws NoSuchMethodException, ClassNotFoundException;

}
