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
package org.netbeans.modules.php.twig.editor.lexer;

import java.io.File;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.twig.editor.util.TestUtils;

/**
 * Tests for Twig top lexer.
 */
public class TwigTopLexerTest extends TwigLexerTestBase {

    public TwigTopLexerTest(String testName) {
        super(testName);
    }

    public void testHtmlBasic() throws Exception {
        performTest("html-basic.html");
    }

    @Override
    protected String getTestResult(String filename) throws Exception {
        String content = TestUtils.getFileContent(new File(getDataDir(), "testfiles/lexer/top/" + filename + ".twig"));
        Language<TwigTopTokenId> language = TwigTopTokenId.language();
        TokenHierarchy<?> hierarchy = TokenHierarchy.create(content, language);
        return createResult(hierarchy.tokenSequence(language));
    }

    private String createResult(TokenSequence<?> ts) throws Exception {
        StringBuilder result = new StringBuilder();
        while (ts.moveNext()) {
            TokenId tokenId = ts.token().id();
            CharSequence text = ts.token().text();
            result.append("token #");
            result.append(ts.index());
            result.append(" ");
            result.append(tokenId.name());
            String token = TestUtils.replaceLinesAndTabs(text.toString());
            if (!token.isEmpty()) {
                result.append(" ");
                result.append("[");
                result.append(token);
                result.append("]");
            }
            result.append("\n");
        }
        return result.toString();
    }

}
