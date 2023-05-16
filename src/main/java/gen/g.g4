grammar g;


prog: (createTable | createIndex | insert | update | delete | selection )+EOF;

createTable: 'create table' STRING  '('  columns  ')';
createIndex: 'create index' STRING 'on' STRING '(' columns ')';
insert: 'insert into' STRING '(' columns ')' 'values' '(' values ')';
update: 'update' STRING 'set' updateColumns 'where' condition;
delete: 'delete from' STRING 'where' condition;
condition: statement | statement LOGICAL_OP statement ;
selection: 'select' '*' 'from' STRING 'where' condition;
updateColumns : STRING  '='  object | STRING  '='  object ',' updateColumns;
columns: STRING | STRING ',' columns;
values: object | object ',' values;
statement: STRING OPERATOR object ;
object : INT | DOUBLE | STRING | date ;
date : year '-' month '-' day ;
year : DIGIT DIGIT DIGIT DIGIT ;
month : '0' DIGIT | '1' MONTH ;
day : '0' DIGIT | DAY DIGIT | '3' DAY1 ;

INT: '0' | [1-9][0-9]*;
DIGIT: [0-9];
DAY: [1-2];
DAY1: [0-1];
MONTH: [0-2];
OPERATOR : '=' | '>'| '<'|'>=' | '<=';
LOGICAL_OP : 'AND' | 'OR' | 'XOR';
DOUBLE : [0-9]+ '.' [0-9]+ ;
STRING : [a-z]*;
WS : [ \t\n\r]+ -> skip ;
NEWLINE:'\r'? '\n' ;








