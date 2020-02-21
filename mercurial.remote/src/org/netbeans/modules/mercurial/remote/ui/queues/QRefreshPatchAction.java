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
package org.netbeans.modules.mercurial.remote.ui.queues;

import java.util.List;
import java.util.Set;
import org.netbeans.modules.mercurial.remote.HgException;
import org.netbeans.modules.mercurial.remote.HgModuleConfig;
import org.netbeans.modules.mercurial.remote.OutputLogger;
import org.netbeans.modules.mercurial.remote.ui.log.HgLogMessage;
import org.netbeans.modules.mercurial.remote.ui.queues.CreateRefreshAction.Cmd.CreateRefreshPatchCmd;
import org.netbeans.modules.mercurial.remote.util.HgCommand;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;

/**
 *
 * 
 */
@ActionID(id = "org.netbeans.modules.mercurial.remote.ui.queues.QRefreshPatchAction", category = "MercurialRemote/Queues")
@ActionRegistration(displayName = "#CTL_MenuItem_QRefreshPatch")
@Messages({
    "CTL_MenuItem_QRefreshPatch=&Refresh Patch...",
    "CTL_PopupMenuItem_QRefreshPatch=Refresh Patch..."
})
public class QRefreshPatchAction extends CreateRefreshAction {

    static final String KEY_CANCELED_MESSAGE = "qrefresh."; //NOI18N

    public QRefreshPatchAction () {
        super("refresh"); //NOI18N
    }

    @Override
    protected String getBaseName (Node[] nodes) {
        return "CTL_MenuItem_QRefreshPatch"; //NOI18N
    }

    @Override
    @Messages({
        "# {0} - repository name", "MSG_QRefreshPatchAction.err.noPatchApplied=Cannot refresh patch.\n"
            + "No patch applied in repository \"{0}\"."
    })
    QCommitPanel createPanel (final VCSFileProxy root, final VCSFileProxy[] roots) {
        QPatch currentPatch = null;
        try {
            for (QPatch p : HgCommand.qListSeries(root)) {
                if (p.isApplied()) {
                    currentPatch = p;
                } else {
                    break;
                }
            }
            if (currentPatch == null) {
                NotifyDescriptor.Message e = new NotifyDescriptor.Message(Bundle.MSG_QRefreshPatchAction_err_noPatchApplied(root.getName()));
                DialogDisplayer.getDefault().notifyLater(e);
            } else {
                final HgLogMessage.HgRevision parent = HgCommand.getParent(root, null, currentPatch.getId());
                String commitMessage = HgModuleConfig.getDefault(root).getLastCanceledCommitMessage(KEY_CANCELED_MESSAGE + currentPatch.getId());
                if (commitMessage.isEmpty()) {
                    commitMessage = HgModuleConfig.getDefault(root).getLastUsedQPatchMessage(currentPatch.getId());
                    if (commitMessage.isEmpty()) {
                        List<HgLogMessage> msgs = HgCommand.getParents(root, null, null);
                        if (!msgs.isEmpty()) {
                            commitMessage = msgs.get(0).getMessage();
                        }
                    }
                }
                return QCommitPanel.createRefreshPanel(roots, root, commitMessage, currentPatch, parent, QRefreshPatchAction.class.getName());
            }
        } catch (HgException.HgCommandCanceledException ex) {
            // canceled by user, do nothing
        } catch (HgException ex) {
            NotifyDescriptor.Message e = new NotifyDescriptor.Message(ex.getMessage());
            DialogDisplayer.getDefault().notifyLater(e);
        }
        return null;
    }

    @Override
    CreateRefreshPatchCmd createHgCommand (VCSFileProxy root, List<VCSFileProxy> candidates, OutputLogger logger,
            String message, String patchName, String user, String bundleKeyPostfix,
            List<VCSFileProxy> roots, Set<VCSFileProxy> excludedFiles, Set<VCSFileProxy> filesToRefresh) {
        return new CreateRefreshPatchCmd(root, candidates, logger, message, patchName, user, bundleKeyPostfix,
                roots, excludedFiles, filesToRefresh) {
            @Override
            protected void runHgCommand (VCSFileProxy repository, List<VCSFileProxy> candidates, Set<VCSFileProxy> excludedFiles,
                    String patchId, String msg, String user, OutputLogger logger) throws HgException {
                HgCommand.qRefreshPatch(repository, candidates, excludedFiles, msg, user, logger);
            }
        };
    }

    @Override
    void persistCanceledCommitMessage (VCSFileProxy root, QCreatePatchParameters parameters, String canceledCommitMessage) {
        HgModuleConfig.getDefault(root).setLastCanceledCommitMessage(KEY_CANCELED_MESSAGE + parameters.getPatch().getId(), canceledCommitMessage);
    }
    
}
