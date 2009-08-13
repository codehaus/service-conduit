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
package org.sca4j.idea;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationInfoProvider;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.JavaProgramRunner;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.sca4j.idea.run.F3JUnitCommandLineState;
import org.sca4j.idea.ui.JUnitSettingsEditor;
import org.jdom.Element;

/**
 * Encapsulates run configuration for JUnit components.
 *
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
public class F3JUnitRunConfiguration extends RunConfigurationBase {
    private static final String NAME = "name";
    private static final String MODULE = "module";
    private static final String JUNIT_CLASS = "junit_class";
    private static final String COMPOSITE = "composite";
    private static final String NAMESPACE = "namespace";
    private Module currentModule;
    private String moduleName;
    private String junitClass;
    private String compositeNamespace;
    private String compositeName;

    public F3JUnitRunConfiguration(final Project project, final ConfigurationFactory factory, final String name) {
        super(project, factory, name);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
        currentModule = ModuleManager.getInstance(getProject()).findModuleByName(moduleName);
    }

    public String getJUnitClass() {
        return junitClass;
    }

    public void setJUnitClass(String junitClass) {
        this.junitClass = junitClass;
    }

    public String getCompositeNamespace() {
        return compositeNamespace;
    }

    public void setCompositeNamespace(String compositeNamespace) {
        this.compositeNamespace = compositeNamespace;
    }

    public String getCompositeName() {
        return compositeName;
    }

    public void setCompositeName(String compositeName) {
        this.compositeName = compositeName;
    }

    public SettingsEditor<F3JUnitRunConfiguration> getConfigurationEditor() {
        return new JUnitSettingsEditor();
    }

    public JDOMExternalizable createRunnerSettings(ConfigurationInfoProvider provider) {
        return null;
    }

    public SettingsEditor<JDOMExternalizable> getRunnerSettingsEditor(JavaProgramRunner runner) {
        return null;
    }

    public RunProfileState getState(DataContext context,
                                    RunnerInfo runnerInfo,
                                    RunnerSettings runnerSettings,
                                    ConfigurationPerRunnerSettings configurationSettings) throws ExecutionException {
        if (getModule() == null) {
            throw new ExecutionException("run.configuration.no.module.specified");
        }
        QName composite;
        try {
            composite = new QName(compositeNamespace, compositeName);
        } catch (IllegalArgumentException e) {
            throw new InvalidCompositeNameException("Invalid composite name", e);
        }
        JavaCommandLineState state =
            new F3JUnitCommandLineState(getModule(), junitClass, composite, runnerSettings, configurationSettings);
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));
        state.setModulesToCompile(new Module[]{getModule()});
        return state;
    }

    public void checkConfiguration() throws RuntimeConfigurationException {
        if (getModule() == null) {
            throw new RuntimeConfigurationException("run.configuration.no.module.specified");
        }
        String moduleName = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            public String compute() {
                return getModule().getName();
            }
        });
        if (ModuleManager.getInstance(getProject()).findModuleByName(moduleName) == null) {
            throw new RuntimeConfigurationException("run.configuration.no.module.specified");
        }
        final ModuleRootManager rootManager = ModuleRootManager.getInstance(getModule());
        final ProjectJdk jdk = rootManager.getJdk();
        if (jdk == null) {
            throw new RuntimeConfigurationException("jdk.no.specified", moduleName);
        }
    }


    public Module[] getModules() {
        List<Module> modules = new ArrayList<Module>();
        Module[] allModules = ModuleManager.getInstance(getProject()).getModules();
        for (Module module : allModules) {
            //if (module.getModuleType() == PluginModuleType.getInstance()) {
            modules.add(module);
            //}
        }
        return modules.toArray(new Module[modules.size()]);
    }

    public void readExternal(Element element) throws InvalidDataException {
        Element module = element.getChild(MODULE);
        if (module != null) {
            moduleName = module.getAttributeValue(NAME);
        }

        Element junit = element.getChild(JUNIT_CLASS);
        if (junit != null) {
            junitClass = junit.getAttributeValue(NAME);
        }

        Element namespace = element.getChild(NAMESPACE);
        if (namespace != null && namespace.getAttributeValue(NAME).length() > 0) {
            compositeNamespace = namespace.getAttributeValue(NAME);
        }

        Element composite = element.getChild(COMPOSITE);
        if (composite != null) {
            compositeName = composite.getAttributeValue(NAME);
        }

        DefaultJDOMExternalizer.readExternal(this, element);
        super.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        Element moduleElement = new Element(MODULE);
        moduleElement.setAttribute(NAME, ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            public String compute() {
                return getModuleName() != null ? getModuleName() : "";
            }
        }));
        element.addContent(moduleElement);


        Element junitElement = new Element(JUNIT_CLASS);
        junitElement.setAttribute(NAME, ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            public String compute() {
                return getJUnitClass() != null ? getJUnitClass() : "";
            }
        }));
        element.addContent(junitElement);

        Element namespaceElement = new Element(NAMESPACE);
        namespaceElement.setAttribute(NAME, ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            public String compute() {
                return getCompositeNamespace() != null ? getCompositeNamespace() : "";
            }
        }));
        element.addContent(namespaceElement);

        Element compositeElement = new Element(COMPOSITE);
        compositeElement.setAttribute(NAME, ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            public String compute() {
                return getCompositeName() != null ? getCompositeName() : "";
            }
        }));
        element.addContent(compositeElement);

        DefaultJDOMExternalizer.writeExternal(this, element);
        super.writeExternal(element);
    }

    public Module getModule() {
        if (currentModule != null && currentModule.isDisposed()) {
            moduleName = currentModule.getName();
            currentModule = null;
        }
        if (currentModule == null && moduleName != null) {
            currentModule = ModuleManager.getInstance(getProject()).findModuleByName(moduleName);
        }
        return currentModule;
    }

    public void setModule(Module module) {
        currentModule = module;
    }

}
