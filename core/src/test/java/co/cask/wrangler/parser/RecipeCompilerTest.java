/*
 *  Copyright © 2017 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package co.cask.wrangler.parser;

import co.cask.wrangler.api.CompileException;
import co.cask.wrangler.api.CompiledUnit;
import co.cask.wrangler.api.Compiler;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link RecipeCompiler}
 */
public class RecipeCompilerTest {

  private static final Compiler compiler = new RecipeCompiler();

  @Test
  public void testSuccessCompilation() throws Exception {
    try {
      Compiler compiler = new RecipeCompiler();
      CompiledUnit units = compiler.compile(
          "parse-as-csv :body ' ' true;\n"
        + "set-column :abc, :edf;\n"
        + "send-to-error exp:{ window < 10 } ;\n"
        + "parse-as-simple-date :col 'yyyy-mm-dd' :col 'test' :col2,:col4,:col9 10 exp:{test < 10};\n"
      );

      Assert.assertNotNull(units);
      Assert.assertEquals(4, units.size());
    } catch (CompileException e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testListSyntaxError() throws Exception {
    try {
      compiler.compile("merge a,b,;");
      Assert.assertTrue(compiler.hasErrors());
      Assert.assertEquals("line 1:7 - Error at token ',' Mismatched input ',' expecting ';'",
                          compiler.getSyntaxErrors().next().getMessage());

      compiler.compile("merge ,a,b;");
      Assert.assertTrue(compiler.hasErrors());
      Assert.assertEquals("line 1:6 - Error at token ',' Mismatched input ',' expecting ';'",
                          compiler.getSyntaxErrors().next().getMessage());

      compiler.compile("merge a,,b,;");
      Assert.assertTrue(compiler.hasErrors());
      Assert.assertEquals("line 1:7 - Error at token ',' Mismatched input ',' expecting ';'",
                          compiler.getSyntaxErrors().next().getMessage());

      System.out.println(compiler.getSyntaxErrors().next().getMessage());
    } catch (CompileException e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testStringSyntax() throws Exception {
    compiler.compile("merge :A :B :C '\\u00A';");
    Assert.assertTrue(compiler.hasErrors());
  }

  @Test
  public void testExpressionSyntaxError() throws Exception {
    compiler.compile("send-to-error exp 10 > 12;");
    Assert.assertTrue(compiler.hasErrors());
    Assert.assertEquals("line 1:18 - Error at token '10' Mismatched input '10' expecting ':'",
                        compiler.getSyntaxErrors().next().getMessage());

    compiler.compile("send-to-error exp:10 > 12;");
    Assert.assertTrue(compiler.hasErrors());
    Assert.assertEquals("line 1:18 - Error at token '10' Mismatched input '10' expecting '{'",
                        compiler.getSyntaxErrors().next().getMessage());

    compiler.compile("send-to-error exp:{10 > 12;");
    Assert.assertTrue(compiler.hasErrors());
    Assert.assertEquals("line 1:27 - Error at token '<EOF>' Mismatched input '<EOF>' expecting '}'",
                        compiler.getSyntaxErrors().next().getMessage());

    compiler.compile("send-to-error exp:{{10 > 12;");
    Assert.assertTrue(compiler.hasErrors());
    Assert.assertEquals("line 1:28 - Error at token '<EOF>' No viable alternative at input '{10>12;'",
                        compiler.getSyntaxErrors().next().getMessage());
  }
}