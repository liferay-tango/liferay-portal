/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.segments.asah.connector.internal.expression;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.asah.connector.internal.expression.parser.IndividualSegmentsExpressionBaseVisitor;
import com.liferay.segments.asah.connector.internal.expression.parser.IndividualSegmentsExpressionLexer;
import com.liferay.segments.asah.connector.internal.expression.parser.IndividualSegmentsExpressionParser;

import java.util.Objects;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * @author Cristina González
 */
public class JSONObjectIndividualSegmentsExpressionVisitorImpl
	extends IndividualSegmentsExpressionBaseVisitor<Object> {

	@Override
	public JSONObject visitAndExpression(
		@NotNull IndividualSegmentsExpressionParser.AndExpressionContext
			andExpressionContext) {

		return _getConjunctionJSONObject(
			"and",
			(JSONObject)visitChildren(
				(RuleNode)andExpressionContext.getChild(0)),
			(JSONObject)visitChildren(
				(RuleNode)andExpressionContext.getChild(2)));
	}

	@Override
	public Object visitBooleanParenthesis(
		@NotNull IndividualSegmentsExpressionParser.BooleanParenthesisContext
			booleanParenthesisContext) {

		ParseTree parseTree = booleanParenthesisContext.getChild(1);

		return parseTree.accept(this);
	}

	@Override
	public Object visitChildren(@NotNull RuleNode node) {
		Object result = defaultResult();

		for (int i = 0; i < node.getChildCount(); i++) {
			if (!shouldVisitNextChild(node, result)) {
				break;
			}

			ParseTree parseTree = node.getChild(i);

			Object object = parseTree.accept(this);

			result = aggregateResult(result, object);
		}

		return result;
	}

	@Override
	public JSONObject visitEqualsExpression(
		@NotNull IndividualSegmentsExpressionParser.EqualsExpressionContext
			equalsExpressionContext) {

		return _getOperationJSONObject(
			equalsExpressionContext.getChild(0),
			equalsExpressionContext.getChild(1),
			equalsExpressionContext.getChild(2));
	}

	@Override
	public JSONObject visitGreaterThanExpression(
		@NotNull IndividualSegmentsExpressionParser.GreaterThanExpressionContext
			greaterThanExpressionContext) {

		return _getOperationJSONObject(
			greaterThanExpressionContext.getChild(0),
			greaterThanExpressionContext.getChild(1),
			greaterThanExpressionContext.getChild(2));
	}

	@Override
	public JSONObject visitGreaterThanOrEqualsExpression(
		@NotNull
			IndividualSegmentsExpressionParser.
				GreaterThanOrEqualsExpressionContext
					greaterThanOrEqualsExpressionContext) {

		return _getOperationJSONObject(
			greaterThanOrEqualsExpressionContext.getChild(0),
			greaterThanOrEqualsExpressionContext.getChild(1),
			greaterThanOrEqualsExpressionContext.getChild(2));
	}

	@Override
	public JSONObject visitLessThanExpression(
		@NotNull IndividualSegmentsExpressionParser.LessThanExpressionContext
			lessThanExpressionContext) {

		return _getOperationJSONObject(
			lessThanExpressionContext.getChild(0),
			lessThanExpressionContext.getChild(1),
			lessThanExpressionContext.getChild(2));
	}

	@Override
	public JSONObject visitLessThanOrEqualsExpression(
		@NotNull
			IndividualSegmentsExpressionParser.LessThanOrEqualsExpressionContext
				lessThanOrEqualsExpressionContext) {

		return _getOperationJSONObject(
			lessThanOrEqualsExpressionContext.getChild(0),
			lessThanOrEqualsExpressionContext.getChild(1),
			lessThanOrEqualsExpressionContext.getChild(2));
	}

	@Override
	public JSONObject visitOrExpression(
		@NotNull IndividualSegmentsExpressionParser.OrExpressionContext
			orExpressionContext) {

		return _getConjunctionJSONObject(
			"or",
			(JSONObject)visitChildren(
				(RuleNode)orExpressionContext.getChild(0)),
			(JSONObject)visitChildren(
				(RuleNode)orExpressionContext.getChild(2)));
	}

	@Override
	public String visitStringLiteral(
		@NotNull IndividualSegmentsExpressionParser.StringLiteralContext
			stringLiteralContext) {

		return stringLiteralContext.getText();
	}

	@Override
	public String visitTerminal(TerminalNode terminalNode) {
		if (Objects.equals(terminalNode.getText(), "<EOF>")) {
			return null;
		}

		return terminalNode.getText();
	}

	@Override
	public JSONObject visitToFilterByCountExpression(
		@NotNull
			IndividualSegmentsExpressionParser.ToFilterByCountExpressionContext
				toFilterByCountExpressionContext) {

		IndividualSegmentsExpressionParser.FilterByCountExpressionContext
			filterByCountExpressionContext =
				(IndividualSegmentsExpressionParser.
					FilterByCountExpressionContext)
						toFilterByCountExpressionContext.getChild(0);

		Token token = filterByCountExpressionContext.filter;

		String filterString = token.getText();

		filterString = filterString.substring(1, filterString.length() - 1);

		filterString = filterString.replaceAll("''", "'");

		IndividualSegmentsExpressionParser individualSegmentsExpressionParser =
			new IndividualSegmentsExpressionParser(
				new CommonTokenStream(
					new IndividualSegmentsExpressionLexer(
						new ANTLRInputStream(filterString))));

		IndividualSegmentsExpressionParser.ExpressionContext expressionContext =
			individualSegmentsExpressionParser.expression();

		Token tokenValue = filterByCountExpressionContext.value;

		return JSONUtil.put(
			"operatorName",
			_getTokenToLowerCaseString(filterByCountExpressionContext.operator)
		).put(
			"propertyName", "event"
		).put(
			"propertyType", "count"
		).put(
			"query",
			(JSONObject)expressionContext.accept(
				new JSONObjectIndividualSegmentsExpressionVisitorImpl())
		).put(
			"value", tokenValue.getText()
		);
	}

	@Override
	protected Object aggregateResult(Object query, Object object) {
		if (query == null) {
			return object;
		}
		else if (object == null) {
			return query;
		}

		return object;
	}

	@Override
	protected JSONObject defaultResult() {
		return null;
	}

	private JSONObject _getConjunctionJSONObject(
		String operation, JSONObject leftJSONObject,
		JSONObject rightJSONObject) {

		String conjunctionName = leftJSONObject.getString("conjunctionName");

		_groupCount++;

		if (Validator.isNotNull(conjunctionName) &&
			Objects.equals(
				conjunctionName.toLowerCase(LocaleUtil.ROOT),
				operation.toLowerCase(LocaleUtil.ROOT))) {

			return JSONUtil.put(
				"conjunctionName", operation
			).put(
				"groupId", "group_" + _groupCount
			).put(
				"items",
				leftJSONObject.getJSONArray(
					"items"
				).put(
					rightJSONObject
				)
			);
		}

		return JSONUtil.put(
			"conjunctionName", StringUtil.lowerCase(String.valueOf(operation))
		).put(
			"groupId", "group_" + _groupCount
		).put(
			"items", JSONUtil.putAll(leftJSONObject, rightJSONObject)
		);
	}

	private JSONObject _getOperationJSONObject(
		ParseTree leftParseTree, ParseTree operatorParseTree,
		ParseTree rightParseTree) {

		return JSONUtil.put(
			"operatorName", operatorParseTree.accept(this)
		).put(
			"propertyName", leftParseTree.accept(this)
		).put(
			"value", rightParseTree.accept(this)
		);
	}

	private String _getTokenToLowerCaseString(Token token) {
		return StringUtil.lowerCase(token.getText());
	}

	private int _groupCount;

}