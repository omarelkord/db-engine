// Generated from D:/db-engine/src/main/java/gen\g.g4 by ANTLR 4.12.0
package gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class gParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, INT=20, DIGIT=21, DAY=22, DAY1=23, MONTH=24, OPERATOR=25, 
		LOGICAL_OP=26, DOUBLE=27, STRING=28, WS=29, NEWLINE=30;
	public static final int
		RULE_prog = 0, RULE_createTable = 1, RULE_createIndex = 2, RULE_insert = 3, 
		RULE_update = 4, RULE_delete = 5, RULE_condition = 6, RULE_selection = 7, 
		RULE_columns = 8, RULE_values = 9, RULE_statement = 10, RULE_object = 11, 
		RULE_date = 12, RULE_year = 13, RULE_month = 14, RULE_day = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "createTable", "createIndex", "insert", "update", "delete", "condition", 
			"selection", "columns", "values", "statement", "object", "date", "year", 
			"month", "day"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'create table'", "'('", "')'", "'create index'", "'on'", "'insert into'", 
			"'values'", "'update'", "'set'", "'where'", "'delete from'", "'select'", 
			"'*'", "'from'", "','", "'-'", "'0'", "'1'", "'3'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "INT", "DIGIT", "DAY", 
			"DAY1", "MONTH", "OPERATOR", "LOGICAL_OP", "DOUBLE", "STRING", "WS", 
			"NEWLINE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "g.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public gParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(gParser.EOF, 0); }
		public List<CreateTableContext> createTable() {
			return getRuleContexts(CreateTableContext.class);
		}
		public CreateTableContext createTable(int i) {
			return getRuleContext(CreateTableContext.class,i);
		}
		public List<CreateIndexContext> createIndex() {
			return getRuleContexts(CreateIndexContext.class);
		}
		public CreateIndexContext createIndex(int i) {
			return getRuleContext(CreateIndexContext.class,i);
		}
		public List<InsertContext> insert() {
			return getRuleContexts(InsertContext.class);
		}
		public InsertContext insert(int i) {
			return getRuleContext(InsertContext.class,i);
		}
		public List<UpdateContext> update() {
			return getRuleContexts(UpdateContext.class);
		}
		public UpdateContext update(int i) {
			return getRuleContext(UpdateContext.class,i);
		}
		public List<DeleteContext> delete() {
			return getRuleContexts(DeleteContext.class);
		}
		public DeleteContext delete(int i) {
			return getRuleContext(DeleteContext.class,i);
		}
		public List<SelectionContext> selection() {
			return getRuleContexts(SelectionContext.class);
		}
		public SelectionContext selection(int i) {
			return getRuleContext(SelectionContext.class,i);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(38);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(32);
					createTable();
					}
					break;
				case T__3:
					{
					setState(33);
					createIndex();
					}
					break;
				case T__5:
					{
					setState(34);
					insert();
					}
					break;
				case T__7:
					{
					setState(35);
					update();
					}
					break;
				case T__10:
					{
					setState(36);
					delete();
					}
					break;
				case T__11:
					{
					setState(37);
					selection();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(40); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 6482L) != 0) );
			setState(42);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CreateTableContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public CreateTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterCreateTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitCreateTable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitCreateTable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateTableContext createTable() throws RecognitionException {
		CreateTableContext _localctx = new CreateTableContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_createTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(T__0);
			setState(45);
			match(STRING);
			setState(46);
			match(T__1);
			setState(47);
			columns();
			setState(48);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CreateIndexContext extends ParserRuleContext {
		public List<TerminalNode> STRING() { return getTokens(gParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(gParser.STRING, i);
		}
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public CreateIndexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createIndex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterCreateIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitCreateIndex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitCreateIndex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateIndexContext createIndex() throws RecognitionException {
		CreateIndexContext _localctx = new CreateIndexContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_createIndex);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(T__3);
			setState(51);
			match(STRING);
			setState(52);
			match(T__4);
			setState(53);
			match(STRING);
			setState(54);
			match(T__1);
			setState(55);
			columns();
			setState(56);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InsertContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public ValuesContext values() {
			return getRuleContext(ValuesContext.class,0);
		}
		public InsertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterInsert(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitInsert(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitInsert(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InsertContext insert() throws RecognitionException {
		InsertContext _localctx = new InsertContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_insert);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(T__5);
			setState(59);
			match(STRING);
			setState(60);
			match(T__1);
			setState(61);
			columns();
			setState(62);
			match(T__2);
			setState(63);
			match(T__6);
			setState(64);
			match(T__1);
			setState(65);
			values();
			setState(66);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UpdateContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public List<ConditionContext> condition() {
			return getRuleContexts(ConditionContext.class);
		}
		public ConditionContext condition(int i) {
			return getRuleContext(ConditionContext.class,i);
		}
		public UpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterUpdate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitUpdate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitUpdate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UpdateContext update() throws RecognitionException {
		UpdateContext _localctx = new UpdateContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_update);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(T__7);
			setState(69);
			match(STRING);
			setState(70);
			match(T__8);
			setState(71);
			condition();
			setState(72);
			match(T__9);
			setState(73);
			condition();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeleteContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public DeleteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterDelete(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitDelete(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitDelete(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeleteContext delete() throws RecognitionException {
		DeleteContext _localctx = new DeleteContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_delete);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(T__10);
			setState(76);
			match(STRING);
			setState(77);
			match(T__9);
			setState(78);
			condition();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode LOGICAL_OP() { return getToken(gParser.LOGICAL_OP, 0); }
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_condition);
		try {
			setState(85);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(80);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(81);
				statement();
				setState(82);
				match(LOGICAL_OP);
				setState(83);
				statement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectionContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public SelectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterSelection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitSelection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitSelection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectionContext selection() throws RecognitionException {
		SelectionContext _localctx = new SelectionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_selection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(T__11);
			setState(88);
			match(T__12);
			setState(89);
			match(T__13);
			setState(90);
			match(STRING);
			setState(91);
			match(T__9);
			setState(92);
			condition();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColumnsContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public ColumnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterColumns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitColumns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitColumns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnsContext columns() throws RecognitionException {
		ColumnsContext _localctx = new ColumnsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_columns);
		try {
			setState(98);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(94);
				match(STRING);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(95);
				match(STRING);
				setState(96);
				match(T__14);
				setState(97);
				columns();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ValuesContext extends ParserRuleContext {
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public ValuesContext values() {
			return getRuleContext(ValuesContext.class,0);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_values);
		try {
			setState(105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(100);
				object();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(101);
				object();
				setState(102);
				match(T__14);
				setState(103);
				values();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public TerminalNode OPERATOR() { return getToken(gParser.OPERATOR, 0); }
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(STRING);
			setState(108);
			match(OPERATOR);
			setState(109);
			object();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(gParser.INT, 0); }
		public TerminalNode DOUBLE() { return getToken(gParser.DOUBLE, 0); }
		public TerminalNode STRING() { return getToken(gParser.STRING, 0); }
		public DateContext date() {
			return getRuleContext(DateContext.class,0);
		}
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_object);
		try {
			setState(115);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(111);
				match(INT);
				}
				break;
			case DOUBLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				match(DOUBLE);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(113);
				match(STRING);
				}
				break;
			case DIGIT:
				enterOuterAlt(_localctx, 4);
				{
				setState(114);
				date();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DateContext extends ParserRuleContext {
		public YearContext year() {
			return getRuleContext(YearContext.class,0);
		}
		public MonthContext month() {
			return getRuleContext(MonthContext.class,0);
		}
		public DayContext day() {
			return getRuleContext(DayContext.class,0);
		}
		public DateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_date; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterDate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitDate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitDate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DateContext date() throws RecognitionException {
		DateContext _localctx = new DateContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_date);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			year();
			setState(118);
			match(T__15);
			setState(119);
			month();
			setState(120);
			match(T__15);
			setState(121);
			day();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class YearContext extends ParserRuleContext {
		public List<TerminalNode> DIGIT() { return getTokens(gParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(gParser.DIGIT, i);
		}
		public YearContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_year; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterYear(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitYear(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitYear(this);
			else return visitor.visitChildren(this);
		}
	}

	public final YearContext year() throws RecognitionException {
		YearContext _localctx = new YearContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_year);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(DIGIT);
			setState(124);
			match(DIGIT);
			setState(125);
			match(DIGIT);
			setState(126);
			match(DIGIT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MonthContext extends ParserRuleContext {
		public TerminalNode DIGIT() { return getToken(gParser.DIGIT, 0); }
		public TerminalNode MONTH() { return getToken(gParser.MONTH, 0); }
		public MonthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_month; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterMonth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitMonth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitMonth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MonthContext month() throws RecognitionException {
		MonthContext _localctx = new MonthContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_month);
		try {
			setState(132);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__16:
				enterOuterAlt(_localctx, 1);
				{
				setState(128);
				match(T__16);
				setState(129);
				match(DIGIT);
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				match(T__17);
				setState(131);
				match(MONTH);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DayContext extends ParserRuleContext {
		public TerminalNode DIGIT() { return getToken(gParser.DIGIT, 0); }
		public TerminalNode DAY() { return getToken(gParser.DAY, 0); }
		public TerminalNode DAY1() { return getToken(gParser.DAY1, 0); }
		public DayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_day; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).enterDay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gListener ) ((gListener)listener).exitDay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gVisitor ) return ((gVisitor<? extends T>)visitor).visitDay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DayContext day() throws RecognitionException {
		DayContext _localctx = new DayContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_day);
		try {
			setState(140);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__16:
				enterOuterAlt(_localctx, 1);
				{
				setState(134);
				match(T__16);
				setState(135);
				match(DIGIT);
				}
				break;
			case DAY:
				enterOuterAlt(_localctx, 2);
				{
				setState(136);
				match(DAY);
				setState(137);
				match(DIGIT);
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 3);
				{
				setState(138);
				match(T__18);
				setState(139);
				match(DAY1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u001e\u008f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0004\u0000\'\b\u0000\u000b\u0000\f\u0000(\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006"+
		"V\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bc\b\b"+
		"\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\tj\b\t\u0001\n\u0001\n"+
		"\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003"+
		"\u000bt\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u0085\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u008d\b\u000f\u0001\u000f"+
		"\u0000\u0000\u0010\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0016\u0018\u001a\u001c\u001e\u0000\u0000\u008d\u0000&\u0001\u0000\u0000"+
		"\u0000\u0002,\u0001\u0000\u0000\u0000\u00042\u0001\u0000\u0000\u0000\u0006"+
		":\u0001\u0000\u0000\u0000\bD\u0001\u0000\u0000\u0000\nK\u0001\u0000\u0000"+
		"\u0000\fU\u0001\u0000\u0000\u0000\u000eW\u0001\u0000\u0000\u0000\u0010"+
		"b\u0001\u0000\u0000\u0000\u0012i\u0001\u0000\u0000\u0000\u0014k\u0001"+
		"\u0000\u0000\u0000\u0016s\u0001\u0000\u0000\u0000\u0018u\u0001\u0000\u0000"+
		"\u0000\u001a{\u0001\u0000\u0000\u0000\u001c\u0084\u0001\u0000\u0000\u0000"+
		"\u001e\u008c\u0001\u0000\u0000\u0000 \'\u0003\u0002\u0001\u0000!\'\u0003"+
		"\u0004\u0002\u0000\"\'\u0003\u0006\u0003\u0000#\'\u0003\b\u0004\u0000"+
		"$\'\u0003\n\u0005\u0000%\'\u0003\u000e\u0007\u0000& \u0001\u0000\u0000"+
		"\u0000&!\u0001\u0000\u0000\u0000&\"\u0001\u0000\u0000\u0000&#\u0001\u0000"+
		"\u0000\u0000&$\u0001\u0000\u0000\u0000&%\u0001\u0000\u0000\u0000\'(\u0001"+
		"\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000"+
		")*\u0001\u0000\u0000\u0000*+\u0005\u0000\u0000\u0001+\u0001\u0001\u0000"+
		"\u0000\u0000,-\u0005\u0001\u0000\u0000-.\u0005\u001c\u0000\u0000./\u0005"+
		"\u0002\u0000\u0000/0\u0003\u0010\b\u000001\u0005\u0003\u0000\u00001\u0003"+
		"\u0001\u0000\u0000\u000023\u0005\u0004\u0000\u000034\u0005\u001c\u0000"+
		"\u000045\u0005\u0005\u0000\u000056\u0005\u001c\u0000\u000067\u0005\u0002"+
		"\u0000\u000078\u0003\u0010\b\u000089\u0005\u0003\u0000\u00009\u0005\u0001"+
		"\u0000\u0000\u0000:;\u0005\u0006\u0000\u0000;<\u0005\u001c\u0000\u0000"+
		"<=\u0005\u0002\u0000\u0000=>\u0003\u0010\b\u0000>?\u0005\u0003\u0000\u0000"+
		"?@\u0005\u0007\u0000\u0000@A\u0005\u0002\u0000\u0000AB\u0003\u0012\t\u0000"+
		"BC\u0005\u0003\u0000\u0000C\u0007\u0001\u0000\u0000\u0000DE\u0005\b\u0000"+
		"\u0000EF\u0005\u001c\u0000\u0000FG\u0005\t\u0000\u0000GH\u0003\f\u0006"+
		"\u0000HI\u0005\n\u0000\u0000IJ\u0003\f\u0006\u0000J\t\u0001\u0000\u0000"+
		"\u0000KL\u0005\u000b\u0000\u0000LM\u0005\u001c\u0000\u0000MN\u0005\n\u0000"+
		"\u0000NO\u0003\f\u0006\u0000O\u000b\u0001\u0000\u0000\u0000PV\u0003\u0014"+
		"\n\u0000QR\u0003\u0014\n\u0000RS\u0005\u001a\u0000\u0000ST\u0003\u0014"+
		"\n\u0000TV\u0001\u0000\u0000\u0000UP\u0001\u0000\u0000\u0000UQ\u0001\u0000"+
		"\u0000\u0000V\r\u0001\u0000\u0000\u0000WX\u0005\f\u0000\u0000XY\u0005"+
		"\r\u0000\u0000YZ\u0005\u000e\u0000\u0000Z[\u0005\u001c\u0000\u0000[\\"+
		"\u0005\n\u0000\u0000\\]\u0003\f\u0006\u0000]\u000f\u0001\u0000\u0000\u0000"+
		"^c\u0005\u001c\u0000\u0000_`\u0005\u001c\u0000\u0000`a\u0005\u000f\u0000"+
		"\u0000ac\u0003\u0010\b\u0000b^\u0001\u0000\u0000\u0000b_\u0001\u0000\u0000"+
		"\u0000c\u0011\u0001\u0000\u0000\u0000dj\u0003\u0016\u000b\u0000ef\u0003"+
		"\u0016\u000b\u0000fg\u0005\u000f\u0000\u0000gh\u0003\u0012\t\u0000hj\u0001"+
		"\u0000\u0000\u0000id\u0001\u0000\u0000\u0000ie\u0001\u0000\u0000\u0000"+
		"j\u0013\u0001\u0000\u0000\u0000kl\u0005\u001c\u0000\u0000lm\u0005\u0019"+
		"\u0000\u0000mn\u0003\u0016\u000b\u0000n\u0015\u0001\u0000\u0000\u0000"+
		"ot\u0005\u0014\u0000\u0000pt\u0005\u001b\u0000\u0000qt\u0005\u001c\u0000"+
		"\u0000rt\u0003\u0018\f\u0000so\u0001\u0000\u0000\u0000sp\u0001\u0000\u0000"+
		"\u0000sq\u0001\u0000\u0000\u0000sr\u0001\u0000\u0000\u0000t\u0017\u0001"+
		"\u0000\u0000\u0000uv\u0003\u001a\r\u0000vw\u0005\u0010\u0000\u0000wx\u0003"+
		"\u001c\u000e\u0000xy\u0005\u0010\u0000\u0000yz\u0003\u001e\u000f\u0000"+
		"z\u0019\u0001\u0000\u0000\u0000{|\u0005\u0015\u0000\u0000|}\u0005\u0015"+
		"\u0000\u0000}~\u0005\u0015\u0000\u0000~\u007f\u0005\u0015\u0000\u0000"+
		"\u007f\u001b\u0001\u0000\u0000\u0000\u0080\u0081\u0005\u0011\u0000\u0000"+
		"\u0081\u0085\u0005\u0015\u0000\u0000\u0082\u0083\u0005\u0012\u0000\u0000"+
		"\u0083\u0085\u0005\u0018\u0000\u0000\u0084\u0080\u0001\u0000\u0000\u0000"+
		"\u0084\u0082\u0001\u0000\u0000\u0000\u0085\u001d\u0001\u0000\u0000\u0000"+
		"\u0086\u0087\u0005\u0011\u0000\u0000\u0087\u008d\u0005\u0015\u0000\u0000"+
		"\u0088\u0089\u0005\u0016\u0000\u0000\u0089\u008d\u0005\u0015\u0000\u0000"+
		"\u008a\u008b\u0005\u0013\u0000\u0000\u008b\u008d\u0005\u0017\u0000\u0000"+
		"\u008c\u0086\u0001\u0000\u0000\u0000\u008c\u0088\u0001\u0000\u0000\u0000"+
		"\u008c\u008a\u0001\u0000\u0000\u0000\u008d\u001f\u0001\u0000\u0000\u0000"+
		"\b&(Ubis\u0084\u008c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}