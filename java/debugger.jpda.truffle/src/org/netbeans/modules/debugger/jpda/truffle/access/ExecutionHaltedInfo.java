/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.modules.debugger.jpda.truffle.access;

import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.ObjectVariable;

/**
 * Halted information from the backend <code>JPDATruffleAccessor.executionHalted()</code>.
 */
final class ExecutionHaltedInfo {
    
    final ObjectVariable debugManager;
    final ObjectVariable sourcePositions;
    final boolean haltedBefore;
    final ObjectVariable returnValue;
    final ObjectVariable frameInfo;
    final ObjectVariable[] breakpointsHit;
    final ObjectVariable[] breakpointConditionExceptions;
    final LocalVariable stepCmd;
    
    private ExecutionHaltedInfo(LocalVariable[] vars) {
        this.debugManager = (ObjectVariable) vars[0];
        this.sourcePositions = (ObjectVariable) vars[1];
        this.haltedBefore = (Boolean) vars[2].createMirrorObject();
        this.returnValue = (ObjectVariable) vars[3];
        this.frameInfo = (ObjectVariable) vars[4];
        this.breakpointsHit = getObjectArray((ObjectVariable) vars[5]);
        this.breakpointConditionExceptions = getObjectArray((ObjectVariable) vars[6]);
        this.stepCmd = vars[7];
    }
    
    static ExecutionHaltedInfo get(LocalVariable[] vars) {
        return new ExecutionHaltedInfo(vars);
    }
    
    private static ObjectVariable[] getObjectArray(ObjectVariable var) {
        Field[] fields = var.getFields(0, Integer.MAX_VALUE);
        int n = fields.length;
        ObjectVariable[] arr = new ObjectVariable[n];
        for (int i = 0; i < n; i++) {
            arr[i] = (ObjectVariable) fields[i];
        }
        return arr;
    }
}
