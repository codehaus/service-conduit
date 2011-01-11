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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.fabric.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.spi.command.Command;
import org.sca4j.spi.generator.AddCommandGenerator;
import org.sca4j.spi.generator.CommandGenerator;
import org.sca4j.spi.generator.CommandMap;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.RemoveCommandGenerator;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;

/**
 * Default implementation of the physical model generator. This implementation topologically sorts components according to their position in the
 * domain hierarchy. That is, by URI. This guarantees commands will be generated in in the proper order. As part of the topological sort, an ordered
 * set of all logical components is created. The set is then iterated and command generators called based on their command order are dispatched to for
 * each logical component.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class PhysicalModelGeneratorImpl implements PhysicalModelGenerator {
    private static final Comparator<LogicalComponent<?>> COMPARATOR = new Comparator<LogicalComponent<?>>() {
        public int compare(LogicalComponent<?> first, LogicalComponent<?> second) {
            return first.getUri().compareTo(second.getUri());
        }
    };

    private final List<CommandGenerator> addCommandGenerators;
    private final List<CommandGenerator> removeCommandGenerators;

    public PhysicalModelGeneratorImpl(@Reference(name = "addCommandGenerators")List<AddCommandGenerator> addGenerators,
                                      @Reference(name = "removeCommandGenerators")List<RemoveCommandGenerator> removeGenerators) {
        // sort the command generators
        this.addCommandGenerators = sort(addGenerators);
        this.removeCommandGenerators = sort(removeGenerators);
    }

    public CommandMap generate(Collection<LogicalComponent<?>> components) throws GenerationException {
        List<LogicalComponent<?>> sorted = topologicalSort(components);

        CommandMap commandMap = new CommandMap();
        
        for (CommandGenerator generator : addCommandGenerators) {
            for (LogicalComponent<?> component : sorted) {
                Command command = generator.generate(component);
                if (command != null) {
                    commandMap.addCommand(component.getRuntimeId(), command);
                }
            }
        }
        for (LogicalComponent<?> component : components) {
            component.setProvisioned(true);
        }
        return commandMap;
    }


    public CommandMap generate(LogicalChange change) throws GenerationException {
        Collection<LogicalComponent<?>> addedComponents = topologicalSort(change.getAddedComponents());
        Collection<LogicalComponent<?>> deletedComponents = change.getDeletedComponents();
        CommandMap commandMap = new CommandMap();
        for (CommandGenerator generator : removeCommandGenerators) {
            for (LogicalComponent<?> component : deletedComponents) {
                Command command = generator.generate(component);
                if (command != null) {
                    commandMap.addCommand(component.getRuntimeId(), command);
                }
            }
        }
        for (CommandGenerator generator : addCommandGenerators) {
            for (LogicalComponent<?> component : addedComponents) {
                Command command = generator.generate(component);
                if (command != null) {
                    commandMap.addCommand(component.getRuntimeId(), command);
                }
            }
        }
        for (LogicalComponent<?> component : addedComponents) {
            component.setProvisioned(true);
        }
        return commandMap;
    }

    /**
     * Topologically sorts components according to their URI.
     *
     * @param components the collection to sort
     * @return a sorted collection
     */
    private List<LogicalComponent<?>> topologicalSort(Collection<LogicalComponent<?>> components) {
        List<LogicalComponent<?>> sorted = new ArrayList<LogicalComponent<?>>();
        for (LogicalComponent<?> component : components) {
            sorted.add(component);
            if (component instanceof LogicalCompositeComponent) {
                flatten((LogicalCompositeComponent) component, sorted);
            }
        }
        Collections.sort(sorted, COMPARATOR);
        return sorted;
    }

    /**
     * Recursively adds composite children to the collection of components
     *
     * @param component  the composite component
     * @param components the collection
     */
    private void flatten(LogicalCompositeComponent component, List<LogicalComponent<?>> components) {
        for (LogicalComponent<?> child : component.getComponents()) {
            components.add(child);
            if (child instanceof LogicalCompositeComponent) {
                flatten((LogicalCompositeComponent) child, components);
            }
        }

    }

    private List<CommandGenerator> sort(List<? extends CommandGenerator> commandGenerators) {
        Comparator<CommandGenerator> generatorComparator = new Comparator<CommandGenerator>() {

            public int compare(CommandGenerator first, CommandGenerator second) {
                return first.getOrder() - second.getOrder();
            }
        };
        List<CommandGenerator> sorted = new ArrayList<CommandGenerator>(commandGenerators);
        Collections.sort(sorted, generatorComparator);
        return sorted;
    }


}
