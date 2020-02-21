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
package org.netbeans.modules.mercurial.remote.ui.diff;

import java.awt.EventQueue;
import org.netbeans.modules.mercurial.remote.HgException;
import org.netbeans.modules.mercurial.remote.Mercurial;
import org.netbeans.modules.mercurial.remote.ui.actions.ContextAction;
import org.netbeans.modules.mercurial.remote.ui.log.HgLogMessage.HgRevision;
import org.netbeans.modules.mercurial.remote.util.HgCommand;
import org.netbeans.modules.mercurial.remote.util.HgUtils;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;

/**
 *
 * 
 */
@Messages({
    "CTL_MenuItem_DiffToRevision=Diff &To Revision...",
    "CTL_PopupMenuItem_DiffToRevision=Diff To Revision..."
})
public class DiffToRevisionAction extends ContextAction {
    
    @Override
    protected boolean enable(Node[] nodes) {
        VCSContext context = HgUtils.getCurrentContext(nodes);
        return HgUtils.isFromHgRepository(context);
    }

    @Override
    protected String getBaseName (Node[] nodes) {
        return "CTL_MenuItem_DiffToRevision"; //NOI18N
    }

    @Override
    protected void performContextAction (Node[] nodes) {
        final VCSContext context = HgUtils.getCurrentContext(nodes);
        final VCSFileProxy[] actionRoots = HgUtils.getActionRoots(context);
        if (actionRoots == null || actionRoots.length == 0) {
            return;
        }
        Utils.post(new Runnable() {
            @Override
            public void run () {
                final VCSFileProxy repository = Mercurial.getInstance().getRepositoryRoot(actionRoots[0]);
                try {
                    final HgRevision parent = HgCommand.getParent(repository, null, null);
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run () {
                            String contextName = VCSFileProxySupport.getContextDisplayName(context);
                            diff(repository, actionRoots, parent, contextName);
                        }
                    });
                } catch (HgException.HgCommandCanceledException ex) {
                } catch (HgException ex) {
                    HgUtils.notifyException(ex);
                }
            }
        });
    }
    
    private void diff (VCSFileProxy repository, VCSFileProxy[] roots, HgRevision wcParent, String contextDisplayName) {
        DiffToRevision diffPanel = new DiffToRevision(repository, wcParent);
        if (diffPanel.showDialog()) {
            SystemAction.get(DiffAction.class).diff(roots, diffPanel.getSelectedTreeFirst(),
                    diffPanel.getSelectedTreeSecond(), contextDisplayName, false, true);
        }
    }
}
