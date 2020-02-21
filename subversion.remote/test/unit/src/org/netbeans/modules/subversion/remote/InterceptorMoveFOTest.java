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
package org.netbeans.modules.subversion.remote;

import java.util.Collections;
import junit.framework.Test;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.test.ClassForAllEnvironments;
import static org.netbeans.modules.subversion.remote.RemoteVersioningTestBase.addTest;
import org.netbeans.modules.subversion.remote.api.SVNStatusKind;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;

/**
 *
 * 
 */
@ClassForAllEnvironments(section = "remote.svn")
public class InterceptorMoveFOTest extends RemoteVersioningTestBase {

    public InterceptorMoveFOTest(String testName, ExecutionEnvironment execEnv) {
        super(testName, execEnv);
    }
    
    public static Test suite() {
        NbTestSuite suite = new NbTestSuite();
        addTest(suite, InterceptorMoveFOTest.class, "moveVersionedFile_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveUnversionedFile_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveUnversionedFolder_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveAddedFile2UnversionedFolder_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveVersionedFile2IgnoredFolder_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveAddedFile2VersionedFolder_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveA2B2A_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveA2B2C_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveA2B2C2A_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveA2B_CreateA_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveVersionedFolder_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveFileTree_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveVersionedFile2Repos_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveVersionedFolder2Repos_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveFileTree2Repos_FO");
        addTest(suite, InterceptorMoveFOTest.class, "moveA2CB2A_FO");
        addTest(suite, InterceptorMoveFOTest.class, "deleteA_moveB2A2B_FO");
        addTest(suite, InterceptorMoveFOTest.class, "deleteA_moveUnversioned2A_FO");
        
        return(suite);
    }
    
    public void moveA2CB2A_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fileA = VCSFileProxy.createFileProxy(wc, "A");
        VCSFileProxySupport.createNew(fileA);
        VCSFileProxy folderB = VCSFileProxy.createFileProxy(wc, "folderB");
        VCSFileProxySupport.mkdirs(folderB);
        VCSFileProxy fileB = VCSFileProxy.createFileProxy(folderB, fileA.getName());
        VCSFileProxySupport.createNew(fileB);
        VCSFileProxy folderC = VCSFileProxy.createFileProxy(wc, "folderC");
        VCSFileProxySupport.mkdirs(folderC);
        commit(wc);

        VCSFileProxy fileC = VCSFileProxy.createFileProxy(folderC, fileA.getName());

        // move
        moveFO(fileA, fileC);
        Thread.sleep(500);
        moveFO(fileB, fileA);

        // test
        assertTrue(fileA.exists());
        assertTrue(fileC.exists());
        assertFalse(fileB.exists());

        assertEquals(SVNStatusKind.REPLACED, getSVNStatus(fileA).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fileB).getTextStatus());
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(fileC).getTextStatus());

        assertEquals(FileInformation.STATUS_VERSIONED_ADDEDLOCALLY, getStatus(fileA));
        assertEquals(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, getStatus(fileB));
        assertEquals(FileInformation.STATUS_VERSIONED_ADDEDLOCALLY, getStatus(fileC));

        commit(wc);

        // test
        assertTrue(fileA.exists());
        assertTrue(fileC.exists());
        assertFalse(fileB.exists());

        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(fileA).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fileB).getTextStatus());
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(fileC).getTextStatus());

        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(fileA));
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fileB));
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(fileC));
    }

    public void moveVersionedFile_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFile = VCSFileProxy.createFileProxy(wc, "file");
        VCSFileProxySupport.createNew(fromFile);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(wc, "toFolder");
        VCSFileProxySupport.mkdirs(toFolder);
        commit(wc);               
        VCSFileProxy toFile = VCSFileProxy.createFileProxy(toFolder, fromFile.getName());
        
        // move
        moveFO(fromFile, toFile);
        
        // test 
        assertFalse(fromFile.exists());
        assertTrue(toFile.exists());
        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile).getTextStatus());        
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(toFile).getTextStatus());        
        
        assertCachedStatus(fromFile, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(toFile, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);   
        
//        commit(wc);
    }
    
    public void moveUnversionedFile_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFile = VCSFileProxy.createFileProxy(wc, "file");
        VCSFileProxySupport.createNew(fromFile);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(wc, "toFolder");
        VCSFileProxySupport.mkdirs(toFolder);
        
        VCSFileProxy toFile = VCSFileProxy.createFileProxy(toFolder, fromFile.getName());
        
        // rename
        moveFO(fromFile, toFile);
        
        // test 
        assertFalse(fromFile.exists());
        assertTrue(toFile.exists());
        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fromFile).getTextStatus());        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile).getTextStatus());        
              
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fromFile));                
        assertCachedStatus(toFile, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);                
        
//        commit(wc);
    }

    public void moveUnversionedFolder_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFolder = VCSFileProxy.createFileProxy(wc, "folder");
        VCSFileProxySupport.mkdirs(fromFolder);
        VCSFileProxy toParent = VCSFileProxy.createFileProxy(wc, "toFolder");
        VCSFileProxySupport.mkdirs(toParent);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(toParent, fromFolder.getName());
        
        
        // move        
        moveFO(fromFolder, toFolder);
        
        // test 
        assertFalse(fromFolder.exists());
        assertTrue(toFolder.exists());

        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fromFolder).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFolder).getTextStatus());

        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fromFolder));
        assertCachedStatus(toFolder, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);

//        commit(wc);
    }
    
    public void moveAddedFile2UnversionedFolder_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init        
        VCSFileProxy fromFile = VCSFileProxy.createFileProxy(wc, "file");
        VCSFileProxySupport.createNew(fromFile);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(wc, "toFodler");
        VCSFileProxySupport.mkdirs(toFolder);
        
        VCSFileProxy toFile = VCSFileProxy.createFileProxy(toFolder, fromFile.getName());
        
        // add
        getClient().addFile(fromFile);                
        
        // move
        moveFO(fromFile, toFile);
                
        // test 
        assertFalse(fromFile.exists());
        assertTrue(toFile.exists());
        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fromFile).getTextStatus());        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile).getTextStatus());        
        
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fromFile));                
        assertCachedStatus(toFile, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);                
        
//        commit(wc);
    }
    
    public void moveVersionedFile2IgnoredFolder_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFile = VCSFileProxy.createFileProxy(wc, "file");
        VCSFileProxySupport.createNew(fromFile);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(wc, "toFolder");
        VCSFileProxySupport.mkdirs(toFolder);
        
        VCSFileProxy toFile = VCSFileProxy.createFileProxy(toFolder, fromFile.getName());

        // add
        commit(fromFile);
        getClient().setIgnoredPatterns(wc, Collections.singletonList(toFolder.getName()));

        // move
        moveFO(fromFile, toFile);

        // test
        assertFalse(fromFile.exists());
        assertTrue(toFile.exists());

        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile).getTextStatus());
        if (version.compareTo(new Version(1,7,0)) >= 0)
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile).getTextStatus());
        assertEquals(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, getStatus(fromFile));
        if (version.compareTo(new Version(1,7,0)) >= 0)
        assertCachedStatus(toFile, FileInformation.STATUS_NOTVERSIONED_EXCLUDED);
    }
       
    public void moveAddedFile2VersionedFolder_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init        
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(wc, "toFodler");
        VCSFileProxySupport.mkdirs(toFolder);
        commit(wc);
        VCSFileProxy fromFile = VCSFileProxy.createFileProxy(wc, "fromFile");
        VCSFileProxySupport.createNew(fromFile);        
        
        VCSFileProxy toFile = VCSFileProxy.createFileProxy(toFolder, fromFile.getName());  
        
        // add
        getClient().addFile(fromFile);                
        
        // rename
        moveFO(fromFile, toFile);
        
        // test 
        assertFalse(fromFile.exists());
        assertTrue(toFile.exists());
        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fromFile).getTextStatus());        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile).getTextStatus());        
        
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fromFile));                
        assertCachedStatus(toFile, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);                
        
//        commit(wc);
    }

    public void moveA2B2A_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fileA = VCSFileProxy.createFileProxy(wc, "A");
        assertFalse(fileA.exists());
        VCSFileProxySupport.createNew(fileA);
        VCSFileProxy folder = VCSFileProxy.createFileProxy(wc, "folder");
        assertFalse(folder.exists());
        VCSFileProxySupport.mkdirs(folder);        
        commit(wc);  
        
        VCSFileProxy fileB = VCSFileProxy.createFileProxy(folder, fileA.getName());
        assertFalse(fileB.exists());
        
        // move
        moveFO(fileA, fileB);
        Thread.sleep(500);
        moveFO(fileB, fileA);
        
        // test 
        assertTrue(fileA.exists());
        assertFalse(fileB.exists());
        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(fileA).getTextStatus());        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fileB).getTextStatus());        
              
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(fileA));                
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fileB));                
        
//        commit(wc);
    }

    public void moveA2B2C_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fileA = VCSFileProxy.createFileProxy(wc, "A");
        VCSFileProxySupport.createNew(fileA);
        VCSFileProxy folderB = VCSFileProxy.createFileProxy(wc, "folderB");
        VCSFileProxySupport.mkdirs(folderB);
        VCSFileProxy folderC = VCSFileProxy.createFileProxy(wc, "folderC");
        VCSFileProxySupport.mkdirs(folderC);
        commit(wc);  
        
        VCSFileProxy fileB = VCSFileProxy.createFileProxy(folderB, fileA.getName());
        VCSFileProxy fileC = VCSFileProxy.createFileProxy(folderC, fileA.getName());
        
        // move
        moveFO(fileA, fileB);
        moveFO(fileB, fileC);
        
        // test 
        assertFalse(fileA.exists());
        assertFalse(fileB.exists());
        assertTrue(fileC.exists());
        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fileA).getTextStatus());        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fileB).getTextStatus());        
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(fileC).getTextStatus());
        
        assertEquals(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, getStatus(fileA));                
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fileB));                
        assertCachedStatus(fileC, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);
        
//        commit(wc);
    }

    public void moveA2B2C2A_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fileA = VCSFileProxy.createFileProxy(wc, "A");
        VCSFileProxySupport.createNew(fileA);
        VCSFileProxy folderB = VCSFileProxy.createFileProxy(wc, "folderB");
        VCSFileProxySupport.mkdirs(folderB);
        VCSFileProxy folderC = VCSFileProxy.createFileProxy(wc, "folderC");
        VCSFileProxySupport.mkdirs(folderC);
        commit(wc);  
        
        VCSFileProxy fileB = VCSFileProxy.createFileProxy(folderB, fileA.getName());
        VCSFileProxy fileC = VCSFileProxy.createFileProxy(folderC, fileA.getName());
        
        // move
        moveFO(fileA, fileB);
        moveFO(fileB, fileC);
        moveFO(fileC, fileA);
        
        // test 
        assertTrue(fileA.exists());
        assertFalse(fileB.exists());
        assertFalse(fileC.exists());
        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(fileA).getTextStatus());        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fileB).getTextStatus());        
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(fileC).getTextStatus());  
        
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(fileA));                
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fileB));                
        assertEquals(FileInformation.STATUS_UNKNOWN, getStatus(fileC));                
        
//        commit(wc);
        
    }        
    
    public void moveA2B_CreateA_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fileA = VCSFileProxy.createFileProxy(wc, "file");
        VCSFileProxySupport.createNew(fileA);
        VCSFileProxy folderB = VCSFileProxy.createFileProxy(wc, "folderB");
        VCSFileProxySupport.mkdirs(folderB);
        commit(wc);  
        
        VCSFileProxy fileB = VCSFileProxy.createFileProxy(folderB, fileA.getName());
        
        // move
        moveFO(fileA, fileB);
        Thread.sleep(500);
        
        // create from file
        fileA.getParentFile().toFileObject().createData(fileA.getName());
        
        // test 
        assertTrue(fileB.exists());
        assertTrue(fileA.exists());
        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(fileA).getTextStatus());
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(fileB).getTextStatus());
        
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(fileA));                
        assertCachedStatus(fileB, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);                
        
//        commit(wc);
    }
    
    public void moveVersionedFolder_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFolder = VCSFileProxy.createFileProxy(wc, "from");
        VCSFileProxySupport.mkdirs(fromFolder);
        VCSFileProxy toParent = VCSFileProxy.createFileProxy(wc, "toFolder");
        VCSFileProxySupport.mkdirs(toParent);
        commit(wc);               
        
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(toParent, fromFolder.getName());
        
        // move
        moveFO(fromFolder, toFolder);
        
        // test 
        assertFalse(fromFolder.exists());
        assertTrue(toFolder.exists());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder).getTextStatus());        
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(toFolder).getTextStatus());        
        assertCachedStatus(fromFolder, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(toFolder, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);                
        commit(wc);
        assertFalse(fromFolder.exists()); 
    }    
    
    public void moveFileTree_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFolder = VCSFileProxy.createFileProxy(wc, "from");
        VCSFileProxySupport.mkdirs(fromFolder);
        VCSFileProxy fromFolder1 = VCSFileProxy.createFileProxy(fromFolder, "folder1");
        VCSFileProxySupport.mkdirs(fromFolder1);
        VCSFileProxy fromFolder2 = VCSFileProxy.createFileProxy(fromFolder, "folder2");
        VCSFileProxySupport.mkdirs(fromFolder2);
        VCSFileProxy fromFile11 = VCSFileProxy.createFileProxy(fromFolder1, "file11");
        VCSFileProxySupport.createNew(fromFile11);
        VCSFileProxy fromFile12 = VCSFileProxy.createFileProxy(fromFolder1, "file12");
        VCSFileProxySupport.createNew(fromFile12);
        VCSFileProxy fromFile21 = VCSFileProxy.createFileProxy(fromFolder2, "file21");
        VCSFileProxySupport.createNew(fromFile21);
        VCSFileProxy fromFile22 = VCSFileProxy.createFileProxy(fromFolder2, "file22");
        VCSFileProxySupport.createNew(fromFile22);
        
        VCSFileProxy toFolderParent = VCSFileProxy.createFileProxy(wc, "to");
        VCSFileProxySupport.mkdirs(toFolderParent);
        
        commit(wc);               
        
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(toFolderParent, fromFolder.getName());
        
        // move
        moveFO(fromFolder, toFolder);
                                                
        // test         t.
        assertFalse(fromFolder.exists());
        assertTrue(toFolder.exists());
        VCSFileProxy toFolder1 = VCSFileProxy.createFileProxy(toFolder, fromFolder1.getName());
        assertTrue(toFolder1.exists());
        VCSFileProxy toFolder2 = VCSFileProxy.createFileProxy(toFolder, fromFolder2.getName());
        assertTrue(toFolder2.exists());
        VCSFileProxy toFile11 = VCSFileProxy.createFileProxy(toFolder1, "file11");
        assertTrue(toFile11.exists());
        VCSFileProxy toFile12 = VCSFileProxy.createFileProxy(toFolder1, "file12");
        assertTrue(toFile12.exists());
        VCSFileProxy toFile21 = VCSFileProxy.createFileProxy(toFolder2, "file21");
        assertTrue(toFile21.exists());
        VCSFileProxy toFile22 = VCSFileProxy.createFileProxy(toFolder2, "file22");
        assertTrue(toFile22.exists());
        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder).getTextStatus());        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder1).getTextStatus());        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder2).getTextStatus());        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile11).getTextStatus());        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile12).getTextStatus());        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile21).getTextStatus());        
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile22).getTextStatus());        
        
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(toFolder).getTextStatus());        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(toFolder1).getTextStatus());        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(toFolder2).getTextStatus());        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(toFile11).getTextStatus());        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(toFile12).getTextStatus());        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(toFile21).getTextStatus());        
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(toFile22).getTextStatus());    
        
        assertCachedStatus(fromFolder, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(fromFolder1, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(fromFolder2, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(fromFile11, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(fromFile12, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(fromFile21, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);                
        assertCachedStatus(fromFile22, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);     
        
        assertCachedStatus(toFolder, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);                
        assertCachedStatus(toFolder1, FileInformation.STATUS_VERSIONED_UPTODATE);                
        assertCachedStatus(toFolder2, FileInformation.STATUS_VERSIONED_UPTODATE);                
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(toFile11));                
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(toFile12));                
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(toFile21));                
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(toFile22));                
        
        commit(wc);
        assertFalse(fromFolder.exists());
    }

    public void deleteA_moveB2A2B_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy folderA = VCSFileProxy.createFileProxy(wc, "folderA");
        VCSFileProxySupport.mkdir(folderA);
        VCSFileProxy fileA = VCSFileProxy.createFileProxy(folderA, "f");
        VCSFileProxySupport.createNew(fileA);
        VCSFileProxy folderB = VCSFileProxy.createFileProxy(wc, "folderB");
        VCSFileProxySupport.mkdir(folderB);
        VCSFileProxy fileB = VCSFileProxy.createFileProxy(folderB, "f");
        VCSFileProxySupport.createNew(fileB);
        commit(wc);
        
        //delete
        delete(fileA);
        assertFalse(fileA.exists());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fileA).getTextStatus());
        assertEquals(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, getStatus(fileA));
        // move
        moveFO(fileB, fileA);
        assertTrue(fileA.exists());
        assertFalse(fileB.exists());
        assertEquals(SVNStatusKind.REPLACED, getSVNStatus(fileA).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fileB).getTextStatus());
        assertEquals(FileInformation.STATUS_VERSIONED_ADDEDLOCALLY, getStatus(fileA));
        assertEquals(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, getStatus(fileB));
        // move second
        moveFO(fileA, fileB);
        assertFalse(fileA.exists());
        assertTrue(fileB.exists());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fileA).getTextStatus());
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(fileB).getTextStatus());
        assertEquals(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, getStatus(fileA));
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(fileB));
    }
    
    public void deleteA_moveUnversioned2A_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy folderA = VCSFileProxy.createFileProxy(wc, "folderA");
        VCSFileProxySupport.mkdir(folderA);
        VCSFileProxy fileA = VCSFileProxy.createFileProxy(folderA, "f");
        VCSFileProxySupport.createNew(fileA);
        commit(wc);
        
        VCSFileProxy folderB = VCSFileProxy.createFileProxy(wc, "folderB");
        VCSFileProxySupport.mkdir(folderB);
        VCSFileProxy fileUnversioned = VCSFileProxy.createFileProxy(folderB, "f");
        VCSFileProxySupport.createNew(fileUnversioned);

        //delete
        delete(fileA);
        assertFalse(fileA.exists());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fileA).getTextStatus());
        assertEquals(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, getStatus(fileA));
        // move
        moveFO(fileUnversioned, fileA);

        // test
        assertTrue(fileA.exists());
        assertFalse(fileUnversioned.exists());
        assertEquals(SVNStatusKind.NORMAL, getSVNStatus(fileA).getTextStatus());
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, getStatus(fileA));
    }
    
    public void moveVersionedFile2Repos_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFolder = VCSFileProxy.createFileProxy(wc, "folder");
        VCSFileProxySupport.mkdirs(fromFolder);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(wc2, "toFolder");
        VCSFileProxySupport.mkdirs(toFolder);
        commit(wc2);
        VCSFileProxy fromFile = VCSFileProxy.createFileProxy(fromFolder, "file");
        VCSFileProxySupport.createNew(fromFile);
        commit(wc);
        VCSFileProxy toFile = VCSFileProxy.createFileProxy(toFolder, "file");
        // move
        moveFO(fromFile, toFile);

        // test
        assertFalse(fromFile.exists());
        assertTrue(toFile.exists());

        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile).getTextStatus());

        assertCachedStatus(fromFile, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(toFile, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);

//        commit(wc);
//        commit(wc2);
    }

    public void moveVersionedFolder2Repos_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFolder = VCSFileProxy.createFileProxy(wc, "folder");
        VCSFileProxySupport.mkdirs(fromFolder);
        VCSFileProxy toFolderParent = VCSFileProxy.createFileProxy(wc2, "folderParent");
        VCSFileProxySupport.mkdirs(toFolderParent);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(toFolderParent, fromFolder.getName());
        VCSFileProxy toFile = VCSFileProxy.createFileProxy(toFolder, "file");
        commit(wc2);
        VCSFileProxy fromFile = VCSFileProxy.createFileProxy(fromFolder, toFile.getName());
        VCSFileProxySupport.createNew(fromFile);
        commit(wc);
        // move
        moveFO(fromFolder, toFolder);

        // test
        assertFalse(fromFolder.exists()); // TODO later delete from folder
        assertFalse(fromFile.exists());
        assertTrue(toFolder.exists());
        assertTrue(toFile.exists());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile).getTextStatus());
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(toFolder).getTextStatus());

        assertCachedStatus(fromFolder, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(toFile, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);
        assertCachedStatus(toFolder, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);

//        commit(wc);
//        commit(wc2);
    }

    public void moveFileTree2Repos_FO() throws Exception {
        if (skipTest()) {
            return;
        }
        // init
        VCSFileProxy fromFolder = VCSFileProxy.createFileProxy(wc, "folder");
        VCSFileProxySupport.mkdirs(fromFolder);
        VCSFileProxy fromFolder1 = VCSFileProxy.createFileProxy(fromFolder, "folder1");
        VCSFileProxySupport.mkdirs(fromFolder1);
        VCSFileProxy fromFolder2 = VCSFileProxy.createFileProxy(fromFolder, "folder2");
        VCSFileProxySupport.mkdirs(fromFolder2);
        VCSFileProxy fromFile11 = VCSFileProxy.createFileProxy(fromFolder1, "file11");
        VCSFileProxySupport.createNew(fromFile11);
        VCSFileProxy fromFile12 = VCSFileProxy.createFileProxy(fromFolder1, "file12");
        VCSFileProxySupport.createNew(fromFile12);
        VCSFileProxy fromFile21 = VCSFileProxy.createFileProxy(fromFolder2, "file21");
        VCSFileProxySupport.createNew(fromFile21);
        VCSFileProxy fromFile22 = VCSFileProxy.createFileProxy(fromFolder2, "file22");
        VCSFileProxySupport.createNew(fromFile22);

        VCSFileProxy toFolderParent = VCSFileProxy.createFileProxy(wc2, "toFolder");
        VCSFileProxySupport.mkdirs(toFolderParent);
        VCSFileProxy toFolder = VCSFileProxy.createFileProxy(toFolderParent, fromFolder.getName());
        commit(wc);
        commit(wc2);

        // move
        moveFO(fromFolder, toFolder);

//        // test         t.
        assertFalse(fromFolder.exists());
        assertTrue(toFolder.exists());
        VCSFileProxy toFolder1 = VCSFileProxy.createFileProxy(toFolder, fromFolder1.getName());
        assertTrue(toFolder1.exists());
        VCSFileProxy toFolder2 = VCSFileProxy.createFileProxy(toFolder, fromFolder2.getName());
        assertTrue(toFolder2.exists());
        VCSFileProxy toFile11 = VCSFileProxy.createFileProxy(toFolder1, "file11");
        assertTrue(toFile11.exists());
        VCSFileProxy toFile12 = VCSFileProxy.createFileProxy(toFolder1, "file12");
        assertTrue(toFile12.exists());
        VCSFileProxy toFile21 = VCSFileProxy.createFileProxy(toFolder2, "file21");
        assertTrue(toFile21.exists());
        VCSFileProxy toFile22 = VCSFileProxy.createFileProxy(toFolder2, "file22");
        assertTrue(toFile22.exists());

        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder1).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFolder2).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile11).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile12).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile21).getTextStatus());
        assertEquals(SVNStatusKind.DELETED, getSVNStatus(fromFile22).getTextStatus());
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(toFolder).getTextStatus());
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(toFolder1).getTextStatus());
        assertEquals(SVNStatusKind.ADDED, getSVNStatus(toFolder2).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile11).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile12).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile21).getTextStatus());
        assertEquals(SVNStatusKind.UNVERSIONED, getSVNStatus(toFile22).getTextStatus());

        assertCachedStatus(fromFolder, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(fromFolder1, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(fromFolder2, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(fromFile11, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(fromFile12, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(fromFile21, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(fromFile22, FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY);
        assertCachedStatus(toFolder, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);
        assertCachedStatus(toFolder1, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);
        assertCachedStatus(toFolder2, FileInformation.STATUS_VERSIONED_ADDEDLOCALLY);
        assertCachedStatus(toFile11, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);
        assertCachedStatus(toFile12, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);
        assertCachedStatus(toFile21, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);
        assertCachedStatus(toFile22, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);

        commit(wc);
        commit(wc2);

        assertFalse(fromFolder.exists());
        assertFalse(fromFolder1.exists());
        assertFalse(fromFolder2.exists());
        assertFalse(fromFile11.exists());
        assertFalse(fromFile12.exists());
        assertFalse(fromFile21.exists());
        assertFalse(fromFile22.exists());

    }
}
