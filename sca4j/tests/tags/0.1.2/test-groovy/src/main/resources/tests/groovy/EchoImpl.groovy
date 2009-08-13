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

package tests.groovy
import org.osoa.sca.annotations.Property
import org.osoa.sca.annotations.Reference
import org.osoa.sca.annotations.Service

@Service(EchoService.class)
class EchoImpl implements EchoService {

    @Property public String message
    @Reference public EchoService java

    public String hello(String name) {
        return message + " " + java.hello(name)
    }
}