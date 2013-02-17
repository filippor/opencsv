/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package au.com.bytecode.opencsv;

/**
 * Builder for creating a CSVParser.
 * <p/>
 * <code>
 * final CSVParser parser =
 * new CSVParserBuilder()
 * .withSeparator('\t')
 * .withIgnoreQuotations(true)
 * .build();
 * </code>
 *
 * @see CSVParser
 */
public class CSVParserBuilder {

    char separator = CSVParser.DEFAULT_SEPARATOR;
    String stringSeparator = null;
    char quoteChar = CSVParser.DEFAULT_QUOTE_CHARACTER;
    char escapeChar = CSVParser.DEFAULT_ESCAPE_CHARACTER;
    boolean strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;
    ;
    boolean ignoreLeadingWhiteSpace =
            CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE;
    boolean ignoreQuotations = CSVParser.DEFAULT_IGNORE_QUOTATIONS;
    
    boolean emptyUnquotedIsNull = CSVParser.DEFAULT_EMPTY_UNQUOTED_IS_NULL;
    /**
     * Sets the delimiter to use for separating entries
     *
     * @param separator the delimiter to use for separating entries
     */
    public CSVParserBuilder withSeparator(
            final char separator) {
        this.separator = separator;
        this.stringSeparator = null;
        return this;
    }
    
    /**
     * Sets the delimiter to use for separating entries
     *
     * @param separator the delimiter to use for separating entries
     */
    public CSVParserBuilder withSeparator(
            final String separator) {
        this.stringSeparator = separator;
        this.separator = 0;
        return this;
    }


    /**
     * Sets the character to use for quoted elements
     *
     * @param quotechar the character to use for quoted elements
     */
    public CSVParserBuilder withQuoteChar(
            final char quoteChar) {
        this.quoteChar = quoteChar;
        return this;
    }


    /**
     * Sets the character to use for escaping a separator or quote
     *
     * @param escape the character to use for escaping a separator or quote
     */
    public CSVParserBuilder withEscapeChar(
            final char escapeChar) {
        this.escapeChar = escapeChar;
        return this;
    }


    /**
     * Sets the strict quotes setting - if true, characters
     * outside the quotes are ignored
     *
     * @param strictQuotes if true, characters outside the quotes are ignored
     */
    public CSVParserBuilder withStrictQuotes(
            final boolean strictQuotes) {
        this.strictQuotes = strictQuotes;
        return this;
    }

    /**
     * Sets the ignore leading whitespace setting - if true, white space
     * in front of a quote in a field is ignored
     *
     * @param ignoreLeadingWhiteSpace if true, white space in front of a quote in a field is ignored
     */
    public CSVParserBuilder withIgnoreLeadingWhiteSpace(
            final boolean ignoreLeadingWhiteSpace) {
        this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;
        return this;
    }

    /**
     * Sets the ignore quotations mode - if true, quotations are ignored
     *
     * @param ignoreQuotations if true, quotations are ignored
     */
    public CSVParserBuilder withIgnoreQuotations(
            final boolean ignoreQuotations) {
        this.ignoreQuotations = ignoreQuotations;
        return this;
    }
    /**
     * Sets the empty unquoted is null - if true, empty unquoted field result is null
     *
     * @param ignoreQuotations if true, empty return null
     */
    public CSVParserBuilder withEmptyUnquotedIsNull(
    		final boolean emptyUnquotedIsNull) {
    	this.emptyUnquotedIsNull = emptyUnquotedIsNull;
    	return this;
    }

    /**
     * Constructs CSVParser
     */
    public CSVParser build() {
    	// Build using new style constructor if we have string separator
    	if (stringSeparator != null) {
    		return new CSVParser(
    				stringSeparator,
    				quoteChar,
    				escapeChar,
    				strictQuotes,
    				ignoreLeadingWhiteSpace,
    				ignoreQuotations,
    				emptyUnquotedIsNull);		
    	} else {
    		// Otherwise use the old style constructor
    		return new CSVParser(
    				String.valueOf(separator),
    				quoteChar,
    				escapeChar,
    				strictQuotes,
    				ignoreLeadingWhiteSpace,
    				ignoreQuotations,
    				emptyUnquotedIsNull);
    	}
    }
}
