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

package co.cask.directives.transformation;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.wrangler.api.Arguments;
import co.cask.wrangler.api.DirectiveExecutionException;
import co.cask.wrangler.api.DirectiveParseException;
import co.cask.wrangler.api.RecipeContext;
import co.cask.wrangler.api.Row;
import co.cask.wrangler.api.UDD;
import co.cask.wrangler.api.parser.ColumnName;
import co.cask.wrangler.api.parser.Numeric;
import co.cask.wrangler.api.parser.TokenType;
import co.cask.wrangler.api.parser.UsageDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * A directive for splitting a col into two additional columns based on a start and end.
 */
@Plugin(type = UDD.Type)
@Name(IndexSplit.NAME)
@Description("[DEPRECATED] Use the 'split-to-columns' or 'parse-as-fixed-length' directives instead.")
@Deprecated
public class IndexSplit implements UDD {
  public static final String NAME = "indexsplit";
  // Name of the column to be split
  private String col;

  // Start and end index of the split
  private int start, end;

  // Destination column
  private String dest;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("source", TokenType.COLUMN_NAME);
    builder.define("start", TokenType.NUMERIC);
    builder.define("end", TokenType.NUMERIC);
    builder.define("destination", TokenType.COLUMN_NAME);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    this.col = ((ColumnName) args.value("source")).value();
    this.start = ((Numeric) args.value("start")).value().intValue();
    this.end = ((Numeric) args.value("end")).value().intValue();
    this.dest = ((ColumnName) args.value("destination")).value();
    this.start = this.start - 1;
    this.end = this.end - 1;
  }

  @Override
  public List<Row> execute(List<Row> rows, RecipeContext context) throws DirectiveExecutionException {
    List<Row> results = new ArrayList<>();
    for (Row row : rows) {
      int idx = row.find(col);

      if (idx != -1) {
        String val = (String) row.getValue(idx);
        if (end > val.length() - 1) {
          end = val.length() - 1;
        }
        if (start < 0) {
          start = 0;
        }
        val = val.substring(start, end);
        row.add(dest, val);
      } else {
        throw new DirectiveExecutionException(
          col + " is not of type string in the row. Please check the wrangle configuration."
        );
      }
      results.add(row);
    }
    return results;
  }
}
