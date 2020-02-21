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
package org.netbeans.modules.mercurial.remote.ui.rollback;

import java.awt.event.ActionEvent;
import org.netbeans.modules.versioning.core.spi.VCSContext;

import java.util.List;
import org.netbeans.modules.mercurial.remote.HgException;
import org.netbeans.modules.mercurial.remote.HgProgressSupport;
import org.netbeans.modules.mercurial.remote.Mercurial;
import org.netbeans.modules.mercurial.remote.OutputLogger;
import org.netbeans.modules.mercurial.remote.util.HgCommand;
import org.netbeans.modules.mercurial.remote.util.HgUtils;
import org.netbeans.modules.mercurial.remote.ui.actions.ContextAction;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.nodes.Node;

/**
 * Pull action for mercurial: 
 * hg pull - pull changes from the specified source
 * 
 * 
 */
public class VerifyAction extends ContextAction {
    
    @Override
    protected boolean enable(Node[] nodes) {
        return HgUtils.isFromHgRepository(HgUtils.getCurrentContext(nodes));
    }

    @Override
    protected String getBaseName(Node[] nodes) {
        return "CTL_MenuItem_Verify";                                   //NOI18N
    }

    @Override
    protected void performContextAction(Node[] nodes) {
        VCSContext context = HgUtils.getCurrentContext(nodes);
        verify(context);
    }
    
    public static void verify(final VCSContext ctx){
        final VCSFileProxy roots[] = HgUtils.getActionRoots(ctx);
        if (roots == null || roots.length == 0) {
            return;
        }
        final VCSFileProxy root = Mercurial.getInstance().getRepositoryRoot(roots[0]);
         
        RequestProcessor rp = Mercurial.getInstance().getRequestProcessor(root);
        HgProgressSupport support = new HgProgressSupport() {
            @Override
            public void perform() {
                
                OutputLogger logger = getLogger();
                try {
                    logger.outputInRed(
                                NbBundle.getMessage(VerifyAction.class,
                                "MSG_VERIFY_TITLE")); // NOI18N
                    logger.outputInRed(
                                NbBundle.getMessage(VerifyAction.class,
                                "MSG_VERIFY_TITLE_SEP")); // NOI18N
                    logger.output(
                                NbBundle.getMessage(VerifyAction.class,
                                "MSG_VERIFY_INFO", root.getPath())); // NOI18N
                    List<String> list = HgCommand.doVerify(root, logger);
                    
                    if(list != null && !list.isEmpty()){                      
                        logger.output(list);                        
                    }
                } catch (HgException.HgCommandCanceledException ex) {
                    // canceled by user, do nothing
                } catch (HgException ex) {
                    HgUtils.notifyException(ex);
                } finally {
                    logger.outputInRed(
                                NbBundle.getMessage(VerifyAction.class,
                                "MSG_VERIFY_DONE")); // NOI18N
                    logger.output(""); // NOI18N
                    logger.getOpenOutputAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "")); //NOI18N
                }
            }
        };
        support.start(rp, root, org.openide.util.NbBundle.getMessage(VerifyAction.class, "MSG_VERIFY_PROGRESS")); // NOI18N
    }
}
