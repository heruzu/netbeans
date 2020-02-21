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

package org.netbeans.modules.mercurial.remote.ui.commit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.netbeans.modules.mercurial.remote.FileInformation;
import org.netbeans.modules.mercurial.remote.FileStatusCache;
import org.netbeans.modules.mercurial.remote.HgException;
import org.netbeans.modules.mercurial.remote.HgProgressSupport;
import org.netbeans.modules.mercurial.remote.Mercurial;
import org.netbeans.modules.mercurial.remote.OutputLogger;
import org.netbeans.modules.mercurial.remote.ui.actions.ContextAction;
import org.netbeans.modules.mercurial.remote.util.HgCommand;
import org.netbeans.modules.mercurial.remote.util.HgUtils;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * Delete action enabled for new local files and added but not yet committed files.
 *
 * 
 */
public final class DeleteLocalAction extends ContextAction {

    public static final int LOCALLY_DELETABLE_MASK = FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY | FileInformation.STATUS_VERSIONED_ADDEDLOCALLY;

    @Override
    protected String getBaseName(Node[] nodes) {
        return "CTL_MenuItem_Delete"; //NOI18N
    }

    @Override
    protected boolean enable(Node[] nodes) {
        VCSContext context = HgUtils.getCurrentContext(nodes);
        return Mercurial.getInstance().getFileStatusCache().listFiles(context, LOCALLY_DELETABLE_MASK).length > 0;
    }
    
    @Override
    protected void performContextAction (final Node[] nodes) {
        final VCSContext context = HgUtils.getCurrentContext(nodes);
        FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
        final Set<VCSFileProxy> files = context.getRootFiles();
        for (VCSFileProxy f : files) {
            if ((cache.getCachedStatus(f).getStatus() & LOCALLY_DELETABLE_MASK) == 0) {
                return;
            }
        }

        NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(DeleteLocalAction.class, "CTL_DeleteLocal_Prompt")); // NOI18N
        descriptor.setTitle(NbBundle.getMessage(DeleteLocalAction.class, "CTL_DeleteLocal_Title")); // NOI18N
        descriptor.setMessageType(JOptionPane.WARNING_MESSAGE);
        descriptor.setOptionType(NotifyDescriptor.YES_NO_OPTION);

        Object res = DialogDisplayer.getDefault().notify(descriptor);
        if (res != NotifyDescriptor.YES_OPTION) {
            return;
        }
        
        HgProgressSupport support = new HgProgressSupport() {
            @Override
            public void perform() {
                final Map<VCSFileProxy, Set<VCSFileProxy>> sortedFiles = HgUtils.sortUnderRepository(files);
                try {
                    HgUtils.runWithoutIndexing(new Callable<Void>() {
                        @Override
                        public Void call () throws Exception {
                            for (Map.Entry<VCSFileProxy, Set<VCSFileProxy>> e : sortedFiles.entrySet()) {
                                try {
                                    HgCommand.doRevert(e.getKey(), new ArrayList<>(e.getValue()), null, false, OutputLogger.getLogger(null));
                                } catch (HgException ex) {
                                    Mercurial.LOG.log(Level.INFO, null, ex);
                                }
                                for (VCSFileProxy file : e.getValue()) {
                                    if(isCanceled()) {
                                        return null;
                                    }
                                    FileObject fo = file.toFileObject();
                                    if (fo != null) {
                                        FileLock lock = null;
                                        try {
                                            lock = fo.lock();                    
                                            fo.delete(lock);       
                                        } catch (IOException ex) {
                                            Mercurial.LOG.log(Level.SEVERE, NbBundle.getMessage(DeleteLocalAction.class, "MSG_Cannot_lock", file.getPath()), e); //NOI18N
                                        } finally {
                                            if (lock != null) {
                                                lock.releaseLock();
                                            }
                                        }
                                    }
                                }
                            }
                            return null;
                        }
                    }, files.toArray(new VCSFileProxy[files.size()]));
                } catch (HgException ex) {
                    HgUtils.notifyException(ex);
                }
            }
        };
        support.start(Mercurial.getInstance().getRequestProcessor(), HgUtils.getRootFile(context), NbBundle.getMessage(DeleteLocalAction.class, "LBL_DeleteLocalAction.progress")); //NOI18N
    }
    
}
