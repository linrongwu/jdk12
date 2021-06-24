/*
 * Copyright (c) 2001, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package nsk.jdi.ClassPrepareRequest.addClassExclusionFilter;

import nsk.share.*;
import nsk.share.jpda.*;
import nsk.share.jdi.*;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import java.util.*;
import java.io.*;

/**
 * The test for the implementation of an object of the type
 * ClassPrepareRequest.
 *
 * The test checks that results of the method
 * <code>com.sun.jdi.ClassPrepareRequest.addClassExclusionFilter(String)</code>
 * complies with its spec.
 *
 * The test checks up on the following assertion:
 *    Restricts the events generated by this request to those
 *    whose location is in a class
 *    whose name matches a restricted regular expression.
 * The case to check includes a pattern that ends with '*'.
 *
 * The test works as follows.
 * - The debugger
 *   - sets up a ClassPrepareRequest,
 *   - using a string pattern, restricts it with addClassExclusionFilter,
 *   - resumes the debuggee, and waits for ClassPrepareEvents.
 * - The debuggee creates and starts a thread1
 *   whose 'run' method creates objects of ClassTypes needed
 *   to generate Events and to test the filters.
 * - Upon getting the events, the debugger performs the checks required.
 */

public class filter002 extends TestDebuggerType1 {

    public static void main (String argv[]) {
        System.exit(run(argv, System.out) + Consts.JCK_STATUS_BASE);
    }

    public static int run (String argv[], PrintStream out) {
        debuggeeName = "nsk.jdi.ClassPrepareRequest.addClassExclusionFilter.filter002a";
        return new filter002().runThis(argv, out);
    }

    private String classExclFilter1 = "nsk.jdi.ClassPrepareRequest.addClassExclusionFilter.Thread1filter002a.TestClass11*";
    private String classExclName1 = "nsk.jdi.ClassPrepareRequest.addClassExclusionFilter.Thread1filter002a.TestClass11";

    protected void testRun() {

        EventRequest eventRequest1 = null;
        String property1 = "ClassPrepareRequest1";
        ThreadReference thread1 = null;

        Event newEvent = null;

        for (int i = 0; ; i++) {

            if (!shouldRunAfterBreakpoint()) {
                vm.resume();
                break;
            }

            display(":::::: case: # " + i);

            switch (i) {

                case 0:
                eventRequest1 = setting23ClassPrepareRequest(classExclFilter1,
                                                             EventRequest.SUSPEND_NONE,
                                                             property1);
                eventRequest1.enable();
                eventHandler.addListener(
                     new EventHandler.EventListener() {
                         public boolean eventReceived(Event event) {
                            if (event instanceof ClassPrepareEvent) {
                                String str = ((ClassPrepareEvent)event).referenceType().name();
                                if (str.endsWith(classExclName1)) {
                                    setFailedStatus("Received ClassPrepareEvent for excluded class:" + str);
                                } else {
                                    display("Received expected ClassPrepareEvent for " + str);
                                }
                                return true;
                            }
                            return false;
                         }
                     }
                );

                display("......waiting for ClassPrepareEvent");
                vm.resume();
                break;

                default:
                throw new Failure("** default case 1 **");
            }
        }
        return;
    }

    private ClassPrepareRequest setting23ClassPrepareRequest ( String classExclFilter,
                                                               int    suspendPolicy,
                                                               String property       )
            throws Failure {
        try {
            display("......setting up ClassPrepareRequest:");
            display("       class exclude filter: " + classExclFilter + "; property: " + property);

            ClassPrepareRequest
            cpr = eventRManager.createClassPrepareRequest();
            cpr.putProperty("number", property);
            cpr.setSuspendPolicy(suspendPolicy);

            cpr.addClassExclusionFilter(classExclFilter);

            display("      ClassPrepareRequest has been set up");
            return cpr;
        } catch ( Exception e ) {
            throw new Failure("** FAILURE to set up ClassPrepareRequest **");
        }
    }
}
