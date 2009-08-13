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
package org.sca4j.fabric.generator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.generator.classloader.ClassLoaderCommandGenerator;
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
    private ClassLoaderCommandGenerator classLoaderCommandGenerator;

    public PhysicalModelGeneratorImpl(@Reference(name = "addCommandGenerators")List<AddCommandGenerator> addGenerators,
                                      @Reference(name = "removeCommandGenerators")List<RemoveCommandGenerator> removeGenerators,
                                      @Reference ClassLoaderCommandGenerator classLoaderCommandGenerator) {
        this.classLoaderCommandGenerator = classLoaderCommandGenerator;
        // sort the command generators
        this.addCommandGenerators = sort(addGenerators);
        this.removeCommandGenerators = sort(removeGenerators);
    }

    public CommandMap generate(Collection<LogicalComponent<?>> components) throws GenerationException {
        List<LogicalComponent<?>> sorted = topologicalSort(components);

        CommandMap commandMap = new CommandMap();
        Map<URI, Set<Command>> commandsPerRuntime = classLoaderCommandGenerator.generate(sorted);
        for (Map.Entry<URI, Set<Command>> entry : commandsPerRuntime.entrySet()) {
            for (Command command : entry.getValue()) {
                commandMap.addCommand(entry.getKey(), command);
            }
        }
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
