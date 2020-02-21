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
package org.netbeans.modules.cnd.remote.mapper;

import org.netbeans.modules.cnd.api.utils.PlatformInfo;

/**
 *
 */
/*package*/final class HostMappingProviderLinux extends HostMappingProviderUnixAbstract {

    public boolean isApplicable(PlatformInfo hostPlatform, PlatformInfo otherPlatform) {
        return hostPlatform.isLinux() && otherPlatform.isUnix();
    }

    @Override
    protected String getShareCommand() {
        return "cat /etc/exports"; // NOI18N
    }

    @Override
    protected String fetchPath(String[] values) {
        return values.length > 0 ? values[0] : null;
    }
}
