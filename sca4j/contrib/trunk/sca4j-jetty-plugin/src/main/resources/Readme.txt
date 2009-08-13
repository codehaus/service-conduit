Fabric3 Jetty Plugin

1. Problem
   Since Fabric3 runtime expects the extensions and boot related classes to be in a location other than WEB-INF/lib, It was not possible to run the Web Components using jetty:run, however,
   it was possible to run them by jetty:run-war. Run-war goal creates a WAR file and then deploys it onto the Jetty Server making it possible to run. During this, any changes to your
   web pages forces a rebuild and redployment of the WAR onto the Jetty Server, which in process increases the development/debugging time. To solve this, we have extended the existing Jetty Run Mojo
   and overriden the execute() and getClassPath() method, wherein we add the required boot and extensions onto the classpath in the way Fabric3 runtime expects them to be.

2. Usage

   In the pom file of your web component, add the following to the <build> tag.

                    <plugin>
	                <groupId>org.codehaus.fabric3</groupId>
	                <artifactId>fabric3-jetty-plugin</artifactId>
	                <version>{pluginVersion}</version>
	                <executions>
	                    <execution>
	                        <goals>
	                            <goal>run</goal>
	                        </goals>
	                    </execution>
	                </executions>
	                <configuration>
	                    <runTimeVersion>{fabric3RuntimeVersion}</runTimeVersion>
	                    <extensions>
	                    </extensions>
	                </configuration>
	                </plugin>

   To run your web component, use the fabric3-jetty:run command, where fabric3-jetty is the name of the plugin and run is the goal. This command will add org.codehaus.fabric3:fabric3-webapp-host to the
   dependency to {webappSourceDirectory}/WEB-INF/fabric3/boot and any extensions that you may have configured to the {webappSourceDirectory}/WEB-INF/fabric3/extensions directory.

   To clean up the Libraries added to the Webapp directory you can use the fabric3-jetty:clean command. Since fabric3-jetty:run from the trunk of your web component, use this comand only if you donot have
   these folders(boot, extensions) in your source tree, otherwise it may clean your source files as well.