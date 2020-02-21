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

package org.netbeans.modules.subversion.remote.ui.blame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.editor.errorstripe.privatespi.Mark;
import org.netbeans.modules.editor.errorstripe.privatespi.MarkProvider;

/**
 * ErrorStripe liason, real work is done in AnnotationBar.
 *
 * 
 */
final class AnnotationMarkProvider extends MarkProvider {

    private List<Mark> marks = Collections.emptyList();

    public void setMarks(List<AnnotationMark> marks) {
        List<Mark> old = this.marks;
        this.marks = new ArrayList<Mark>(marks);        
        firePropertyChange(PROP_MARKS, old, marks);
    }

    @Override
    public List<Mark> getMarks() {
        return marks;
    }

}
