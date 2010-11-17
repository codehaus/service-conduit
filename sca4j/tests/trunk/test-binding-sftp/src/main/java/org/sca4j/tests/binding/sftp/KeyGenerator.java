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
 */
package org.sca4j.tests.binding.sftp;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

/**
 * Utility class to generate private and public key pairs
 * 
 * @author dhillonn
 */
public class KeyGenerator {

    public static void main(String[] args) throws FileNotFoundException, IOException, JSchException {
        final String fileName = prompt(new JTextField(20), "Enter file name");
        final String passphrase = prompt(new JPasswordField(20), "Enter passphrase");
        KeyPair kpair = KeyPair.genKeyPair(new JSch(), KeyPair.RSA);
        kpair.setPassphrase(passphrase);
        kpair.writePrivateKey(fileName + ".pem");
        kpair.writePublicKey(fileName + ".pub", "sca4j sftp test key");
        System.out.println("Finger print: " + kpair.getFingerPrint());
        kpair.dispose();
    }

    public static String prompt(JTextField inputField, String prompt) {
        int result = JOptionPane.showConfirmDialog(null, new Object[] { inputField }, prompt, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return inputField.getText();
        }
        throw new AssertionError("Nothing selected");
    }

}
