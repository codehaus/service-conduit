/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.xstream.factory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.AttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.CachingMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.EnumMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.OuterClassMapper;
import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

/**
 * Default implemenation of XStreamFactory. The factory may be configured with custom converters and drivers.
 *
 * @version $Rev: 4842 $ $Date: 2008-06-20 11:47:22 +0100 (Fri, 20 Jun 2008) $
 */
public class XStreamFactoryImpl implements XStreamFactory {
    private ClassLoaderRegistry registry;
    private ReflectionProvider reflectionProvider;

    public XStreamFactoryImpl(@Reference ClassLoaderRegistry registry) {
        this.registry = registry;
        JVM jvm = new JVM();
        reflectionProvider = jvm.bestReflectionProvider();
    }

    public XStream createInstance() {
        ClassLoader cl = XStreamFactoryImpl.class.getClassLoader();
        ClassLoaderStaxDriver driver = new ClassLoaderStaxDriver(cl);
        Mapper mapper = buildMapper(cl);
        return new XStream(reflectionProvider, mapper, driver);
    }


    private Mapper buildMapper(ClassLoader cl) {
        // method exists to replace the default Mapper with the ClassLoaderMapper
        Mapper mapper = new ClassLoaderMapper(registry, cl);
        // note do not use  XStream11XmlFriendlyMapper
        mapper = new ClassAliasingMapper(mapper);
        mapper = new FieldAliasingMapper(mapper);
        mapper = new AttributeAliasingMapper(mapper);
        mapper = new AttributeMapper(mapper);
        mapper = new ImplicitCollectionMapper(mapper);
        mapper = new DynamicProxyMapper(mapper);
        if (JVM.is15()) {
            mapper = new EnumMapper(mapper);
        }
        mapper = new OuterClassMapper(mapper);
        mapper = new ArrayMapper(mapper);
        mapper = new DefaultImplementationsMapper(mapper);
        mapper = new ImmutableTypesMapper(mapper);
        mapper = new CachingMapper(mapper);
        return mapper;
    }


}
